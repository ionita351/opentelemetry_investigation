package ru.vector.n1.otlp.common.item;

public record LogInfo(long time,
                      String scope,
                      String traceId,
                      String spanId,
                      String parentSpanId,
                      String hostName,
                      String serviceName,
                      String severityText,
                      String bodyValue) {
}
