package eu.iotfeds.marketplace.dtos.baas.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaasDecreaseAccessRequest {

    @JsonProperty("product_id")
    private String product_id;

    @JsonProperty("user_id")
    private String user_id;

    @JsonProperty("times_used")
    private String timesUsed;

}
