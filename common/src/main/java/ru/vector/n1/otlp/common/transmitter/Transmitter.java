package ru.vector.n1.otlp.common.transmitter;

public interface Transmitter<R> {

    void transmit(R value);

    void close();
}
