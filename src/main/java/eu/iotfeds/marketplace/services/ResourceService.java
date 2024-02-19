package eu.iotfeds.marketplace.services;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.FederatedResource;
import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.model.cim.Observation;
import eu.h2020.symbiote.security.accesspolicies.common.AccessPolicyType;
import eu.h2020.symbiote.security.accesspolicies.common.singletoken.SingleTokenAccessPolicySpecifier;
import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;
import eu.iotfeds.marketplace.dtos.baas.BaasResourceDto;
import eu.iotfeds.marketplace.dtos.baas.request.BaasSearchResourceRequest;
import eu.iotfeds.marketplace.dtos.baas.request.BaasShareResourceRequest;
import eu.iotfeds.marketplace.dtos.iotfedsapi.ResourceInfoDto;
import eu.iotfeds.marketplace.dtos.marketplace.ResourceInfo;
import eu.iotfeds.marketplace.dtos.marketplace.request.*;
import eu.iotfeds.marketplace.exception.ApiError;
import eu.iotfeds.marketplace.exception.MarketplaceNotFoundException;
import eu.iotfeds.marketplace.http.baas.BaasRest;
import eu.iotfeds.marketplace.http.iotfedsapi.IotfedsApiRest;
import eu.iotfeds.marketplace.mapper.ResourceMapper;
import eu.iotfeds.marketplace.models.CloudResourceL1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResourceService {
    @Autowired
    private BaasRest baasRest;
    @Autowired
    private IotfedsApiRest iotfedsApiRest;
    @Autowired
    private ResourceMapper resourceMapper;
    @Value("${baas.integration:true}")
    private boolean baasIntegration;
    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

//    public List<ResourceInfo> searchResourcesL1(Principal principal, SearchResourceL1Request request) throws MarketplaceNotFoundException {
//        PlatformCredentials platformCredentials = resourceMapper.mapToPlatformCredentials(principal);
    public ResponseEntity<?> searchResourcesL1(SearchResourceL1Request request) throws MarketplaceNotFoundException {
        log.debug("Received the request " + request.toQueryParameters());
        List<QueryResourceResult> resources = iotfedsSearchResourceL1(request);
        List<String> resourceIds = resourceMapper.mapQueryResultToResourceIdsList(resources);
        if (baasIntegration) {
            String user = request.getPlatformCredentials().getUsername();
            BaasSearchResourceRequest baasRequest = resourceMapper.mapToBaasSearchResourcesRequest(user, resourceIds, request.getPriceMin(), request.getPriceMax(),
                    request.getRepMin(), request.getRepMax());
            ResponseEntity<?> baasResponse = baasSearchResources(baasRequest);
            if(HttpStatus.OK == baasResponse.getStatusCode()) {
                List<BaasResourceDto> baasResources = (List<BaasResourceDto>) baasResponse.getBody();
                List<ResourceInfo> resourceInfoList = mergeIotfedsWithBaasSearch(resources, baasResources);
                return new ResponseEntity<>(resourceInfoList, HttpStatus.OK);
            } else {
                return baasResponse;
            }
        } else {
            List<ResourceInfo> resourceInfoList = resourceMapper.mapQueryResultToResourceInfo(resources);
            return new ResponseEntity<>(resourceInfoList, HttpStatus.OK);
        }
    }

//    public List<ResourceInfo> searchResourcesL2(Principal principal, SearchResourceL2Request request) throws MarketplaceNotFoundException {
//    PlatformCredentials platformCredentials = resourceMapper.mapToPlatformCredentials(principal);
    public ResponseEntity<?> searchResourcesL2(SearchResourceL2Request request) throws MarketplaceNotFoundException {
        List<FederatedResource> resources = iotfedsSearchResourceL2(request);
//        List<String> resourceIds = resourceMapper.mapFederatedResourcesToResourceIdsList(resources);
//        if (baasIntegration && !resourceIds.isEmpty()) {
//            String user = request.getPlatformCredentials().getUsername();
//            BaasSearchResourceRequest baasRequest = resourceMapper.mapToBaasSearchResourcesRequest(user, resourceIds, request.getPriceMin(), request.getPriceMax(),
//                    request.getRepMin(), request.getRepMax());
//            ResponseEntity<?> baasResponse = baasSearchResources(baasRequest);
//            if(HttpStatus.OK == baasResponse.getStatusCode()) {
//                List<BaasResourceDto> baasResources = (List<BaasResourceDto>) baasResponse.getBody();
//                List<ResourceInfo> resourceInfoList = mergeIotfedsWithBaasSearch(resources, baasResources);
//                return new ResponseEntity<>(resourceInfoList, HttpStatus.OK);
//                return new ResponseEntity<>(null, HttpStatus.OK);
//            } else {
//                return baasResponse;
//            }
//        } else {
//            List<ResourceInfo> resourceInfoList = resourceMapper.mapFederatedResourcesToResourceInfo(resources);
//            return new ResponseEntity<>(resourceInfoList, HttpStatus.OK);
//        }
        List<ResourceInfo> resourceInfoList = resourceMapper.mapFederatedResourcesToResourceInfo(resources);
        return new ResponseEntity<>(resourceInfoList, HttpStatus.OK);
    }

    public List<Observation> iotfedsAccessL1Id(String resourceId, String fromDate, String toDate, Integer topObservations, IotfedsAccessL1Request request) {
        return iotfedsApiRest.accessL1ResourceWithResourceId(resourceId, fromDate, toDate, topObservations, request);
    }

    public List<Observation> iotfedsAccessL1InternalId(String resourceInternalId, String fromDate, String toDate, Integer topObservations, IotfedsAccessL1Request request) {
        return iotfedsApiRest.accessL1ResourceWithInternalId(resourceInternalId, fromDate, toDate, topObservations, request);
    }

    public Observation iotfedsAccessL2InternalId(String resourceInternalId, IotfedsAccessL2Request request) {
        return iotfedsApiRest.accessL2ResourceWithInternalId(resourceInternalId, request);
    }

    public ResponseEntity<?> iotfedsRegisterL1(RegisterResourceL1Request request) {
        CloudResourceL1 cloudResource = request.getCloudResourceL1();
        try {
            Map<String,String> requiredClaims = new HashMap<>();
            requiredClaims.put("iss", "SymbIoTe_Core_AAM");
            requiredClaims.put("sub", "marketplace");
            SingleTokenAccessPolicySpecifier accessPolicy = new SingleTokenAccessPolicySpecifier(AccessPolicyType.SLHTIBAP, requiredClaims);
            cloudResource.setAccessPolicy(accessPolicy);
        } catch (InvalidArgumentsException e) {
            String error = "Error occurred, cannot setup accessPolicy for the registered resource." + e;
            log.error(error);
            return new ResponseEntity<String>(error, e.getStatusCode());
        }
        try {
            SingleTokenAccessPolicySpecifier filteringPolicy = new SingleTokenAccessPolicySpecifier(AccessPolicyType.PUBLIC, null);
            cloudResource.setFilteringPolicy(filteringPolicy);
        } catch (InvalidArgumentsException e) {
            String error = "Error occurred, cannot setup filteringPolicy for the registered resource." + e;
            log.error(error);
            return new ResponseEntity<String>(error, e.getStatusCode());
        }
        return ResponseEntity.ok(iotfedsApiRest.registerL1Resource(request));
    }

    public ResponseEntity<?> iotfedsShareL2(ShareResourceRequest request) {
        String resourceId = iotfedsApiRest.getResourceIdFromInternalID(new ResourceInfoDto(request.getResourceInternalId(),
                request.getPlatformCredentials().getLocalPlatformId()));
        log.debug("resourceId = "+resourceId);
        if(resourceId == null || "".equals(resourceId)) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, List<CloudResource>> sharedResources = iotfedsApiRest.shareL2Resource(request);
        if(sharedResources == null || sharedResources.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            if(baasIntegration) {
                String user = request.getPlatformCredentials() == null ? "" : request.getPlatformCredentials().getUsername();
                log.debug("sharer = "+user);
                BaasShareResourceRequest baasRequest = resourceMapper.mapToBaasShareResourceRequest(user,
                        resourceId,
                        request.getPrice(),
                        request.getFederationId());
                String baasResponse = baasRest.shareResource(baasRequest);
                log.debug("baasResponse = " + baasResponse);
                if(baasResponse == null) { //TODO check the response body
                    iotfedsApiRest.unShareL2Resource(request);
                    return ResponseEntity.badRequest().build();
                }
            }
            return ResponseEntity.ok(sharedResources);
        }
    }

    private List<QueryResourceResult> iotfedsSearchResourceL1(SearchResourceL1Request request) {
        try {
            PlatformCredentials platformCredentials = getMarketplaceCredentials();
            return iotfedsApiRest.searchL1Resources(platformCredentials, request);
        } catch (Exception exc) {
            log.error("Error in IoTFeds "+ exc);
            throw new MarketplaceNotFoundException("Not found resource(s) in IoTFeds.");
        }
    }

    private List<FederatedResource> iotfedsSearchResourceL2(SearchResourceL2Request request) {
        try {
            PlatformCredentials platformCredentials = getMarketplaceCredentials();
            return iotfedsApiRest.searchL2Resources(platformCredentials, request);
        } catch (Exception exc) {
            log.error("Error in IoTFeds "+ exc);
            throw new MarketplaceNotFoundException("Not found resource(s) in IoTFeds.");
        }
    }

    private ResponseEntity<?> baasSearchResources(BaasSearchResourceRequest request) {
        try {
            return baasRest.searchResource(request);
        } catch (Exception exc) {
            log.debug("#### error occured in baas = "+exc.getMessage());
            List<String> errors = Arrays.asList(exc.getMessage());
            ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Exception occured in baas.", errors);
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
    }

    private PlatformCredentials getMarketplaceCredentials() {
        PlatformCredentials credentials = new PlatformCredentials();
        credentials.setLocalPlatformId("SymbIoTe_Core_AAM");
        credentials.setUsername("marketplace");
        credentials.setPassword("marketplace");
        String generatedString = "test-client";
        credentials.setClientId(generatedString);
        log.debug("Search with credentials " + credentials.toString());
        return credentials;
    }

    private List<ResourceInfo> mergeIotfedsWithBaasSearch(List<QueryResourceResult> iotfedsResources, List<BaasResourceDto> baasSearchResources) {
        List<ResourceInfo> resourceInfoList = new ArrayList<>();
        baasSearchResources.stream()
                .forEach(baasResourceDto -> {
                    Optional<QueryResourceResult> iotfedsResource = iotfedsResources.stream()
                            .filter(resource -> resource.getId().equals(baasResourceDto.getResourceId()))
                            .findFirst();
                    if (iotfedsResource.isPresent()) {
                        ResourceInfo resourceInfo = new ResourceInfo(iotfedsResource.get());
                        resourceInfoList.add(resourceInfo);
                    }
                });
        return resourceInfoList;
    }

}
