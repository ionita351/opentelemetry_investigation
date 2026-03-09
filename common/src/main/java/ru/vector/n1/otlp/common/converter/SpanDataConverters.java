package ru.vector.n1.otlp.common.converter;

import com.google.protobuf.InvalidProtocolBufferException;
import io.opentelemetry.exporter.internal.otlp.traces.ResourceSpansMarshaler;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.sdk.trace.data.SpanData;
import ru.vector.n1.otlp.common.item.SpanInfo;
import ru.vector.n1.otlp.common.util.ByteStringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class SpanDataConverters {

    private SpanDataConverters() {
    }

    public static ExportTraceServiceRequest toExportTraceServiceRequest(Collection<SpanData> spans) {
        List<ResourceSpans> resourceSpansList =
                Arrays.stream(ResourceSpansMarshaler.create(spans))
                        .map(
                                resourceSpansMarshaler -> {
                                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                        resourceSpansMarshaler.writeBinaryTo(baos);
                                        return ResourceSpans.parseFrom(baos.toByteArray());
                                    } catch (IOException e) {
                                        throw new IllegalStateException(e);
                                    }
                                })
                        .collect(toList());
        return ExportTraceServiceRequest.newBuilder().addAllResourceSpans(resourceSpansList).build();
    }

    public static ExportTraceServiceRequest toExportTraceServiceRequest(byte[] value) {
        try {
            return ExportTraceServiceRequest.parseFrom(value);
        } catch (InvalidProtocolBufferException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static List<SpanInfo> toSpanInfos(ExportTraceServiceRequest data) {
        List<SpanInfo> spanInfos = new ArrayList<>();

        for (int i = 0; i < data.getResourceSpansCount(); i++) {
            ResourceSpans resourceSpans = data.getResourceSpans(i);
            Resource resource = resourceSpans.getResource();
            String hostName = null;
            String serviceName = null;
            for (int j = 0; j < resource.getAttributesCount(); j++) {
                if (resource.getAttributes(j).getKey().equalsIgnoreCase("host.name")) {
                    hostName = resource.getAttributes(j).getValue().getStringValue();
                } else if (resource.getAttributes(j).getKey().equalsIgnoreCase("host.name")) {
                    serviceName = resource.getAttributes(j).getValue().getStringValue();
                }
            }
            for (int j = 0; j < resourceSpans.getScopeSpansCount(); j++) {
                ScopeSpans scopeSpans = resourceSpans.getScopeSpans(j);
                String scopeName = scopeSpans.getScope().getName();
                for (int k = 0; k < scopeSpans.getSpansCount(); k++) {
                    Span span = scopeSpans.getSpans(k);

                    spanInfos.add(new SpanInfo(
                            TimeUnit.NANOSECONDS.toMillis(span.getStartTimeUnixNano()),
                            scopeName,
                            ByteStringUtils.toBase64String(span.getTraceId()),
                            ByteStringUtils.toBase64String(span.getSpanId()),
                            ByteStringUtils.toBase64String(span.getParentSpanId()),
                            TimeUnit.NANOSECONDS.toMillis(span.getStartTimeUnixNano()),
                            TimeUnit.NANOSECONDS.toMillis(span.getEndTimeUnixNano()),
                            TimeUnit.NANOSECONDS.toMillis(span.getEndTimeUnixNano())
                                    - TimeUnit.NANOSECONDS.toMillis(span.getStartTimeUnixNano()),
                            hostName,
                            serviceName,
                            span.getName()));
                }
            }
        }
        return spanInfos;
    }
}
