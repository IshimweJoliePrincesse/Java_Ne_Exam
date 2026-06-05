package com.utility.billing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    OpenAPI utilityBillingOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Utility Billing System API")
                        .description("Backend API for WASAC/REG Utility Billing. Use the Auth section first to register, verify OTP, and login, then authorize Swagger with the returned Bearer token.")
                        .version("1.0.0"))
                .addTagsItem(new Tag().name("1. Authentication").description("Public endpoints for account registration, OTP email verification, login, and password recovery. Start here before testing protected APIs."))
                .addTagsItem(new Tag().name("2. User Management").description("All registered users start as customers. Admin can list users, get user details, delete users, upgrade roles, and revoke roles."))
                .addTagsItem(new Tag().name("3. Customers").description("Customers self-register, view their own profile, and update their own profile. Admin can activate or deactivate customer profiles."))
                .addTagsItem(new Tag().name("4. Meters").description("Physical water and electricity meters assigned to customers. Operators/admins can register meters and activate or deactivate them."))
                .addTagsItem(new Tag().name("5. Meter Readings").description("Monthly meter readings captured by operators. The system validates active meters, one reading per month/year, and consumption values."))
                .addTagsItem(new Tag().name("6. Tariffs").description("Admin tariff configuration for water and electricity, including flat or tiered pricing, fixed charges, VAT, penalties, and versioning."))
                .addTagsItem(new Tag().name("7. Bills").description("Bill generation, approval, lookup, customer bill history, and styled PDF bill download. Bills are generated from meter readings."))
                .addTagsItem(new Tag().name("8. Payments").description("Finance/admin payment recording and payment history. Supports partial and full payments with automatic balance updates."))
                .addTagsItem(new Tag().name("9. Notifications").description("Customer notification messages created by database triggers when bills are approved or fully paid."))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme().name("Bearer Authentication").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
