package eu.iotfeds.marketplace.dtos.marketplace.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyProductModel {

    private String product_id;
    private String buyer;
    private int access;
    private String data_from;
    private String data_until;
    private String frequency;
//    private boolean time_based;
}
