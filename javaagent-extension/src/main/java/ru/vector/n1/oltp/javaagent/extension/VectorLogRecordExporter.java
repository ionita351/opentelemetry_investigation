package ru.vector.n1.oltp.javaagent.extension;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import ru.vector.n1.otlp.common.transmitter.LogRecordDataPulsarTransmitter;

import java.util.Collection;

public class VectorLogRecordExporter implements LogRecordExporter {
    private LogRecordDataPulsarTransmitter transmitter;

    @Override
    public CompletableResultCode export(Collection<LogRecordData> collection) {
        if (transmitter == null) {
            transmitter = new LogRecordDataPulsarTransmitter();
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
