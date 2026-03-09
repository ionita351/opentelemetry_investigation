package ru.vector.n1.otlp.common.util;

import com.google.protobuf.ByteString;

import java.util.Base64;

public class ByteStringUtils {

    private ByteStringUtils() {}

    public static String toBase64String(ByteString value) {
        return Base64.getEncoder().encodeToString(value.toByteArray());
    }
}
