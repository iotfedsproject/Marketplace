package eu.iotfeds.marketplace.dtos.baas.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BaasSearchProductRequest {
    private List<String> products;
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
        return "BaasSearchProductRequest{" +
                "products=" + products +
                ", priceMin=" + priceMin +
                ", priceMax=" + priceMax +
                ", repMin=" + repMin +
                ", repMax=" + repMax +
                ", userId='" + userId + '\'' +
                '}';
    }
}
