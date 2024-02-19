package eu.iotfeds.marketplace.dtos.baas.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaasAccessToken {
    @JsonProperty("AccessTimes")
    private String accessTimes;
    @JsonProperty("DataAvailableFrom")
    private String dataAvailableFrom;
    @JsonProperty("DataAvailableUntil")
    private String dataAvailableUntil;
    @JsonProperty("Frequency")
    private String frequency;
    @JsonProperty("Marketplace")
    private String marketplace;
    @JsonProperty("ValidUntil")
    private String validUntil;
    @JsonProperty("toBeExchanged")
    private Boolean toBeExchanged;
    @JsonProperty("userHasRated")
    private Boolean userHasRated;
}
