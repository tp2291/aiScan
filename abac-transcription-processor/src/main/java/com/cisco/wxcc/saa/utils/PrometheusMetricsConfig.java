package com.cisco.wxcc.saa.utils;

import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.cisco.wxcc.saa.App.interactionId;
import static com.cisco.wxcc.saa.App.orgId;
import static com.cisco.wxcc.saa.constants.AppConstants.*;

@Slf4j
public class PrometheusMetricsConfig {
    private final PrometheusMeterRegistry prometheusRegistry;

    public PrometheusMetricsConfig() throws UnknownHostException {
        prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT,
                CollectorRegistry.defaultRegistry,Clock.SYSTEM);

        // Makes sure we don't re-register anything during restart.
        prometheusRegistry.clear();

        prometheusRegistry.config()
                .commonTags("instance", InetAddress.getLocalHost().getHostAddress())
                .commonTags("source", "saa-abac-transcription-processor");
        new ClassLoaderMetrics().bindTo(prometheusRegistry);
        new JvmMemoryMetrics().bindTo(prometheusRegistry);
        new JvmGcMetrics().bindTo(prometheusRegistry);
        new ProcessorMetrics().bindTo(prometheusRegistry);
        new JvmThreadMetrics().bindTo(prometheusRegistry);
        new UptimeMetrics().bindTo(prometheusRegistry);
        new ProcessMemoryMetrics().bindTo(prometheusRegistry);
        new ProcessThreadMetrics().bindTo(prometheusRegistry);

        try{
            new LogbackMetrics().bindTo(prometheusRegistry);
        }catch (NoClassDefFoundError e){
            log.warn("Logback Implementation missing ! : Logging metrics are not available", Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_ORG_ID_FIELD_NAME, orgId));
        }
    }

    public PrometheusMeterRegistry getRegistry() {
        return prometheusRegistry;
    }
}