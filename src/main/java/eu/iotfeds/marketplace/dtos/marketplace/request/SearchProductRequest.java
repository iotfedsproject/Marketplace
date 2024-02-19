package eu.iotfeds.marketplace.dtos.marketplace.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchProductRequest {
    private List<String> sellerId;
    private Double priceMin;
    private Double priceMax;
    private List<String> name;
    private String fedMarketplaceId;
    private List<String> description;
    private List<String> resources;
    private List<String> locationName;
    private List<String> vertical;
    private Double repMin;
    private Double repMax;
    private String sort;
    private Boolean streaming;

    @JsonProperty("credentials")
    private PlatformCredentials platformCredentials;


    public Criteria productCriteria() {
        List<Criteria> criteria = new ArrayList<>();
        if (this.sellerId != null && !this.sellerId.isEmpty()) {
            criteria.add(Criteria.where("sellerId").in(this.sellerId));
        }
        if (this.name != null && !this.name.isEmpty()) {
            criteria.add(Criteria.where("name").in(this.name));
        }
        if (isNonNullAndNonBlank(fedMarketplaceId)) {
            criteria.add(Criteria.where("fedMarketplaceId").regex(this.fedMarketplaceId));
        }
        if (this.description != null && !this.description.isEmpty()) {
            criteria.add(Criteria.where("description").in(this.description));
        }
        if (this.resources != null && !this.resources.isEmpty()) {
            criteria.add(Criteria.where("observationParameters.resourceId").in(this.resources));
        }
        if (this.vertical != null && !this.vertical.isEmpty()) {
            criteria.add(Criteria.where("vertical").in(this.vertical));
        }
        if (this.streaming != null) {
            criteria.add(Criteria.where("streaming").in(this.streaming));
        }
        return new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()]));
    }

    private boolean isNonNullAndNonBlank(String text) {
        return text != null && text.trim().length() != 0;
    }
}
