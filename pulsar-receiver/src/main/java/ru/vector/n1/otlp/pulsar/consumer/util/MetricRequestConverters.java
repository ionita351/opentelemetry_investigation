package ru.vector.n1.otlp.pulsar.consumer.util;

import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import io.opentelemetry.proto.resource.v1.Resource;

public class MetricRequestConverters {

    private MetricRequestConverters() {}

    public static void print(ExportMetricsServiceRequest data) {
        for(int i = 0; i < data.getResourceMetricsCount(); i++) {
            ResourceMetrics resourceMetrics = data.getResourceMetrics(i);
            for (int j = 0; j < resourceMetrics.getScopeMetricsCount(); j++) {
                ScopeMetrics scopeMetrics = resourceMetrics.getScopeMetrics(j);
                for(int k = 0; k < scopeMetrics.getMetricsCount(); k++) {
                    Metric metric = scopeMetrics.getMetrics(k);
                    System.out.println(String.format("VectorMetricExporter. Received. Name: %s Description: %s",
                            metric.getName(), metric.getDescription()));
                }
            }
        }
    }
}
