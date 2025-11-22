package com.restkeeper.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.restkeeper.utils.JWTUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthFilter implements GlobalFilter, Ordered {
    @Value("#{'${gateway.excludeUrls}'.split(',')}")
    private List<String> excludeUrls;
    @Value("${gateway.secret}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        String paths = exchange.getRequest().getURI().getPath();
        if(excludeUrls.contains(paths)){
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(StringUtils.isNotEmpty(token)){
            JWTUtil.VerifyResult verifyResult = JWTUtil.verifyJwt(token, secret);
            if (verifyResult.isValidate()){
                return chain.filter(exchange);
            }else {
                Map<String, Object> responseData = Maps.newHashMap();
                responseData.put("code", verifyResult.getCode());
                responseData.put("message", "验证失败");
                return responseError(response, responseData);
            }
        }else {
            Map<String,Object> responseData = Maps.newHashMap();

            responseData.put("code", 401);

            responseData.put("message", "非法请求");

            responseData.put("cause", "Token is empty");

            return responseError(response,responseData);
        }
    }

    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData) {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try {
            objectMapper.writeValueAsBytes(responseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
