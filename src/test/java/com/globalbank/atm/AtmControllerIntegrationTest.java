package com.globalbank.atm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalbank.atm.dto.request.LoginRequest;
import com.globalbank.atm.dto.request.WithdrawRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AtmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String DEMO_CARD = "4001234567890004"; // Titiksha Gupta

    private String obtainJwtToken() throws Exception {
        LoginRequest loginReq = new LoginRequest(DEMO_CARD, "2004");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("token").asText();
    }

    @Test
    void testLoginSuccessReturnsJwt() throws Exception {
        LoginRequest req = new LoginRequest(DEMO_CARD, "2004");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.fullName").value("Titiksha Gupta"));
    }

    @Test
    void testLoginInvalidPinReturnsUnauthorized() throws Exception {
        LoginRequest req = new LoginRequest(DEMO_CARD, "1111");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testUnauthenticatedBalanceReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/atm/balance"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAuthenticatedBalanceEnquiry() throws Exception {
        String token = obtainJwtToken();

        mockMvc.perform(get("/api/v1/atm/balance")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Titiksha Gupta"))
                .andExpect(jsonPath("$.data.accountNo").value("GB100000004"));
    }

    @Test
    void testWithdrawAndMiniStatement() throws Exception {
        String token = obtainJwtToken();

        // Perform withdrawal
        WithdrawRequest withdrawReq = new WithdrawRequest(new BigDecimal("20.00"));
        mockMvc.perform(post("/api/v1/atm/withdraw")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.amountWithdrawn").value(20.00));

        // Fetch mini statement
        mockMvc.perform(get("/api/v1/transactions/mini-statement")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
