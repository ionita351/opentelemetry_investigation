package ru.vector.n1.oltp.javaagent.extension.internal;

import io.opentelemetry.api.incubator.config.DeclarativeConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.internal.ComponentProvider;
import ru.vector.n1.oltp.javaagent.extension.VectorMetricExporter;

public class VectorMetricExporterComponentProvider implements ComponentProvider {

    @Override
    public Class<?> getType() {
        return VectorMetricExporter.class;
    }

    @Override
    public String getName() {
        return "vector";
    }

    @Override
    public Object create(DeclarativeConfigProperties declarativeConfigProperties) {
        return new VectorMetricExporter();
    }
}
