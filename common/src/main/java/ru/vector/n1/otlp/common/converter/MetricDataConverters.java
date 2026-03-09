package ru.vector.n1.otlp.common.converter;

import com.google.protobuf.InvalidProtocolBufferException;
import io.opentelemetry.exporter.internal.otlp.metrics.ResourceMetricsMarshaler;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.metrics.v1.Exemplar;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.sdk.metrics.data.MetricData;
import ru.vector.n1.otlp.common.item.MetricInfo;
import ru.vector.n1.otlp.common.util.ByteStringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class MetricDataConverters {

    private MetricDataConverters() {
    }

    public static ExportMetricsServiceRequest toExportMetricsServiceRequest(Collection<MetricData> metrics) {
        List<ResourceMetrics> resourceMetricsList =
                Arrays.stream(ResourceMetricsMarshaler.create(metrics))
                        .map(
                                resourceMetricsMarshaler -> {
                                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                        resourceMetricsMarshaler.writeBinaryTo(baos);
                                        return ResourceMetrics.parseFrom(baos.toByteArray());
                                    } catch (IOException e) {
                                        throw new IllegalStateException(e);
                                    }
                                })
                        .collect(toList());

        return ExportMetricsServiceRequest.newBuilder().addAllResourceMetrics(resourceMetricsList).build();
    }

    public static ExportMetricsServiceRequest toExportMetricsServiceRequest(byte[] value) {
        try {
            return ExportMetricsServiceRequest.parseFrom(value);
        } catch (InvalidProtocolBufferException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static List<MetricInfo> toMetricInfos(ExportMetricsServiceRequest data) {
        List<MetricInfo> metricInfos = new ArrayList<>();

        for (int i = 0; i < data.getResourceMetricsCount(); i++) {
            ResourceMetrics resourceMetrics = data.getResourceMetrics(i);
            Resource resource = resourceMetrics.getResource();

            String hostName = null;
            String serviceName = null;
            for (int j = 0; j < resource.getAttributesCount(); j++) {
                if (resource.getAttributes(j).getKey().equalsIgnoreCase("host.name")) {
                    hostName = resource.getAttributes(j).getValue().getStringValue();
                } else if (resource.getAttributes(j).getKey().equalsIgnoreCase("host.name")) {
                    serviceName = resource.getAttributes(j).getValue().getStringValue();
                }
            }
            for (int j = 0; j < resourceMetrics.getScopeMetricsCount(); j++) {
                ScopeMetrics scopeMetrics = resourceMetrics.getScopeMetrics(j);
                String scopeName = scopeMetrics.getScope().getName();
                for (int k = 0; k < scopeMetrics.getMetricsCount(); k++) {
                    Metric metric = scopeMetrics.getMetrics(k);
                    String metricName = metric.getName();
                    String description = metric.getDescription();
                    if (metric.getSum() != null) {
                        for (int l = 0; l < metric.getSum().getDataPointsCount(); l++) {
                            NumberDataPoint dataPoint = metric.getSum().getDataPoints(l);

                            for (int m = 0; m < dataPoint.getExemplarsCount(); m++) {
                                Exemplar exemplar = dataPoint.getExemplars(m);

                                metricInfos.add(new MetricInfo(
                                        TimeUnit.NANOSECONDS.toMillis(dataPoint.getTimeUnixNano()),
                                        scopeName,
                                        ByteStringUtils.toBase64String(exemplar.getTraceId()),
                                        ByteStringUtils.toBase64String(exemplar.getSpanId()),
                                        null,
                                        hostName,
                                        serviceName,
                                        metricName,
                                        description,
                                        dataPoint.getAsInt()
                                ));

                            }


                        }
                    }
                }
            }
        }
        return metricInfos;
    }
}
