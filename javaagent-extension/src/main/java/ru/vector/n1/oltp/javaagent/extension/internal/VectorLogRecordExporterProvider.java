package ru.vector.n1.oltp.javaagent.extension.internal;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.logs.ConfigurableLogRecordExporterProvider;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import ru.vector.n1.oltp.javaagent.extension.VectorLogRecordExporter;

public final class VectorLogRecordExporterProvider implements ConfigurableLogRecordExporterProvider {

    @Override
    public LogRecordExporter createExporter(ConfigProperties configProperties) {
        return new VectorLogRecordExporter();
    }

    @Override
    public String getName() {
        return "vector";
    }
}
