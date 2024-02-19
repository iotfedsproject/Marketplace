package eu.iotfeds.marketplace.dtos.baas.request;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyProductRequestDto {

    private String product_id;
    private String buyer;
    private String seller;
    private double price;
    private String marketplace;
    private boolean streaming;
    private int access;
    private String data_from;
    private String data_until;
    private String frequency;

}
