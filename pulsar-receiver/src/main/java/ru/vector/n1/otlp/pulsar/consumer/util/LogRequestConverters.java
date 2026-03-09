package ru.vector.n1.otlp.pulsar.consumer.util;

import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.ResourceLogs;
import io.opentelemetry.proto.logs.v1.ScopeLogs;

public class LogRequestConverters {

    private LogRequestConverters() {}

    public static void print(ExportLogsServiceRequest data) {
        for (int i = 0; i < data.getResourceLogsCount(); i++) {
            ResourceLogs resourceLogs = data.getResourceLogs(i);
            for (int j = 0; j < resourceLogs.getScopeLogsCount(); j++) {
                ScopeLogs scopeLogs = resourceLogs.getScopeLogs(j);
                for (int k = 0; k < scopeLogs.getLogRecordsCount(); k++) {
                    LogRecord logRecord = scopeLogs.getLogRecords(k);
                    System.out.println(String.format("VectorLogRecordExporter. Received. SeverityText: %s Value: %s",
                            logRecord.getSeverityText(), logRecord.getBody().getStringValue()));
                }
            }
        }
    }
}
