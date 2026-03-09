package ru.vector.n1.oltp.javaagent.extension;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import ru.vector.n1.otlp.common.transmitter.SpanDataPulsarTransmitter;

import java.util.Collection;

public class VectorSpanExporter implements SpanExporter {
    private SpanDataPulsarTransmitter transmitter;

    @Override
    public CompletableResultCode export(Collection<SpanData> collection) {
        if (transmitter == null) {
            transmitter = new SpanDataPulsarTransmitter();
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
}
