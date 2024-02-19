package eu.iotfeds.marketplace.dtos.baas.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.iotfeds.marketplace.dtos.baas.BaasResourceDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BaasSearchResourceResponse {

    @JsonProperty("Resources")
    private List<BaasResourceDto> resources;

}
