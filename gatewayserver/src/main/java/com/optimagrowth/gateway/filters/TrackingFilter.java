package com.optimagrowth.gateway.filters;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Order(1)
@Component
public class TrackingFilter implements GlobalFilter {

    @Autowired
    FilterUtils filterUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        if (isCorrelationIdPresent(requestHeaders)) {
            log.debug("tmx-correlation-id found in tracking filter: {}. ",
                    filterUtils.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = filterUtils.setCorrelationId(exchange, correlationID);
            log.debug("tmx-correlation-id generated in tracking filter: {}.", correlationID);
        }

        log.debug("The authentication name from the token is : " + getUsername(requestHeaders));

        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        if (filterUtils.getCorrelationId(requestHeaders) != null) {
            return true;
        } else {
            return false;
        }
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

    private String getUsername(HttpHeaders requestHeaders) {
        String username = "";
        if (filterUtils.getAuthToken(requestHeaders) != null) {
            String authToken = filterUtils.getAuthToken(requestHeaders)
                    .replace("Bearer ", "");
            try {
                JWT jwt = decodeJWT(authToken);
                username = jwt.getJWTClaimsSet().getClaim("preferred_username").toString();
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
        return username;
    }

    private static JWT decodeJWT(String jwtToken) throws ParseException {
        return JWTParser.parse(jwtToken);
    }
}