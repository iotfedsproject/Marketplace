package eu.iotfeds.marketplace.configuration.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("eu.iotfeds.marketplace"))
                .paths(PathSelectors.any())
                .build()
//                .securitySchemes(Collections.singletonList(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()))
                .apiInfo(apiInfo());

    }

//    private SecurityScheme securityScheme() {
//
//        return new ApiKey("JWT", "Authorization", "header");
//
//    }


    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .operationSelector(o -> true) //All paths
                .build();
    }


    ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("IoTFeds Marketplace Service")
                .description("Api documentation for IoTFeds Marketplace service")
                .version("1.2.0")
                .contact(new Contact("Dimitrios Laskaratos", "", "dlaskaratos@intracom-telecom.com"))
                .contact(new Contact("Apostolos Nasiou", "", "anasiou@intracom-telecom.com"))
                .build();
    }
}