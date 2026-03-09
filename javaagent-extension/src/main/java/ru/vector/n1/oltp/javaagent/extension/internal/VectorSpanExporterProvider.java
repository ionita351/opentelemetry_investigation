package ru.vector.n1.oltp.javaagent.extension.internal;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.traces.ConfigurableSpanExporterProvider;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import ru.vector.n1.oltp.javaagent.extension.VectorSpanExporter;

public class VectorSpanExporterProvider implements ConfigurableSpanExporterProvider {

    @Override
    public SpanExporter createExporter(ConfigProperties configProperties) {
        return new VectorSpanExporter();
    }

    @Override
    public String getName() {
        return "vector";
    }
}
