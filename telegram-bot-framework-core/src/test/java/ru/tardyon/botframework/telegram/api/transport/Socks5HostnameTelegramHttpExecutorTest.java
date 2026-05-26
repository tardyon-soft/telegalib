package ru.tardyon.botframework.telegram.api.transport;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.junit.jupiter.api.Test;

class Socks5HostnameTelegramHttpExecutorTest {

    @Test
    void sendsHttpRequestThroughSocksProxyUsingDomainAddressAndPasswordAuth() throws Exception {
        try (ServerSocket targetServer = new ServerSocket(0); ServerSocket proxyServer = new ServerSocket(0)) {
            CountDownLatch completed = new CountDownLatch(2);
            AtomicReference<String> requestedHost = new AtomicReference<>();
            AtomicReference<byte[]> requestBody = new AtomicReference<>();
            AtomicReference<Throwable> failure = new AtomicReference<>();

            Thread targetThread = new Thread(() -> serveHttpTarget(targetServer, requestBody, completed, failure));
            targetThread.start();

            Thread proxyThread = new Thread(() -> serveSocksProxy(proxyServer, targetServer.getLocalPort(), requestedHost, completed, failure));
            proxyThread.start();

            Socks5HostnameTelegramHttpExecutor executor = new Socks5HostnameTelegramHttpExecutor(
                "127.0.0.1",
                proxyServer.getLocalPort(),
                "socksuser",
                "secret",
                Duration.ofSeconds(5),
                SocketFactory.getDefault(),
                (SSLSocketFactory) SSLSocketFactory.getDefault()
            );

            TelegramHttpResponse response = executor.execute(new TelegramHttpRequest(
                "POST",
                URI.create("http://api.telegram.org/test/path?x=1"),
                Map.of("Content-Type", List.of("application/json")),
                "{\"ok\":true}".getBytes(StandardCharsets.UTF_8)
            ));

            assertEquals(200, response.statusCode());
            assertArrayEquals("response-body".getBytes(StandardCharsets.UTF_8), response.body());
            assertEquals("api.telegram.org", requestedHost.get());
            assertEquals("{\"ok\":true}", new String(requestBody.get(), StandardCharsets.UTF_8));
            assertTrue(completed.await(5, TimeUnit.SECONDS));
            if (failure.get() != null) {
                throw new AssertionError(failure.get());
            }
        }
    }

    private static void serveHttpTarget(
        ServerSocket serverSocket,
        AtomicReference<byte[]> requestBody,
        CountDownLatch completed,
        AtomicReference<Throwable> failure
    ) {
        try (Socket socket = serverSocket.accept()) {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            String requestLine = readLine(in);
            assertTrue(requestLine.startsWith("POST /test/path?x=1 HTTP/1.1"));

            int contentLength = 0;
            String line;
            while ((line = readLine(in)) != null && !line.isEmpty()) {
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
                }
            }
            requestBody.set(readFully(in, contentLength));

            byte[] responseBody = "response-body".getBytes(StandardCharsets.UTF_8);
            out.write(("HTTP/1.1 200 OK\r\nContent-Length: " + responseBody.length + "\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
            out.write(responseBody);
            out.flush();
        } catch (Exception e) {
            failure.compareAndSet(null, e);
        } finally {
            completed.countDown();
        }
    }

    private static void serveSocksProxy(
        ServerSocket serverSocket,
        int targetPort,
        AtomicReference<String> requestedHost,
        CountDownLatch completed,
        AtomicReference<Throwable> failure
    ) {
        try (Socket clientSocket = serverSocket.accept()) {
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();

            int version = in.read();
            int methods = in.read();
            assertEquals(5, version);
            assertEquals(2, methods);
            readFully(in, methods);
            out.write(new byte[] {0x05, 0x02});
            out.flush();

            assertEquals(0x01, in.read());
            int usernameLength = in.read();
            assertEquals("socksuser", new String(readFully(in, usernameLength), StandardCharsets.UTF_8));
            int passwordLength = in.read();
            assertEquals("secret", new String(readFully(in, passwordLength), StandardCharsets.UTF_8));
            out.write(new byte[] {0x01, 0x00});
            out.flush();

            assertEquals(0x05, in.read());
            assertEquals(0x01, in.read());
            assertEquals(0x00, in.read());
            assertEquals(0x03, in.read());
            int hostLength = in.read();
            requestedHost.set(new String(readFully(in, hostLength), StandardCharsets.UTF_8));
            int port = (in.read() << 8) | in.read();
            assertEquals(80, port);

            out.write(new byte[] {0x05, 0x00, 0x00, 0x01, 127, 0, 0, 1, (byte) (targetPort >> 8), (byte) targetPort});
            out.flush();

            try (Socket targetSocket = new Socket("127.0.0.1", targetPort)) {
                relayBidirectional(clientSocket, targetSocket);
            }
        } catch (Exception e) {
            failure.compareAndSet(null, e);
        } finally {
            completed.countDown();
        }
    }

    private static void relayBidirectional(Socket clientSocket, Socket targetSocket) throws InterruptedException {
        Thread clientToTarget = new Thread(() -> copy(clientSocket, targetSocket));
        Thread targetToClient = new Thread(() -> copy(targetSocket, clientSocket));
        clientToTarget.start();
        targetToClient.start();
        clientToTarget.join(5000);
        targetToClient.join(5000);
    }

    private static void copy(Socket source, Socket target) {
        try {
            source.getInputStream().transferTo(target.getOutputStream());
        } catch (IOException ignored) {
        }
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int current;
        boolean carriage = false;
        while ((current = inputStream.read()) != -1) {
            if (carriage) {
                if (current == '\n') {
                    return line.toString(StandardCharsets.ISO_8859_1);
                }
                line.write('\r');
                carriage = false;
            }
            if (current == '\r') {
                carriage = true;
            } else {
                line.write(current);
            }
        }
        return line.size() == 0 ? null : line.toString(StandardCharsets.ISO_8859_1);
    }

    private static byte[] readFully(InputStream inputStream, int length) throws IOException {
        byte[] bytes = new byte[length];
        int offset = 0;
        while (offset < length) {
            int read = inputStream.read(bytes, offset, length - offset);
            if (read < 0) {
                throw new IOException("Unexpected EOF");
            }
            offset += read;
        }
        return bytes;
    }
}
