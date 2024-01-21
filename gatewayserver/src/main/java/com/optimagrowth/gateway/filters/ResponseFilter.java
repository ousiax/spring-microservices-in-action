package com.optimagrowth.gateway.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class ResponseFilter {

    @Autowired
    Tracer tracer;

    @Autowired
    FilterUtils filterUtils;

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        String traceId = tracer.currentSpan()
                                .context()
                                .traceId();
                        log.debug("Adding the correlation id to the outbound headers. {}", traceId);

                        exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);

                        log.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
                    }));
        };
    }
}

// @Slf4j
// @Configuration
// public class ResponseFilter {

//     @Autowired
//     FilterUtils filterUtils;

//     @Bean
//     public GlobalFilter postGlobalFilter() {
//         return (exchange, chain) -> {
//             return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                 HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
//                 String correlationId = filterUtils.getCorrelationId(requestHeaders);

//                 log.debug("Adding the correlation id to the outbound headers. {}", correlationId);

//                 exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);

//                 log.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
//             }));
//         };
//     }
// }
