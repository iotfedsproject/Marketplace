package eu.iotfeds.marketplace.dtos.baas.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckAccessResponse {

    @JsonProperty("AccessTimes")
    private int accessTimes;

    @JsonProperty("DataAvailableFrom")
    private String dataAvailableFrom;

    @JsonProperty("DataAvailableUntil")
    private String dataAvailableUntil;

    @JsonProperty("Marketplace")
    private String marketplaceId;

    @JsonProperty("ValidUntil")
    private String validUntil;

    private boolean toBeExchanged;

    private boolean userHasRated;
}
