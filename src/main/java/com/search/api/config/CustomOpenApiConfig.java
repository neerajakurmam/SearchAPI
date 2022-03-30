package com.search.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomOpenApiConfig {
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components())
				.info(new Info()
							.version("v1.0")
							.contact(new Contact()
											.email("neeru.knr22@gmail.com")
											.name("Neerja Kurmam"))
							.license(new License()
											.name("Search API"))
							.title("Search API")
							.description("This REST API allows you to perform Insert and Search Documents."));
	}
}
