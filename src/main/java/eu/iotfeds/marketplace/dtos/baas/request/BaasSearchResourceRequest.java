package eu.iotfeds.marketplace.dtos.baas.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BaasSearchResourceRequest {
    private List<String> resources;
    @JsonProperty("price_min")
    private Object priceMin;
    @JsonProperty("price_max")
    private Object priceMax;
    @JsonProperty("rep_min")
    private Object repMin;
    @JsonProperty("rep_max")
    private Object repMax;
    @JsonProperty("user_id")
    private String userId;

    @Override
    public String toString() {
        return "BaasSearchResourceRequest{" +
                "resources=" + resources +
                ", priceMin=" + priceMin +
                ", priceMax=" + priceMax +
                ", repMin=" + repMin +
                ", repMax=" + repMax +
                ", userId='" + userId + '\'' +
                '}';
    }
}
