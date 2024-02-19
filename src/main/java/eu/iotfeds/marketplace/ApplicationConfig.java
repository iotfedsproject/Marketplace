package eu.iotfeds.marketplace;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.Duration;

@Configuration
@EnableSwagger2
public class ApplicationConfig {

    @Value("${symbioteapi.root_url}")
    private String iotfedsApiRootUrl;

    @Value("${iotfeds.baas.root_url}")
    private String baasRootUrl;

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.setConnectTimeout(Duration.ofMillis(1 * 1000)).setReadTimeout(Duration.ofMillis(10 * 1000)).build();
    }

    @Bean(name = "iotfedsapiWebClient")
    protected WebClient fedsApiWebClient() {
        return WebClient.builder()
                .baseUrl(iotfedsApiRootUrl)
                .build();
    }

    @Bean(name = "baasWebClient")
    protected WebClient baasWebClient() {
        return WebClient.builder()
                .baseUrl(baasRootUrl)
                .build();
    }
}
