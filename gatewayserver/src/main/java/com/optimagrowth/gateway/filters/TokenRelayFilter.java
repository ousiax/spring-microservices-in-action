// package com.optimagrowth.gateway.filters;

// import java.time.Instant;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.cloud.gateway.filter.GlobalFilter;
// import org.springframework.core.Ordered;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
// import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
// import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
// import org.springframework.web.server.ServerWebExchange;

// import reactor.core.publisher.Mono;

// public class TokenRelayFilter implements GlobalFilter, Ordered {

//     @Autowired
//     private OAuth2AuthorizedClientService authorizedClientService;

//     @Override
//     public int getOrder() {
//         return -100;
//     }

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//         return exchange.getPrincipal()
//                 .filter(principal -> principal instanceof OAuth2AuthenticationToken)
//                 .cast(OAuth2AuthenticationToken.class)
//                 .flatMap(authentication -> {
//                     OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
//                             authentication.getAuthorizedClientRegistrationId(),
//                             authentication.getName());

//                     // Check if OAuth2AuthorizedClient is null or expired then handle accordingly
//                     if (client == null || client.getAccessToken().getTokenValue() == null ||
//                             client.getAccessToken().getExpiresAt().isBefore(Instant.now())) {
//                         return Mono.error(new RuntimeException("Not Authenticated")); // handle not authenticated error
//                     }

//                     String token = client.getAccessToken().getTokenValue();

//                     ServerHttpRequest request = exchange.getRequest().mutate()
//                             .header("Authorization", "Bearer " + token) // add the authorization header
//                             .build();

//                     return chain.filter(exchange.mutate().request(request).build()); // mutate the ServerWebExchange with new request
//                 });
//     }
// }
