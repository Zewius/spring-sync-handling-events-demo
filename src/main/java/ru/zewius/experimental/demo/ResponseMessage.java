package ru.zewius.experimental.demo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseMessage extends Message {
}
