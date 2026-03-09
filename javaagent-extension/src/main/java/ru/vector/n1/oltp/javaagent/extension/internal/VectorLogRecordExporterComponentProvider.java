package ru.vector.n1.oltp.javaagent.extension.internal;

import io.opentelemetry.sdk.autoconfigure.spi.internal.ComponentProvider;
import ru.vector.n1.oltp.javaagent.extension.VectorLogRecordExporter;

public class VectorLogRecordExporterComponentProvider implements ComponentProvider {

    @Override
    public Class<?> getType() {
        return VectorLogRecordExporter.class;
    }

    @Override
    public String getName() {
        return "vector";
    }

    @Override
    public VectorLogRecordExporter create(io.opentelemetry.api.incubator.config.DeclarativeConfigProperties declarativeConfigProperties) {
        return new VectorLogRecordExporter();
    }
}
