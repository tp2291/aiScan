package com.cisco.wxcc.saa.abo.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Configuration
public class CustomPrometheusConfig implements WebMvcConfigurer {

    private final MeterRegistry meterRegistry;


    public CustomPrometheusConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PrometheusInterceptor(meterRegistry));
    }

    private static class PrometheusInterceptor implements HandlerInterceptor {
        private final MeterRegistry meterRegistry;

        public PrometheusInterceptor(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            request.setAttribute("startTime", System.currentTimeMillis());
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                               ModelAndView modelAndView) {
            long startTime = (long) request.getAttribute("startTime");
            long requestLatency = System.currentTimeMillis() - startTime;
            Timer.builder("saa_ab_api_latency")
                    .tag("endpoint", request.getRequestURI())
                    .register(meterRegistry)
                    .record(requestLatency, TimeUnit.MILLISECONDS);

            int statusCode = response.getStatus();
            if (statusCode >= 400 && statusCode < 500) {
                Counter.builder("saa_ab_api_4xx_responses")
                        .tag("endpoint", request.getRequestURI())
                        .register(meterRegistry)
                        .increment();
            } else if (statusCode >= 500 && statusCode < 600) {
                Counter.builder("saa_ab_api_5xx_responses")
                        .tag("endpoint", request.getRequestURI())
                        .register(meterRegistry)
                        .increment();
            } else if (statusCode==200) {
                Counter.builder("saa_ab_api_200_responses")
                        .tag("endpoint", request.getRequestURI())
                        .register(meterRegistry)
                        .increment();

            }

        }

    }
}

