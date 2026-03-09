package ru.vector.n1.otlp.common.destination;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.tsfile.enums.TSDataType;
import ru.vector.n1.otlp.common.item.LogInfo;
import ru.vector.n1.otlp.common.item.MetricInfo;
import ru.vector.n1.otlp.common.item.SpanInfo;

import java.util.ArrayList;
import java.util.List;


public class IoTblDestination {
    public SessionPool SESSION_POOL;

    private IoTblDestination() {}

    public static IoTblDestination create() {
        return new IoTblDestination();
    }

    public void initialize() {
        SESSION_POOL = new SessionPool.Builder()
                .nodeUrls(List.of("localhost:6667"))
                .user("root")
                .password("root")
                .maxSize(20)
                .build();
    }

    public void clos() {
        SESSION_POOL.close();
    }

    public void sendLogs(List<LogInfo> logs) {
        List<Long> timestamps = new ArrayList<>();
        List<String> deviceIds = new ArrayList<>();
        List<String> measurements = List.of("scope", "traceId", "spanId",
                "hostName", "serviceName",
                "severityText", "bodyValue");
        List<List<String>> measurementsList = new ArrayList<>();
        List<TSDataType> types = List.of(TSDataType.STRING, TSDataType.STRING, TSDataType.STRING,
                TSDataType.STRING, TSDataType.STRING,
                TSDataType.STRING, TSDataType.STRING);
        List<List<TSDataType>> typesList = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        List<List<Object>> valuesList = new ArrayList<>();

        for(LogInfo logInfo : logs) {
            deviceIds.add("root.logs." + logInfo.hostName().replaceAll("-", "_"));
            timestamps.add(logInfo.time());
            measurementsList.add(measurements);
            typesList.add(types);
            values.clear();
            values.add(logInfo.scope());
            values.add(logInfo.traceId());
            values.add(logInfo.spanId());

            values.add(logInfo.hostName());
            values.add(logInfo.serviceName());

            values.add(logInfo.severityText());
            values.add(logInfo.bodyValue());
            valuesList.add(values);
        }
        try {
            SESSION_POOL.insertRecords(deviceIds, timestamps, measurementsList, typesList, valuesList);
        } catch (IoTDBConnectionException | StatementExecutionException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void sendMetrics(List<MetricInfo> metrics) {
        List<Long> timestamps = new ArrayList<>();
        List<String> deviceIds = new ArrayList<>();
        List<String> measurements = List.of("scope", "traceId", "spanId", "parentSpanId",
                "hostName", "serviceName", "value");
        List<List<String>> measurementsList = new ArrayList<>();
        List<TSDataType> types = List.of(TSDataType.STRING, TSDataType.STRING, TSDataType.STRING, TSDataType.STRING,
                TSDataType.STRING, TSDataType.STRING, TSDataType.INT64);
        List<List<TSDataType>> typesList = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        List<List<Object>> valuesList = new ArrayList<>();

        for(MetricInfo metricInfo : metrics) {
            deviceIds.add("root.metrics." + metricInfo.hostName().replaceAll("-", "_") + "."
                    + metricInfo.metricName().replaceAll("\\.", "_"));
            timestamps.add(metricInfo.time());
            measurementsList.add(measurements);
            typesList.add(types);
            values.clear();
            values.add(metricInfo.scope());
            values.add(metricInfo.traceId());
            values.add(metricInfo.spanId());
            values.add(metricInfo.parentSpanId());

            values.add(metricInfo.hostName());
            values.add(metricInfo.serviceName());

            values.add(metricInfo.value());
            valuesList.add(values);
        }
        try {
            SESSION_POOL.insertRecords(deviceIds, timestamps, measurementsList, typesList, valuesList);
        } catch (IoTDBConnectionException | StatementExecutionException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void sendSpans(List<SpanInfo> metrics) {
        List<Long> timestamps = new ArrayList<>();
        List<String> deviceIds = new ArrayList<>();
        List<String> measurements = List.of("scope", "traceId", "spanId", "parentSpanId",
                "startTime", "endTime", "duration",
                "hostName", "serviceName", "name");
        List<List<String>> measurementsList = new ArrayList<>();
        List<TSDataType> types = List.of(TSDataType.STRING, TSDataType.STRING, TSDataType.STRING, TSDataType.STRING,
                TSDataType.INT64, TSDataType.INT64, TSDataType.INT64,
                TSDataType.STRING, TSDataType.STRING, TSDataType.STRING);
        List<List<TSDataType>> typesList = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        List<List<Object>> valuesList = new ArrayList<>();

        for(SpanInfo spanInfo : metrics) {
            deviceIds.add("root.spans." + spanInfo.hostName().replaceAll("-", "_"));
            timestamps.add(spanInfo.startTime());
            measurementsList.add(measurements);
            typesList.add(types);
            values.clear();
            values.add(spanInfo.scope());
            values.add(spanInfo.traceId());
            values.add(spanInfo.spanId());
            values.add(spanInfo.parentSpanId());

            values.add(spanInfo.startTime());
            values.add(spanInfo.endTime());
            values.add(spanInfo.duration());

            values.add(spanInfo.hostName());
            values.add(spanInfo.serviceName());
            values.add(spanInfo.name());
            valuesList.add(values);
        }
        try {
            SESSION_POOL.insertRecords(deviceIds, timestamps, measurementsList, typesList, valuesList);
        } catch (IoTDBConnectionException | StatementExecutionException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
