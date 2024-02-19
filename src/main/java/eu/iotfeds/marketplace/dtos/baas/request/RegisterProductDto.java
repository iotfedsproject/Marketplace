package eu.iotfeds.marketplace.dtos.baas.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterProductDto {

    private String product_id;
    private String seller_id;
    private List<String> resource_ids;
    private String fedMarketplace_id;
    private Map<String, String> product_details;
}
