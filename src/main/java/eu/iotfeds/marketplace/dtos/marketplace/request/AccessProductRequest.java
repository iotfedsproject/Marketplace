package eu.iotfeds.marketplace.dtos.marketplace.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessProductRequest {

    @JsonProperty("credentials")
    private PlatformCredentials platformCredentials;

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("dateFrom")
    private String dateFrom;

    @JsonProperty("dateTo")
    private String dateTo;

    @JsonProperty("reqObservations")
    private String reqObservations;
}
