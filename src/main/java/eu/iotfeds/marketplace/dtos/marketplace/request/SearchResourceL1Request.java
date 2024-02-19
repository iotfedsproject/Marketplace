package eu.iotfeds.marketplace.dtos.marketplace.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.h2020.symbiote.security.communication.payloads.SecurityRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchResourceL1Request {

    private String platform_id;
    private String platform_name;
    private String owner;
    private String name;
    private String id;
    private String description;
    private String location_name;
    private Double location_lat;
    private Double location_long;
    private Integer max_distance;
    private List<String> observed_property;
    private List<String> observed_property_iri;
    private String resource_type;
    private SecurityRequest securityRequest;
    private Boolean should_rank;
    @JsonProperty("price_min")
    private Double priceMin;
    @JsonProperty("price_max")
    private Double priceMax;
    @JsonProperty("rep_min")
    private Double repMin;
    @JsonProperty("rep_max")
    private Double repMax;
    @JsonProperty("credentials")
    private PlatformCredentials platformCredentials;

    public String toQueryParameters() {
        StringBuilder queryBuilder = new StringBuilder("?");
        if(isNonNullAndNonBlank(platform_id)) {
            queryBuilder.append("platform_id=" + platform_id + "&");
        }
        if(isNonNullAndNonBlank(platform_name)) {
            queryBuilder.append("platform_name=" + platform_name + "&");
        }
        if(isNonNullAndNonBlank(owner)) {
            queryBuilder.append("owner=" + owner + "&");
        }

        if(isNonNullAndNonBlank(name)) {
            queryBuilder.append("name=" + name + "&");
        }
        if(isNonNullAndNonBlank(description)) {
            queryBuilder.append("description=" + description + "&");
        }
        if(isNonNullAndNonBlank(id)) {
            queryBuilder.append("id=" + id + "&");
        }
        if(isNonNullAndNonBlank(location_name)) {
            queryBuilder.append("location_name=" + location_name + "&");
        }
        if(location_lat != null) {
            queryBuilder.append("location_lat=" + location_lat + "&");
        }
        if(location_long != null) {
            queryBuilder.append("location_long=" + location_long + "&");
        }
        if(max_distance != null) {
            queryBuilder.append("max_distance=" + max_distance + "&");
        }
        if(observed_property != null && observed_property.size() > 0) {
            String observesPropertyString = extractQueryParamFromList(observed_property);
            queryBuilder.append("observed_property=" + observesPropertyString + "&");
        }
        if(observed_property_iri != null && observed_property_iri.size() > 0) {
            String observedPropertyIriString = extractQueryParamFromList(observed_property_iri);
            queryBuilder.append("observed_property_iri=" + observedPropertyIriString + "&");
        }
        if(isNonNullAndNonBlank(resource_type)) {
            queryBuilder.append("resource_type=" + resource_type + "&");
        }
        if(securityRequest != null) {
            queryBuilder.append("securityRequest=" + securityRequest + "&");
        }
        if(should_rank != null) {
            queryBuilder.append("should_rank=" + should_rank + "&");
        }
        return queryBuilder.toString();
    }
    private String extractQueryParamFromList(List<String> list) {
        if(list.size() == 1) {
            return list.get(0);
        } else {
            return list.stream().map(Object::toString)
                    .collect(Collectors.joining(","));
        }
    }
    private boolean isNonNullAndNonBlank(String text) {
        return text != null && text.trim().length() != 0;
    }
}
