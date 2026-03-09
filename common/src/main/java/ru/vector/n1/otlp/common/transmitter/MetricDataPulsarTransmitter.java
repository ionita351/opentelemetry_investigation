package ru.vector.n1.otlp.common.transmitter;

import io.opentelemetry.sdk.metrics.data.MetricData;
import ru.vector.n1.otlp.common.converter.MetricDataConverters;
import ru.vector.n1.otlp.common.destination.PulsarDestination;

import java.util.Collection;

public class MetricDataPulsarTransmitter implements Transmitter<Collection<MetricData>> {
    private static final String TOPIC_METRICS = "persistent://public/default/otlp_metrics";
    private final PulsarDestination pulsarDestination;

    public MetricDataPulsarTransmitter() {
        pulsarDestination = PulsarDestination.create();
        pulsarDestination.initializeProducer(TOPIC_METRICS);
    }

    @Override
    public void transmit(Collection<MetricData> value) {
        pulsarDestination.send(MetricDataConverters.toExportMetricsServiceRequest(value).toByteArray());
    }

    @Override
    public void close() {
        pulsarDestination.close();
    }
}
