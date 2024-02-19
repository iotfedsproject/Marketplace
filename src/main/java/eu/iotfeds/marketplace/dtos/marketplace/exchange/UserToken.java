package eu.iotfeds.marketplace.dtos.marketplace.exchange;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserToken {

    private String productId;
    private int accessTimes;
    private String dataAvailableFrom;
    private String dataAvailableUntil;
    private String marketplaceId;
//    private String validUntil;
    private boolean toBeExchanged;
    private boolean userHasRated;
}
