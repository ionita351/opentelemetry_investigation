package ru.vector.n1.otlp.common.transmitter;

import io.opentelemetry.sdk.logs.data.LogRecordData;
import ru.vector.n1.otlp.common.converter.LogRecordDataConverters;
import ru.vector.n1.otlp.common.destination.PulsarDestination;

import java.util.Collection;

public class LogRecordDataPulsarTransmitter implements Transmitter<Collection<LogRecordData>> {
    private static final String TOPIC_LOGS = "persistent://public/default/otlp_logs";
    private final PulsarDestination pulsarDestination;

    public LogRecordDataPulsarTransmitter() {
        pulsarDestination = PulsarDestination.create();
        pulsarDestination.initializeProducer(TOPIC_LOGS);
    }

    @Override
    public void transmit(Collection<LogRecordData> value) {
        pulsarDestination.send(LogRecordDataConverters.toExportLogsServiceRequest(value).toByteArray());
    }

    @Override
    public void close() {
        pulsarDestination.close();
    }
}
