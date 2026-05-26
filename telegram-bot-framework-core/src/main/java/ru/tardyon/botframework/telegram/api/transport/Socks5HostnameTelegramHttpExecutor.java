package ru.tardyon.botframework.telegram.api.transport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public final class Socks5HostnameTelegramHttpExecutor implements TelegramHttpExecutor {

    private static final int SOCKS_VERSION = 5;
    private static final int SOCKS_AUTH_NONE = 0x00;
    private static final int SOCKS_AUTH_PASSWORD = 0x02;
    private static final int SOCKS_CMD_CONNECT = 0x01;
    private static final int SOCKS_ADDR_DOMAIN = 0x03;
    private static final int SOCKS_ADDR_IPV4 = 0x01;
    private static final int SOCKS_ADDR_IPV6 = 0x04;

    private final String proxyHost;
    private final int proxyPort;
    private final String username;
    private final String password;
    private final int connectTimeoutMillis;
    private final SocketFactory plainSocketFactory;
    private final SSLSocketFactory sslSocketFactory;

    public Socks5HostnameTelegramHttpExecutor(String proxyHost, int proxyPort, String username, String password) {
        this(proxyHost, proxyPort, username, password, Duration.ofSeconds(30), SocketFactory.getDefault(), (SSLSocketFactory) SSLSocketFactory.getDefault());
    }

    Socks5HostnameTelegramHttpExecutor(
        String proxyHost,
        int proxyPort,
        String username,
        String password,
        Duration connectTimeout,
        SocketFactory plainSocketFactory,
        SSLSocketFactory sslSocketFactory
    ) {
        this.proxyHost = requireText(proxyHost, "proxyHost");
        this.proxyPort = requirePort(proxyPort);
        this.username = username;
        this.password = password;
        this.connectTimeoutMillis = Math.toIntExact(Objects.requireNonNull(connectTimeout, "connectTimeout must not be null").toMillis());
        this.plainSocketFactory = Objects.requireNonNull(plainSocketFactory, "plainSocketFactory must not be null");
        this.sslSocketFactory = Objects.requireNonNull(sslSocketFactory, "sslSocketFactory must not be null");
    }

    @Override
    public TelegramHttpResponse execute(TelegramHttpRequest request) throws IOException {
        URI uri = request.uri();
        String host = requireText(uri.getHost(), "request host");
        int port = resolvePort(uri);
        try (Socket proxySocket = plainSocketFactory.createSocket()) {
            proxySocket.connect(new InetSocketAddress(proxyHost, proxyPort), connectTimeoutMillis);
            proxySocket.setSoTimeout(0);
            establishSocksTunnel(proxySocket, host, port);

            Socket requestSocket = "https".equalsIgnoreCase(uri.getScheme())
                ? sslSocketFactory.createSocket(proxySocket, host, port, true)
                : proxySocket;

            if (requestSocket != proxySocket) {
                requestSocket.setSoTimeout(0);
            }

            try (Socket socket = requestSocket) {
                writeHttpRequest(socket.getOutputStream(), request, host, port);
                return readHttpResponse(socket.getInputStream());
            }
        }
    }

    private void establishSocksTunnel(Socket socket, String targetHost, int targetPort) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

        if (hasCredentials()) {
            out.write(new byte[] {SOCKS_VERSION, 2, SOCKS_AUTH_NONE, SOCKS_AUTH_PASSWORD});
        } else {
            out.write(new byte[] {SOCKS_VERSION, 1, SOCKS_AUTH_NONE});
        }
        out.flush();

        byte[] greeting = readFully(in, 2);
        if (greeting[0] != SOCKS_VERSION) {
            throw new IOException("Invalid SOCKS5 greeting version: " + (greeting[0] & 0xff));
        }
        if ((greeting[1] & 0xff) == 0xff) {
            throw new IOException("SOCKS5 proxy rejected available authentication methods");
        }

        if ((greeting[1] & 0xff) == SOCKS_AUTH_PASSWORD) {
            authenticate(out, in);
        } else if ((greeting[1] & 0xff) != SOCKS_AUTH_NONE) {
            throw new IOException("Unsupported SOCKS5 authentication method: " + (greeting[1] & 0xff));
        }

        byte[] hostBytes = targetHost.getBytes(StandardCharsets.UTF_8);
        if (hostBytes.length == 0 || hostBytes.length > 255) {
            throw new IOException("SOCKS5 target host must be between 1 and 255 bytes");
        }

        out.write(SOCKS_VERSION);
        out.write(SOCKS_CMD_CONNECT);
        out.write(0);
        out.write(SOCKS_ADDR_DOMAIN);
        out.write(hostBytes.length);
        out.write(hostBytes);
        out.write((targetPort >> 8) & 0xff);
        out.write(targetPort & 0xff);
        out.flush();

        byte[] replyHeader = readFully(in, 4);
        if (replyHeader[0] != SOCKS_VERSION) {
            throw new IOException("Invalid SOCKS5 reply version: " + (replyHeader[0] & 0xff));
        }
        if (replyHeader[1] != 0x00) {
            throw new IOException("SOCKS5 connect failed with status: " + (replyHeader[1] & 0xff));
        }

        skipAddress(in, replyHeader[3] & 0xff);
        readFully(in, 2);
    }

    private void authenticate(OutputStream out, InputStream in) throws IOException {
        byte[] userBytes = requireText(username, "proxy username").getBytes(StandardCharsets.UTF_8);
        byte[] passwordBytes = (password == null ? "" : password).getBytes(StandardCharsets.UTF_8);
        if (userBytes.length == 0 || userBytes.length > 255) {
            throw new IOException("SOCKS5 proxy username must be between 1 and 255 bytes");
        }
        if (passwordBytes.length > 255) {
            throw new IOException("SOCKS5 proxy password must be at most 255 bytes");
        }

        out.write(0x01);
        out.write(userBytes.length);
        out.write(userBytes);
        out.write(passwordBytes.length);
        out.write(passwordBytes);
        out.flush();

        byte[] authReply = readFully(in, 2);
        if (authReply[1] != 0x00) {
            throw new IOException("SOCKS5 authentication failed with status: " + (authReply[1] & 0xff));
        }
    }

    private static void writeHttpRequest(OutputStream outputStream, TelegramHttpRequest request, String host, int port) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        URI uri = request.uri();
        String path = uri.getRawPath();
        if (path == null || path.isBlank()) {
            path = "/";
        }
        if (uri.getRawQuery() != null && !uri.getRawQuery().isBlank()) {
            path = path + "?" + uri.getRawQuery();
        }

        StringBuilder requestLine = new StringBuilder()
            .append(request.method())
            .append(' ')
            .append(path)
            .append(" HTTP/1.1\r\n");
        out.write(requestLine.toString().getBytes(StandardCharsets.US_ASCII));
        out.write(("Host: " + hostHeader(host, port, uri.getScheme()) + "\r\n").getBytes(StandardCharsets.US_ASCII));
        out.write("Connection: close\r\n".getBytes(StandardCharsets.US_ASCII));

        boolean hasContentLength = false;
        for (Map.Entry<String, List<String>> header : request.headers().entrySet()) {
            if ("host".equalsIgnoreCase(header.getKey()) || "connection".equalsIgnoreCase(header.getKey())) {
                continue;
            }
            if ("content-length".equalsIgnoreCase(header.getKey())) {
                hasContentLength = true;
            }
            for (String value : header.getValue()) {
                out.write((header.getKey() + ": " + value + "\r\n").getBytes(StandardCharsets.ISO_8859_1));
            }
        }

        if (!hasContentLength) {
            out.write(("Content-Length: " + request.body().length + "\r\n").getBytes(StandardCharsets.US_ASCII));
        }
        out.write("\r\n".getBytes(StandardCharsets.US_ASCII));
        out.write(request.body());
        out.flush();
    }

    private static TelegramHttpResponse readHttpResponse(InputStream inputStream) throws IOException {
        BufferedInputStream in = new BufferedInputStream(inputStream);
        String statusLine = readLine(in);
        if (statusLine == null || statusLine.isBlank()) {
            throw new EOFException("Unexpected end of stream while reading HTTP status line");
        }
        String[] parts = statusLine.split(" ", 3);
        if (parts.length < 2) {
            throw new IOException("Malformed HTTP status line: " + statusLine);
        }
        int statusCode = Integer.parseInt(parts[1]);

        Map<String, List<String>> headers = new LinkedHashMap<>();
        String line;
        while ((line = readLine(in)) != null && !line.isEmpty()) {
            int separator = line.indexOf(':');
            if (separator <= 0) {
                continue;
            }
            String name = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            headers.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
        }

        byte[] body = readBody(in, headers);
        return new TelegramHttpResponse(statusCode, headers, body);
    }

    private static byte[] readBody(InputStream in, Map<String, List<String>> headers) throws IOException {
        String transferEncoding = firstHeader(headers, "Transfer-Encoding");
        if (transferEncoding != null && transferEncoding.toLowerCase(Locale.ROOT).contains("chunked")) {
            return readChunkedBody(in);
        }

        String contentLength = firstHeader(headers, "Content-Length");
        if (contentLength != null) {
            int length = Integer.parseInt(contentLength.trim());
            return readFully(in, length);
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        in.transferTo(buffer);
        return buffer.toByteArray();
    }

    private static byte[] readChunkedBody(InputStream in) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        while (true) {
            String sizeLine = readLine(in);
            if (sizeLine == null) {
                throw new EOFException("Unexpected end of stream while reading chunk size");
            }
            int separator = sizeLine.indexOf(';');
            String sizeValue = separator >= 0 ? sizeLine.substring(0, separator) : sizeLine;
            int chunkSize = Integer.parseInt(sizeValue.trim(), 16);
            if (chunkSize == 0) {
                while (true) {
                    String trailer = readLine(in);
                    if (trailer == null || trailer.isEmpty()) {
                        return body.toByteArray();
                    }
                }
            }
            body.write(readFully(in, chunkSize));
            expectCrlf(in);
        }
    }

    private static void expectCrlf(InputStream in) throws IOException {
        int cr = in.read();
        int lf = in.read();
        if (cr != '\r' || lf != '\n') {
            throw new IOException("Malformed HTTP chunk framing");
        }
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int current;
        boolean seenCarriageReturn = false;
        while ((current = in.read()) != -1) {
            if (seenCarriageReturn) {
                if (current == '\n') {
                    return line.toString(StandardCharsets.ISO_8859_1);
                }
                line.write('\r');
                seenCarriageReturn = false;
            }
            if (current == '\r') {
                seenCarriageReturn = true;
            } else {
                line.write(current);
            }
        }
        if (seenCarriageReturn) {
            line.write('\r');
        }
        if (line.size() == 0) {
            return null;
        }
        return line.toString(StandardCharsets.ISO_8859_1);
    }

    private static byte[] readFully(InputStream in, int length) throws IOException {
        byte[] result = new byte[length];
        int offset = 0;
        while (offset < length) {
            int read = in.read(result, offset, length - offset);
            if (read < 0) {
                throw new EOFException("Unexpected end of stream");
            }
            offset += read;
        }
        return result;
    }

    private static void skipAddress(InputStream in, int addressType) throws IOException {
        switch (addressType) {
            case SOCKS_ADDR_IPV4 -> readFully(in, 4);
            case SOCKS_ADDR_IPV6 -> readFully(in, 16);
            case SOCKS_ADDR_DOMAIN -> {
                int domainLength = in.read();
                if (domainLength < 0) {
                    throw new EOFException("Unexpected end of stream while reading SOCKS5 domain length");
                }
                readFully(in, domainLength);
            }
            default -> throw new IOException("Unsupported SOCKS5 address type: " + addressType);
        }
    }

    private static String firstHeader(Map<String, List<String>> headers, String name) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey()) && !entry.getValue().isEmpty()) {
                return entry.getValue().getFirst();
            }
        }
        return null;
    }

    private static String hostHeader(String host, int port, String scheme) {
        boolean defaultPort = ("https".equalsIgnoreCase(scheme) && port == 443) || ("http".equalsIgnoreCase(scheme) && port == 80);
        return defaultPort ? host : host + ":" + port;
    }

    private boolean hasCredentials() {
        return username != null && !username.isBlank();
    }

    private static int resolvePort(URI uri) {
        if (uri.getPort() > 0) {
            return uri.getPort();
        }
        if ("https".equalsIgnoreCase(uri.getScheme())) {
            return 443;
        }
        if ("http".equalsIgnoreCase(uri.getScheme())) {
            return 80;
        }
        throw new IllegalArgumentException("Unsupported URI scheme: " + uri.getScheme());
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static int requirePort(int port) {
        if (port <= 0 || port > 65_535) {
            throw new IllegalArgumentException("proxyPort must be in range 1..65535");
        }
        return port;
    }
}
