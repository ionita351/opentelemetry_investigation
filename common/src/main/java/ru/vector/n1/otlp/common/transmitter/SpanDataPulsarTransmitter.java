package ru.vector.n1.otlp.common.transmitter;

import io.opentelemetry.sdk.trace.data.SpanData;
import ru.vector.n1.otlp.common.converter.SpanDataConverters;
import ru.vector.n1.otlp.common.destination.PulsarDestination;

import java.util.Collection;

public class SpanDataPulsarTransmitter implements Transmitter<Collection<SpanData>> {
    private static final String TOPIC_SPANS = "persistent://public/default/otlp_spans";
    private final PulsarDestination pulsarDestination;

    public SpanDataPulsarTransmitter() {
        pulsarDestination = PulsarDestination.create();
        pulsarDestination.initializeProducer(TOPIC_SPANS);
    }

    @Override
    public void transmit(Collection<SpanData> value) {
        pulsarDestination.send(SpanDataConverters.toExportTraceServiceRequest(value).toByteArray());
    }

    @Override
    public void close() {
        pulsarDestination.close();
    }
}
