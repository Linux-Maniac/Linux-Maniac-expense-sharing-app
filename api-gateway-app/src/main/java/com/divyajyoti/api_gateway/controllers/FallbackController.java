package com.divyajyoti.api_gateway.controllers;

import jakarta.ws.rs.QueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FallbackController {

    @RequestMapping(value = "/fallback/user-management-app")
    public ResponseEntity<Object> fallbackUserManagement() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Gateway Timeout!");
        response.put("service", "USER-MANAGEMENT-APPLICATION!");
        return new ResponseEntity<>(response, HttpStatus.GATEWAY_TIMEOUT);
    }

    @RequestMapping(value = "/fallback/group-management-app")
    public ResponseEntity<Object> fallbackGroupManagement() {
        log.info("API-GATEWAY-GROUP-MANAGEMENT-FALL-BACK-EXECUTED");
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Gateway Timeout!");
        response.put("service", "GROUP-MANAGEMENT-APPLICATION!");
        return new ResponseEntity<>(response, HttpStatus.GATEWAY_TIMEOUT);
    }

}
