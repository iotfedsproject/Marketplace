package eu.iotfeds.marketplace.dtos.baas.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalcProductPriceDto {

    private String product_id;
    private String user_id;
    private String marketplace;
    private boolean streaming;
    private int access;
    private String data_from;
    private String data_until;
    private String frequency;
}
