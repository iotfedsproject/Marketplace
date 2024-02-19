package eu.iotfeds.marketplace.dtos.baas.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyProductResponse {

    @JsonProperty("TransactionID")
    private String transactionId;

    @JsonProperty("Buyer")
    private String buyer;

    @JsonProperty("Seller")
    private String seller;

    @JsonProperty("ProductID")
    private String productId;

    @JsonProperty("DateBought")
    private String dateBought;
}
