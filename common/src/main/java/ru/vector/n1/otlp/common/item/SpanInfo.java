package ru.vector.n1.otlp.common.item;

public record SpanInfo(long time,
                       String scope,
                       String traceId,
                       String spanId,
                       String parentSpanId,
                       long startTime,
                       long endTime,
                       long duration,
                       String hostName,
                       String serviceName,
                       String name) {
}
