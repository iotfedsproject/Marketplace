package eu.iotfeds.marketplace.dtos.baas.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaasShareResourceRequest {
    private String user;
    @JsonProperty("device_id")
    private String deviceId;
    private String price;
    @JsonProperty("fed_id")
    private String fedId;
}
