package ru.vector.n1.otlp.common.item;

public record MetricInfo(long time,
                         String scope,
                         String traceId,
                         String spanId,
                         String parentSpanId,
                         String hostName,
                         String serviceName,
                         String metricName,
                         String description,
                         long value
                         ) {
}
