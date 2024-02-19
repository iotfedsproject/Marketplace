package eu.iotfeds.marketplace.dtos.baas.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.iotfeds.marketplace.dtos.baas.BaasProductDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BaasProductResponse {
    @JsonProperty("Products")
    private List<BaasProductDto> products;

}
