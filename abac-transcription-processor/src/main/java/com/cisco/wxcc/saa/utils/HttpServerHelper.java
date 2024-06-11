package com.cisco.wxcc.saa.utils;

import com.cisco.wxcc.saa.pojo.BuildInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.HTTPServer;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import static com.cisco.wxcc.saa.App.interactionId;
import static com.cisco.wxcc.saa.App.orgId;
import static com.cisco.wxcc.saa.constants.AppConstants.*;

@Slf4j
public class HttpServerHelper {

    private HttpServerHelper(){}
    private static PrometheusMetricsConfig metrics;
    public static void startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        configPrometheusMetrics(server);
        configBuildInfo(server);
        configPing(server);

        server.start();

        log.info(String.format("Metric endpoint started at http://<HOST>:%s/metrics", port), Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_ORG_ID_FIELD_NAME, orgId));
    }

    public static PrometheusMeterRegistry getRegistry() {
        return metrics.getRegistry();
    }

    private static void configPrometheusMetrics(HttpServer server) throws IOException {
        metrics = new PrometheusMetricsConfig();
        HTTPServer.HTTPMetricHandler handler = new HTTPServer.HTTPMetricHandler(metrics.getRegistry().getPrometheusRegistry());
        server.createContext("/metrics", handler);
    }

    private static void configBuildInfo(HttpServer server) {
        HttpContext buildInfoContext = server.createContext("/build_info");
        buildInfoContext.setHandler(HttpServerHelper::handleBuildInfoRequest);
    }

    private static void configPing(HttpServer server) {
        HttpContext pingContext = server.createContext("/ping");
        pingContext.setHandler(HttpServerHelper::handlePingRequest);
    }

    private static void handleBuildInfoRequest(HttpExchange exchange) throws IOException {
        BuildInfo buildInfo = new BuildInfo(
                System.getenv("appName"),
                System.getenv("gitCommit"),
                "NA",
                System.getenv("buildTag"),
                System.getenv("buildUrl"),
                System.getenv("buildId"),
                System.getenv("buildNumber"),
                System.getenv("gitBranch"));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(buildInfo);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length());

        OutputStream os = exchange.getResponseBody();
        os.write(json.getBytes());
        os.close();
    }

    private static void handlePingRequest(HttpExchange exchange) throws IOException {
        String response = "Healthy";
        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}