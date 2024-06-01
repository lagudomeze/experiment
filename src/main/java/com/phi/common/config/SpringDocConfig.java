package com.phi.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(),
        security = @SecurityRequirement(name = "github")

)
@SecurityScheme(
        type = SecuritySchemeType.OAUTH2,
        name = "github",
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "/oauth2/authorization/github",
                        tokenUrl = "/login/oauth2/code/github",
                        scopes = {
                                @OAuthScope(name = "read:user"),
                                @OAuthScope(name = "read:email")
                        }
                )
        )
)

public class SpringDocConfig {

    @Bean
    public OpenApiCustomizer consumerTypeHeaderOpenAPICustomizer() {
        return api -> {
            PathItem path = new PathItem();
            ApiResponses responses = new ApiResponses();
            ApiResponse ok = new ApiResponse();
            responses.addApiResponse("200", ok);
            path.get(new Operation().operationId("logout").responses(responses));
            api.path("/logout", path);
        };
    }
}
