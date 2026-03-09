package ru.vector.n1.otlp.investigation.pulsar;

import org.apache.pulsar.client.api.*;

import java.util.function.Consumer;

public class PulsarImpl implements Pulsar {
    private static final String TOPIC = "test";

    private PulsarClient client = null;
    private Producer<String> producer = null;
    private Consumer<Message<String>> listener;


    @Override
    public void initialize() {
        try {
            client = PulsarClient.builder()
                    .serviceUrl("pulsar://localhost:6650")
                    .build();
            producer = client.newProducer(Schema.STRING).topic(TOPIC).create();

            org.apache.pulsar.client.api.Consumer<String> consumer =
                    client.newConsumer(Schema.STRING)
                            .topic(TOPIC)
                            .startMessageIdInclusive()
                            .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                            .subscriptionName("SimplePulsarConsumer")
                            .subscribe();

            Runnable runnable = () -> {
                while (!client.isClosed()) {
                    try {
                        Message<String> message = consumer.receive();
                        consumer.acknowledge(message);
                        if (message != null && listener != null) {
                            listener.accept(message);
                        }
                    } catch (PulsarClientException ex) {
                        System.out.println(ex);
                    }
                }
            };

            Thread.ofVirtual().start(runnable);

        } catch (PulsarClientException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void sendData(String data) {
        try {
            producer.send(data);
        } catch (PulsarClientException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void subscribe(Consumer<Message<String>> listener) {
        this.listener = listener;
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (PulsarClientException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Producer<String> getProducer() {
        return producer;
    }
}
