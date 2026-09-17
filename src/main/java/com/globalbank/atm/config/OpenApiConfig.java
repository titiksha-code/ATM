package com.globalbank.atm.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "GlobalBank ATM Management REST API",
                version = "1.0.0",
                description = "Enterprise Spring Boot REST API for GlobalBank ATM System featuring secure card/PIN authentication, " +
                              "ACID-compliant transactions, daily withdrawal quotas, account lockout protection, and audit logging.",
                contact = @Contact(
                        name = "Titiksha Gupta",
                        email = "titiksha.gupta@globalbank.com"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Enter JWT bearer token received from /api/v1/auth/login"
)
public class OpenApiConfig {
}
