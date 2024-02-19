package eu.iotfeds.marketplace.dtos.marketplace.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IotfedsAccessL1Request {
    private PlatformCredentials platformCredentials;

    private String remotePlatformId;
}
