package com.couple.order.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class D1DatabaseService {

    @Value("${cloudflare.d1.account-id}")
    private String accountId;

    @Value("${cloudflare.d1.database-id}")
    private String databaseId;

    @Value("${cloudflare.d1.api-token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseUrl() {
        return String.format("https://api.cloudflare.com/client/v4/accounts/%s/d1/database/%s",
                accountId, databaseId);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiToken);
        return headers;
    }

    private List<Object> convertParams(Object... params) {
        List<Object> result = new ArrayList<>();
        if (params != null) {
            for (Object param : params) {
                if (param instanceof Boolean) {
                    result.add(((Boolean) param) ? 1 : 0);
                } else {
                    result.add(param);
                }
            }
        }
        return result;
    }

    public List<Map<String, Object>> query(String sql, Object... params) {
        String url = getBaseUrl() + "/query";

        Map<String, Object> body = new HashMap<>();
        body.put("sql", sql);
        if (params != null && params.length > 0) {
            body.put("params", convertParams(params));
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, getHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            if (root.has("success") && root.get("success").asBoolean()) {
                JsonNode result = root.get("result").get(0);
                if (result.has("results")) {
                    return objectMapper.convertValue(result.get("results"),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
                }
                return new ArrayList<>();
            } else {
                String errors = root.has("errors") ? root.get("errors").toString() : "Unknown error";
                throw new RuntimeException("D1 query failed: " + errors);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute D1 query: " + e.getMessage(), e);
        }
    }

    public int execute(String sql, Object... params) {
        String url = getBaseUrl() + "/query";

        Map<String, Object> body = new HashMap<>();
        body.put("sql", sql);
        if (params != null && params.length > 0) {
            body.put("params", convertParams(params));
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, getHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            if (root.has("success") && root.get("success").asBoolean()) {
                JsonNode meta = root.get("result").get(0).get("meta");
                return meta.has("changes") ? meta.get("changes").asInt() : 0;
            } else {
                String errors = root.has("errors") ? root.get("errors").toString() : "Unknown error";
                throw new RuntimeException("D1 execute failed: " + errors);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute D1 statement: " + e.getMessage(), e);
        }
    }

    public long executeInsert(String sql, Object... params) {
        String url = getBaseUrl() + "/query";

        Map<String, Object> body = new HashMap<>();
        body.put("sql", sql);
        if (params != null && params.length > 0) {
            body.put("params", convertParams(params));
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, getHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            if (root.has("success") && root.get("success").asBoolean()) {
                JsonNode meta = root.get("result").get(0).get("meta");
                return meta.has("last_row_id") ? meta.get("last_row_id").asLong() : -1;
            } else {
                String errors = root.has("errors") ? root.get("errors").toString() : "Unknown error";
                throw new RuntimeException("D1 executeInsert failed: " + errors);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute D1 insert: " + e.getMessage(), e);
        }
    }
}
