package eu.iotfeds.marketplace.dtos.marketplace.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.iotfeds.marketplace.models.CloudResourceL1;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterResourceL1Request {

    @JsonProperty("credentials")
    private PlatformCredentials platformCredentials;

    @JsonProperty("cloudResource")
    private CloudResourceL1 cloudResourceL1;

}

