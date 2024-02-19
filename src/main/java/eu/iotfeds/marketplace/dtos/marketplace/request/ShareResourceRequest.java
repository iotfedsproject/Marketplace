package eu.iotfeds.marketplace.dtos.marketplace.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShareResourceRequest {
    private PlatformCredentials platformCredentials;
    private String resourceInternalId;
    private boolean bartered;
    private String federationId;
    private String price;
}
