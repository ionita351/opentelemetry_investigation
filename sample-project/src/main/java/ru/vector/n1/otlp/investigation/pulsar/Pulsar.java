package ru.vector.n1.otlp.investigation.pulsar;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;

import java.util.function.Consumer;

public interface Pulsar {
    void initialize();
    void sendData(String data);
    void subscribe(Consumer<Message<String>> consumer);
    void close();
    Producer<String> getProducer();
}
