package eu.iotfeds.marketplace.dtos.marketplace.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IotfedsAccessL2Request {
    private PlatformCredentials platformCredentials;
    private String platformId;
    private String federationId;
}
