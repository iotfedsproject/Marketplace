package eu.iotfeds.marketplace.dtos.marketplace.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenExchange {

    private String product_id1;
    private String product_id2;
    private String user_id1;
    private String user_id2;
}
