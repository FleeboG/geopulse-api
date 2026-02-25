package com.geopulse.geopulse_api.auth;

import com.geopulse.geopulse_api.auth.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(classes = com.geopulse.geopulse_api.GeopulseApiApplication.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void register_returns201_whenOk() throws Exception {
        String json = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("registered"));
    }

    @Test
    void register_returns409_whenEmailExists() throws Exception {
        doThrow(new IllegalArgumentException("Email already registered"))
                .when(authService).register(any(RegisterRequest.class));

        String json = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("conflict"))
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void login_returns200_withToken_whenOk() throws Exception {
        // Mock the service to return a token response
        org.mockito.Mockito.when(authService.login(any(com.geopulse.geopulse_api.auth.dto.LoginRequest.class)))
                .thenReturn(com.geopulse.geopulse_api.auth.dto.AuthResponse.bearer("fake.jwt.token"));

        String json = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("fake.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_returns401_whenInvalidCredentials() throws Exception {
        org.mockito.Mockito.when(authService.login(any(com.geopulse.geopulse_api.auth.dto.LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        String json = "{\"email\":\"test@example.com\",\"password\":\"wrongpass123\"}";

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}