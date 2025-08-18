package com.example.customeridentitydemo.controller;

import com.example.customeridentitydemo.dto.LoginRequestDTO;
import com.example.customeridentitydemo.dto.LoginResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_shouldReturnJwtToken_whenValidCredentials() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("admin", "admin123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andReturn();

        LoginResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), 
                LoginResponseDTO.class
        );
        
        assertNotNull(response.getToken());
        assertFalse(response.getToken().isEmpty());
        assertEquals("Bearer", response.getType());
        assertEquals("admin", response.getUsername());
    }

    @Test
    void login_shouldReturnUnauthorized_whenInvalidCredentials() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("admin", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturnBadRequest_whenInvalidInput() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("", ""); // Empty credentials

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateToken_shouldReturnOk_whenValidToken() throws Exception {
        // First login to get a token
        LoginRequestDTO loginRequest = new LoginRequestDTO("user", "password");
        
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), 
                LoginResponseDTO.class
        );

        // Use the token to validate
        mockMvc.perform(post("/api/auth/validate")
                .header("Authorization", "Bearer " + loginResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("Token is valid for user: user"));
    }

    @Test
    void validateToken_shouldReturnUnauthorized_whenInvalidToken() throws Exception {
        mockMvc.perform(post("/api/auth/validate")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isForbidden()); // 403 when JWT filter rejects invalid token
    }
}