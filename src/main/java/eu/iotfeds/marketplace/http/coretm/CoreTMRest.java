package eu.iotfeds.marketplace.http.coretm;


import eu.iotfeds.marketplace.dtos.baas.request.ProductRatingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CoreTMRest {

    @Value("${iotfeds.core_trust_manager.root_url}")
    private String coreTmUrl;

    @Autowired
    private RestTemplate restTemplate;

    public String submitRating(ProductRatingDto productRatingDto){

        ResponseEntity<String> resp = restTemplate.exchange(coreTmUrl+"/submit-rating", HttpMethod.GET, new HttpEntity<>(productRatingDto, new HttpHeaders()),
                new ParameterizedTypeReference<String>() {
                });

        return resp.getBody();
    }


}
