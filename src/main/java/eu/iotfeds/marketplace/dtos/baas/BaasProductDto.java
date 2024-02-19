package eu.iotfeds.marketplace.dtos.baas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaasProductDto {
    @JsonProperty("FedMarketplace_id")
    private String fedMarketplaceId;

    @JsonProperty("GlobalMarketplace")
    private boolean globalMarketplace;

    @JsonProperty("Product_details")
    private Map<String, Object> productDetails;

    @JsonProperty("Product_id")
    private String productId;

    @JsonProperty("Reputation")
    private Double reputation;

    @JsonProperty("Resource_ids")
    private Map<String, String> resourceIds;

    @JsonProperty("Seller")
    private String seller;

    private String docType;
    private Object[] subjReputation;

    private int transactionCounter;
}
