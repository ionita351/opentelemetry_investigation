package ru.vector.n1.otlp.pulsar.consumer;

import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import ru.vector.n1.otlp.common.converter.LogRecordDataConverters;
import ru.vector.n1.otlp.common.converter.MetricDataConverters;
import ru.vector.n1.otlp.common.converter.SpanDataConverters;
import ru.vector.n1.otlp.common.destination.IoTblDestination;
import ru.vector.n1.otlp.common.destination.PulsarDestination;

public class PulsarConsumer {
    private static final String TOPIC_SPANS = "persistent://public/default/otlp_spans";
    private static final String TOPIC_METRICS = "persistent://public/default/otlp_metrics";
    private static final String TOPIC_LOGS = "persistent://public/default/otlp_logs";

    private static IoTblDestination ioTblDestination;

    public static void main(String... args) {
        ioTblDestination = IoTblDestination.create();
        ioTblDestination.initialize();

        PulsarDestination pulsarDestination = PulsarDestination.create();
        pulsarDestination.initializeReceiver(
                new PulsarDestination.TopicReceiver(TOPIC_LOGS, PulsarConsumer::acceptLogRecordRequest),
                new PulsarDestination.TopicReceiver(TOPIC_METRICS, PulsarConsumer::acceptMetricRequest),
                new PulsarDestination.TopicReceiver(TOPIC_SPANS, PulsarConsumer::acceptSpanRequest));
    }

    private static void acceptLogRecordRequest(byte[] value) {
        ExportLogsServiceRequest request = LogRecordDataConverters.toExportLogsServiceRequest(value);
        System.out.println(LogRecordDataConverters.toLogInfos(request));
        System.out.println(request);
        ioTblDestination.sendLogs(LogRecordDataConverters.toLogInfos(request));
    }

    private static void acceptMetricRequest(byte[] value) {
        ExportMetricsServiceRequest request = MetricDataConverters.toExportMetricsServiceRequest(value);
        System.out.println(MetricDataConverters.toMetricInfos(request));
        System.out.println(request);
        ioTblDestination.sendMetrics(MetricDataConverters.toMetricInfos(request));
    }

    private static void acceptSpanRequest(byte[] value) {
        ExportTraceServiceRequest request = SpanDataConverters.toExportTraceServiceRequest(value);
        System.out.println(SpanDataConverters.toSpanInfos(request));
        System.out.println(request);
        ioTblDestination.sendSpans(SpanDataConverters.toSpanInfos(request));
    }
}
