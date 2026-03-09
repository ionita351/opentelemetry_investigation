package ru.vector.n1.oltp.javaagent.extension.internal;

import io.opentelemetry.api.incubator.config.DeclarativeConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.internal.ComponentProvider;
import ru.vector.n1.oltp.javaagent.extension.VectorMetricExporter;
import ru.vector.n1.oltp.javaagent.extension.VectorSpanExporter;

public class VectorSpanExporterComponentProvider implements ComponentProvider {

    @Override
    public Class<?> getType() {
        return VectorSpanExporter.class;
    }

    @Override
    public String getName() {
        return "vector";
    }

    @Override
    public VectorMetricExporter create(DeclarativeConfigProperties declarativeConfigProperties) {
        return new VectorMetricExporter();
    }
}
