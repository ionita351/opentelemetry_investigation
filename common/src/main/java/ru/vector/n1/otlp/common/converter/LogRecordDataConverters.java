package ru.vector.n1.otlp.common.converter;

import com.google.protobuf.InvalidProtocolBufferException;
import io.opentelemetry.exporter.internal.otlp.logs.ResourceLogsMarshaler;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.ResourceLogs;
import io.opentelemetry.proto.logs.v1.ScopeLogs;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import ru.vector.n1.otlp.common.item.LogInfo;
import ru.vector.n1.otlp.common.util.ByteStringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class LogRecordDataConverters {
    private LogRecordDataConverters() {
    }

    public static ExportLogsServiceRequest toExportLogsServiceRequest(Collection<LogRecordData> logs) {
        List<ResourceLogs> resourceLogsList =
                Arrays.stream(ResourceLogsMarshaler.create(logs))
                        .map(
                                resourceLogsMarshaler -> {
                                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                        resourceLogsMarshaler.writeBinaryTo(baos);
                                        return ResourceLogs.parseFrom(baos.toByteArray());
                                    } catch (IOException e) {
                                        throw new IllegalStateException(e);
                                    }
                                })
                        .collect(toList());

        return ExportLogsServiceRequest.newBuilder().addAllResourceLogs(resourceLogsList).build();
    }

    public static ExportLogsServiceRequest toExportLogsServiceRequest(byte[] value) {
        try {
            return ExportLogsServiceRequest.parseFrom(value);
        } catch (InvalidProtocolBufferException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static List<LogInfo> toLogInfos(ExportLogsServiceRequest data) {
        List<LogInfo> logInfos = new ArrayList<>();

        for (int i = 0; i < data.getResourceLogsCount(); i++) {
            ResourceLogs resourceLogs = data.getResourceLogs(i);
            Resource resource = resourceLogs.getResource();
            String hostName = null;
            String serviceName = null;
            for (int j = 0; j < resource.getAttributesCount(); j++) {
                if (resource.getAttributes(j).getKey().equalsIgnoreCase("host.name")) {
                    hostName = resource.getAttributes(j).getValue().getStringValue();
                } else if (resource.getAttributes(j).getKey().equalsIgnoreCase("host.name")) {
                    serviceName = resource.getAttributes(j).getValue().getStringValue();
                }
            }
            for (int j = 0; j < resourceLogs.getScopeLogsCount(); j++) {

                ScopeLogs scopeLogs = resourceLogs.getScopeLogs(j);
                for (int k = 0; k < scopeLogs.getLogRecordsCount(); k++) {
                    LogRecord logRecord = scopeLogs.getLogRecords(k);


                    logInfos.add(new LogInfo(
                            TimeUnit.NANOSECONDS.toMillis(logRecord.getTimeUnixNano()),
                            scopeLogs.getScope().getName(),
                            ByteStringUtils.toBase64String(logRecord.getTraceId()),
                            ByteStringUtils.toBase64String(logRecord.getSpanId()),
                            null,
                            hostName,
                            serviceName,
                            logRecord.getSeverityText(),
                            logRecord.getBody().getStringValue()));
                }
            }
        }
        return logInfos;
    }
}
