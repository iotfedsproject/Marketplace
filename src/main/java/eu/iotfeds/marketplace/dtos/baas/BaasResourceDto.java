package eu.iotfeds.marketplace.dtos.baas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaasResourceDto {
    @JsonProperty("Platform")
    private String platform;

    @JsonProperty("Price")
    private String price;

    @JsonProperty("Resource_id")
    private String resourceId;

    @JsonProperty("fed_id")
    private List<String> fedId;

    @JsonProperty("objReputation")
    private double objReputation;

    @JsonProperty("overallReputation")
    private double overallReputation;

    private String owner;

    @JsonProperty("relatedFeds")
    private List<String> relatedFeds;

    @JsonProperty("subjReputation")
    private double subjReputation;

    @JsonProperty("transactionCounter")
    private int transactionCounter;
}
