package eu.iotfeds.marketplace.dtos.marketplace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketInfoDto {

    private String resourceId;
    private String platformId;
    private String serviceUrl;

}
