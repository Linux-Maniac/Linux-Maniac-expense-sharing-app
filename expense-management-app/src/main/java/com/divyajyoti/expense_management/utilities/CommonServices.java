package com.divyajyoti.expense_management.utilities;

import com.divyajyoti.expense_management.rests.exceptions.GenericRestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class CommonServices {

    private final RestTemplate restTemplate;

    @Autowired
    public CommonServices(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    // Generalized method for making any REST call
    public  <T> ResponseEntity<?> makeRestCall(String url, HttpMethod method, Object requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<?> response;
        try {
            // Attempt to make the REST call
            response = restTemplate.exchange(url, method, entity, responseType);
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle 4xx or 5xx status codes (Client or Server Errors)
            String errorMessage = String.format("HTTP Error: %s - %s for URL: %s. Response: %s", e.getStatusCode(), e.getMessage(), url, e.getResponseBodyAsString());
            log.error(errorMessage, e);
            response = new ResponseEntity<>(errorMessage, (HttpStatus) e.getStatusCode());
            return response;
        } catch (ResourceAccessException e) {
            // Handle network issues, timeouts, etc.
            String errorMessage = String.format("Network error or timeout occurred while calling URL: %s. Error: %s", url, e.getMessage());
            log.error(errorMessage, e);
            throw new GenericRestException("NETWORK ERROR OR TIMEOUT OCCURRED", HttpStatus.GATEWAY_TIMEOUT);
        } catch (Exception e) {
            // Catch any other general exceptions
            String errorMessage = String.format("An unexpected error occurred while making REST call to URL: %s. Error: %s", url, e.getMessage());
            log.error(errorMessage, e);
            throw new GenericRestException("UNEXPECTED ERROR OCCURRED", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
