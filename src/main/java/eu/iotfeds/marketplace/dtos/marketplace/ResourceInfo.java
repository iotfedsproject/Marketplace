package eu.iotfeds.marketplace.dtos.marketplace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import eu.h2020.symbiote.cloud.model.internal.FederationInfoBean;
import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.model.cim.Property;
import eu.h2020.symbiote.model.cim.Resource;
import eu.iotfeds.marketplace.models.DbResourceInfo;
import eu.iotfeds.marketplace.models.Product;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ResourceInfo {
    private java.lang.String platformId;
    private java.lang.String platformName;
    private java.lang.String owner;
    private java.lang.String name;
    private java.lang.String resourceId;
    private java.lang.String description;
    private java.lang.String locationName;
    private java.lang.Double locationLatitude;
    private java.lang.Double locationLongitude;
    private java.lang.Double locationAltitude;
    private List<String> observedProperties;
    private java.util.List<java.lang.String> resourceType;
    private java.util.List<eu.h2020.symbiote.model.cim.Parameter> inputParameters;
    private java.util.List<eu.h2020.symbiote.model.cim.Capability> capabilities;
    private java.lang.Float ranking;
    public ResourceInfo() {}

    public ResourceInfo(QueryResourceResult resource) {
        resourceId = resource.getId();
        platformId = resource.getPlatformId();
        platformName = resource.getPlatformName();
        owner = resource.getOwner();
        name = resource.getName();
        description = resource.getDescription();
        locationName = resource.getLocationName();
        locationLatitude = resource.getLocationLatitude();
        locationLongitude = resource.getLocationLongitude();
        locationAltitude = resource.getLocationAltitude();
        observedProperties = resource.getObservedProperties().stream().map(Property::getName).collect(Collectors.toList());
        resourceType = resource.getResourceType();
        inputParameters = resource.getInputParameters();
        capabilities = resource.getCapabilities();
        ranking = resource.getRanking();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceInfo)) return false;
        ResourceInfo that = (ResourceInfo) o;
        return Objects.equal(getResourceId(), that.getResourceId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getResourceId());
    }
}
