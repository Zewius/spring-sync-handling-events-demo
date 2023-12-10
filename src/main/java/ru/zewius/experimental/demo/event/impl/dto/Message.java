package ru.zewius.experimental.demo.event.impl.dto;

import lombok.Data;

@Data
public class Message {
    String id;
    String type;
    String text;
}
