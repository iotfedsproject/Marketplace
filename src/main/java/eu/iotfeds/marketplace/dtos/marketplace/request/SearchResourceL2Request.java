package eu.iotfeds.marketplace.dtos.marketplace.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchResourceL2Request {

    //A list with the resource names
    private List<String> name;

    //The resource description
    private List<String> description;

    // The id for identifying the resource in the symbIoTe federation (aggregationId)
    private List<String> id;

    // The list of federation ids
    private List<String> federationId;

    // Property observed by resource (sensor)
    private List<String> observesProperty;

    // Type of queried resource
    private String resourceType;

    // Name of resource location(s)
    private List<String> locationName;

    // Latitude of resource location. It concerns WGS84 locations for devices
    private Double locationLat;

    // Longitude of resource location. It concerns WGS84 locations for devices
    private Double locationLong;

    // maximal distance from specified resource latitude and longitude (in meters)
    private Double maxDistance;

    private Double adaptiveTrust;

    private Double resourceTrust;

    // the field to be used for sorting the resources
    private String sort;

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

        String nameString = extractQueryParamFromList(name);
        queryBuilder.append("name=" + nameString + "&");

        String descriptionString = extractQueryParamFromList(description);
        queryBuilder.append("description=" + descriptionString + "&");

        String idString = extractQueryParamFromList(id);
        queryBuilder.append("id=" + idString + "&");

        String federationIdString = extractQueryParamFromList(federationId);
        queryBuilder.append("federationId=" + federationIdString + "&");

        String observesPropertyString = extractQueryParamFromList(observesProperty);
        queryBuilder.append("observes_property=" + observesPropertyString + "&");

        queryBuilder.append("resource_type=" + resourceType + "&");

        String locationNameString = extractQueryParamFromList(locationName);
        queryBuilder.append("location_name=" + locationNameString + "&");

        Double latitude = locationLat == null ? Double.NaN : locationLat;
        queryBuilder.append("location_lat=" + latitude + "&");

        Double longitude = locationLong == null ? Double.NaN : locationLong;
        queryBuilder.append("location_long=" + longitude + "&");

        Double radius = maxDistance == null ? Double.NaN : maxDistance;
        queryBuilder.append("max_distance=" + radius + "&");

        Double resTrust = resourceTrust == null ? Double.NaN : resourceTrust;
        queryBuilder.append("resource_trust=" + resTrust + "&");

        Double adTrust = adaptiveTrust == null ? Double.NaN : adaptiveTrust;
        queryBuilder.append("adaptive_trust=" + adTrust + "&");

        queryBuilder.append("sort=" + sort);

        return queryBuilder.toString();
    }
    private String extractQueryParamFromList(List<String> list) {
        if(list == null || list.size() == 0) {
            return "";
        } else if(list.size() == 1) {
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
