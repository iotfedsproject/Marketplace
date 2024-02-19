package eu.iotfeds.marketplace.dtos.iotfedsapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceInfoDto {
    private String internalId;

    private String platformId;
}
