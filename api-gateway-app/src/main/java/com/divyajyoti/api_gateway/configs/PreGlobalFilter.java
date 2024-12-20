package com.divyajyoti.api_gateway.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class PreGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private ModifyRequestBodyGatewayFilterFactory filterFactory;

    public static final String ORIGINAL_REQUEST_BODY = "originalRequestBody";

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return filterFactory
                .apply(new ModifyRequestBodyGatewayFilterFactory.Config()
                        .setRewriteFunction(String.class, String.class, (newExchange, body) -> {
                            String originalBody = null;
                            if (body != null) {
                                originalBody = body;
                            }

                            exchange.getAttributes().put(ORIGINAL_REQUEST_BODY, originalBody);
                            return Mono.just(originalBody);
                        }))
                .filter(exchange, chain);
    }

}
