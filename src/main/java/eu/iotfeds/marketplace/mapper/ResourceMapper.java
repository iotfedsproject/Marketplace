package eu.iotfeds.marketplace.mapper;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.FederatedResource;
import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.iotfeds.marketplace.dtos.baas.BaasResourceDto;
import eu.iotfeds.marketplace.dtos.baas.request.BaasSearchResourceRequest;
import eu.iotfeds.marketplace.dtos.baas.request.BaasShareResourceRequest;
import eu.iotfeds.marketplace.dtos.marketplace.ResourceInfo;
import eu.iotfeds.marketplace.models.DbResourceInfo;
import eu.iotfeds.marketplace.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ResourceMapper {

    @Autowired
    private ResourceRepository resourceRepository;


    public BaasSearchResourceRequest mapToBaasSearchResourcesRequest(String user, List<String> resources,
                                                                     Double priceMin, Double priceMax, Double repMin, Double repMax) {
        BaasSearchResourceRequest baasRequest = new BaasSearchResourceRequest();
        baasRequest.setResources(resources);
        String doubleQuotes = "";
        baasRequest.setPriceMin(priceMin == null ? doubleQuotes : priceMin);
        baasRequest.setPriceMax(priceMax == null ? doubleQuotes : priceMax);
        baasRequest.setRepMin(repMin == null ? doubleQuotes : repMin);
        baasRequest.setRepMax(repMax == null ? doubleQuotes : repMax);
        baasRequest.setUserId(user);
        return baasRequest;
    }

    public BaasShareResourceRequest mapToBaasShareResourceRequest(String user, String resourceId,
                                                                  String price, String fedId) {
        BaasShareResourceRequest baasRequest = new BaasShareResourceRequest();
        baasRequest.setUser(user);
        baasRequest.setDeviceId(resourceId);
        baasRequest.setFedId(fedId);
        baasRequest.setPrice(price);
        return baasRequest;
    }

    public List<String> mapQueryResultToResourceIdsList(List<QueryResourceResult> resources) {
        if(resources == null) {
            return Collections.EMPTY_LIST;
        }
        return resources.stream()
                .map(QueryResourceResult::getId)
                .collect(Collectors.toList());
    }

    public List<String> mapFederatedResourcesToResourceIdsList(List<FederatedResource> resources) {
        return resources.stream()
                .map(FederatedResource::getCloudResource)
                .map(CloudResource::getResource)
                .map(eu.h2020.symbiote.model.cim.Resource::getId)
                .collect(Collectors.toList());
    }

    public List<ResourceInfo> mapQueryResultToResourceInfo(List<QueryResourceResult> resources) {
        List<ResourceInfo> resourceInfoList = new ArrayList<>();
        resources.stream().forEach(resource -> {
            ResourceInfo resourceInfo = new ResourceInfo(resource);
            resourceInfoList.add(resourceInfo);
        });
        return resourceInfoList;
    }

    public List<ResourceInfo> mapFederatedResourcesToResourceInfo(List<FederatedResource> resources) {
        List<ResourceInfo> resourceInfoList = new ArrayList<>();
        resources.stream().forEach(resource -> {
            ResourceInfo resourceInfo = new ResourceInfo();
            resourceInfoList.add(resourceInfo);
        });
        return resourceInfoList;
    }

//    public List<ResourceInfo> mapBaasResourceToResourceInfo(List<BaasResourceDto> resources) {
//        List<ResourceInfo> resourcesInfo = new ArrayList<>();
//        resources.stream()
//                .forEach(resource -> {
//                    ResourceInfo resourceInfo = new ResourceInfo();
//                    resourceInfo.setResourceId(resource.getResourceId());
//                    resourceInfo.setPlatformId(resource.getPlatform());
//                    resourceInfo.setInterWorkingServiceUrl(findInterworkingServiceURL(resource.getResourceId()));
//                    resourcesInfo.add(resourceInfo);
//                });
//        return resourcesInfo;
//    }

    private String findInterworkingServiceURL(String resourceId) {
        Optional<DbResourceInfo> resourceOpt = resourceRepository.findById(resourceId);
        if(resourceOpt.isPresent()) {
            return resourceOpt.get().getResource().getInterworkingServiceURL();
        } else {
            return "";
        }
    }

}
