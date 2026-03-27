package ru.tardyon.botframework.telegram.api.model.payment;

import ru.tardyon.botframework.telegram.api.file.InputFile;

public sealed interface InputPaidMedia permits InputPaidMediaPhoto, InputPaidMediaVideo {

    String type();

    InputFile media();
}

