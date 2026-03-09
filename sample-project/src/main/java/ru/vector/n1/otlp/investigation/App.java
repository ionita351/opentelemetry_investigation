package ru.vector.n1.otlp.investigation;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.TracerBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.internal.ExtendedTextMapGetter;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vector.n1.otlp.investigation.pulsar.Pulsar;
import ru.vector.n1.otlp.investigation.pulsar.PulsarImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger("ru.vector.n1.otlp.investigation.App");

    public static void main(String... args) throws InterruptedException {
        OpenTelemetry openTelemetry =
                GlobalOpenTelemetry.isSet() ? GlobalOpenTelemetry.get() : initializeOpenTelemetry();
        TextMapPropagator propagator = openTelemetry.getPropagators().getTextMapPropagator();
        ContextKey<String> contextKey = ContextKey.named("app");
        Context context = Context.current().with(contextKey, "CONTEXT VALUE");

        try (Scope scope = context.makeCurrent()) {

            Tracer tracer = openTelemetry.getTracer("TRACER use pulsar");
            Pulsar pulsar = new PulsarImpl();
            pulsar.initialize();
            pulsar.subscribe(App::acceptor);

            int i = 0;
            while (true) {
                Span span = tracer.spanBuilder("send pulsar. iteration " + i).startSpan();
                try (Scope s = span.makeCurrent()) {
                    span.storeInContext(context);
                    String value = "Iteration " + i++;
                    LOGGER.info("Send to PULSAR. {}", value);

                    Map<String, String> map = new HashMap<>();
                    propagator.inject(context, map, (prop, key, v) -> {
                        if (value != null) {
                            map.put(key, v);
                        }
                    });
                    TypedMessageBuilder<String> message = pulsar.getProducer().newMessage().properties(map);
                    pulsar.sendData(value);
                    TimeUnit.SECONDS.sleep(5);
                }
                span.end();
            }

        }
    }

    private static void acceptor(Message<String> message) {
        OpenTelemetry openTelemetry =
                GlobalOpenTelemetry.isSet() ? GlobalOpenTelemetry.get() : initializeOpenTelemetry();
        TextMapPropagator propagator = openTelemetry.getPropagators().getTextMapPropagator();

        Context parentContext = propagator.extract(Context.current(),
                message.getProperties(),
                new ExtendedTextMapGetter<Map<String, String>>() {
                    @Override
                    public Iterable<String> keys(Map<String, String> stringStringMap) {
                        return stringStringMap.keySet();
                    }

                    @Override
                    public String get(Map<String, String> stringStringMap, String s) {
                        return stringStringMap.get(s);
                    }
                });

        try (Scope scope = parentContext.makeCurrent()) {
            LOGGER.info("Received from PULSAR : {}", message.getValue());
        }
    }

    public static OpenTelemetry initializeOpenTelemetry() {
        return AutoConfiguredOpenTelemetrySdk.initialize().getOpenTelemetrySdk();
    }
}
