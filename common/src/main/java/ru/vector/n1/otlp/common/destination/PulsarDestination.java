package ru.vector.n1.otlp.common.destination;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;

import java.util.Arrays;
import java.util.function.Consumer;

public class PulsarDestination {
    private PulsarClient client = null;
    private Producer<byte[]> producer = null;
    private org.apache.pulsar.client.api.Consumer<byte[]> consumer;

    private PulsarDestination() {
    }

    public static PulsarDestination create() {
        return new PulsarDestination();
    }

    public void initializeProducer(String topicName) {
        try {
            client = PulsarClient.builder()
                    .serviceUrl("pulsar://localhost:6650")
                    .build();
            producer = client.newProducer().topic(topicName).create();
        } catch (PulsarClientException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void initializeReceiver(TopicReceiver... topicReceivers) {
        String[] topicNames = Arrays.stream(topicReceivers).map(e -> e.topicName).toArray(String[]::new);
        try {
            client = PulsarClient.builder()
                    .serviceUrl("pulsar://localhost:6650")
                    .build();

            consumer = client.newConsumer(Schema.BYTES)
                    .topic(topicNames)
                    .startMessageIdInclusive()
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscriptionName("SimplePulsarConsumer")
                    .subscribe();

            while (true) {
                Message<byte[]> msg = consumer.receive();

                if (msg != null) {
                    for(TopicReceiver topicReceiver : topicReceivers) {
                        if (topicReceiver.topicName.equalsIgnoreCase(msg.getTopicName())) {
                            topicReceiver.topicConsumer.accept(msg.getValue());
                        }
                    }
                }
            }

        } catch (PulsarClientException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void send(byte[] value) {
        try {
            producer.send(value);
        } catch (PulsarClientException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void close() {
        try {
            if (producer != null) {
                producer.close();
                producer = null;
            }
            if (consumer != null) {
                consumer.close();
                consumer = null;
            }
            if (client != null) {
                client.close();
                client = null;
            }
        } catch (PulsarClientException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public record TopicReceiver(String topicName, Consumer<byte[]> topicConsumer) {}
}
