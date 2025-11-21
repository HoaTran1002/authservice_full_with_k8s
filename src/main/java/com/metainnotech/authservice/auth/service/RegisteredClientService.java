package com.metainnotech.authservice.auth.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisteredClientService {

    private final JdbcTemplate jdbcTemplate;

    public RegisteredClientService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> listClientIds() {
        return jdbcTemplate.query("select client_id from oauth2_registered_client", (rs, rowNum) -> rs.getString("client_id"));
    }

    // Add more helper methods if you need to return full client records or custom DTOs
}
