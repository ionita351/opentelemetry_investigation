package ru.vector.n1.otlp.pulsar.consumer.util;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;

public class TraceRequestConverters {

    private TraceRequestConverters() {}

    public static void print(ExportTraceServiceRequest data) {
        for(int i = 0; i < data.getResourceSpansCount(); i++) {
            ResourceSpans resourceSpans = data.getResourceSpans(i);
            for(int j = 0; j < resourceSpans.getScopeSpansCount(); j++) {
                ScopeSpans scopeSpans = resourceSpans.getScopeSpans(j);
                for (int k = 0; k < scopeSpans.getSpansCount(); k++) {
                    Span span = scopeSpans.getSpans(k);
                    System.out.println(String.format("VectorSpanExporter. Received SpanId: %s ParentSpanId: %s Name: %s",
                            span.getSpanId(), span.getParentSpanId(), span.getName()));
                }
            }
        }
    }
}
