package ru.vector.n1.oltp.javaagent.extension.internal;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.metrics.ConfigurableMetricExporterProvider;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import ru.vector.n1.oltp.javaagent.extension.VectorMetricExporter;

public class VectorMetricExporterProvider implements ConfigurableMetricExporterProvider {
    @Override
    public MetricExporter createExporter(ConfigProperties configProperties) {
        return new VectorMetricExporter();
    }

    @Override
    public String getName() {
        return "vector";
    }
}
