package com.divyajyoti.api_gateway.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Configuration
public class PostGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private ModifyResponseBodyGatewayFilterFactory filterFactory;

    public static final String ORIGINAL_RESPONSE_BODY = "originalResponseBody";

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayFilter delegate = filterFactory
                .apply(new ModifyResponseBodyGatewayFilterFactory.Config()
                        .setRewriteFunction(byte[].class, byte[].class, (newExchange, body) -> {
                            String originalBody = null;
                            if (body != null) {
                                originalBody = new String(body);
                            }

                            exchange.getAttributes().put(ORIGINAL_RESPONSE_BODY, originalBody);
                            return Mono.just(originalBody.getBytes());
                        }));
        return delegate
                .filter(exchange, chain)
                .then(Mono.fromRunnable(() -> {
                    this.writeLog(exchange);
                }));
    }

    private void writeLog(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String requestBody = exchange.getAttribute(PreGlobalFilter.ORIGINAL_REQUEST_BODY);

        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        sb.append("URI: ").append(uri).append("\n");
        sb.append("Method: ").append(request.getMethod()).append("\n");
        sb.append("Request Headers: ");

        request.getHeaders().forEach((key, value) -> {
            sb.append(key).append("=").append(value).append(", ");
        });
        sb.append("\n");
        sb.append("Request Body: ").append(requestBody).append("\n");

        sb.append("\n");
        sb.append("Response Status: ").append(response.getStatusCode()).append("\n");
        sb.append("Response Headers: ");

        response.getHeaders().forEach((key, value) -> {
            sb.append(key).append("=").append(value).append(", ");
        });
        sb.append("\n");
        String responseBody = exchange.getAttribute(PostGlobalFilter.ORIGINAL_RESPONSE_BODY);
        sb.append("Response Body: ").append(responseBody).append("\n");

        log.info(sb.toString());
        exchange.getAttributes().remove(PreGlobalFilter.ORIGINAL_REQUEST_BODY);
        exchange.getAttributes().remove(PostGlobalFilter.ORIGINAL_RESPONSE_BODY);
    }

}


