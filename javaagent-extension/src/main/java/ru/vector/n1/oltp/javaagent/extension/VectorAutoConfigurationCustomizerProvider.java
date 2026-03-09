package ru.vector.n1.oltp.javaagent.extension;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;

public class VectorAutoConfigurationCustomizerProvider
        implements AutoConfigurationCustomizerProvider {

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        autoConfiguration
                .addSpanExporterCustomizer((a, b) -> {
                    return new VectorSpanExporter();
                })
                .addLogRecordExporterCustomizer((a, b) -> {
                    return new VectorLogRecordExporter();
                })
                .addMetricExporterCustomizer((a, c) -> {
                    return new VectorMetricExporter();
                });
    }
}
