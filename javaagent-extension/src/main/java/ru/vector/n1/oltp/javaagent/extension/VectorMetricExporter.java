package ru.vector.n1.oltp.javaagent.extension;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import ru.vector.n1.otlp.common.transmitter.MetricDataPulsarTransmitter;

import java.util.Collection;

public class VectorMetricExporter implements MetricExporter {
    private MetricDataPulsarTransmitter transmitter;

    @Override
    public CompletableResultCode export(Collection<MetricData> collection) {
        if (transmitter == null) {
            transmitter = new MetricDataPulsarTransmitter();
        }
        transmitter.transmit(collection);
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        if (transmitter != null) {
            transmitter.close();
        }
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
        return AggregationTemporality.CUMULATIVE;
    }
}
