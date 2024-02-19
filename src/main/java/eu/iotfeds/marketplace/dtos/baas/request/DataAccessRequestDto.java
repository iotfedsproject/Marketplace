package eu.iotfeds.marketplace.dtos.baas.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataAccessRequestDto {

    private String product_id;
    private String user_id;
    private String data_from;
    private String data_until;
    private String frequency;
    private String req_observations;
}
