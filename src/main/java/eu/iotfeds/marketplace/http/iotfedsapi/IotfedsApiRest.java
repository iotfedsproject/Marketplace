package eu.iotfeds.marketplace.http.iotfedsapi;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.FederatedResource;
import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.model.cim.Observation;
import eu.iotfeds.marketplace.dtos.iotfedsapi.ResourceInfoDto;
import eu.iotfeds.marketplace.dtos.marketplace.request.*;
import eu.iotfeds.marketplace.dtos.marketplace.response.ResourceRegistrationResponse;
import eu.iotfeds.marketplace.utils.Constants;
import org.apache.jena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class IotfedsApiRest {
    @Autowired
    @Qualifier("iotfedsapiWebClient")
    private WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(IotfedsApiRest.class);

    public String getResourceIdFromInternalID(ResourceInfoDto resourceInfo) {
        return webClient.post()
                .uri(Constants.IOTFEDSAPI_RESOURCES_SEARCH_BY_INTERNAL_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(resourceInfo))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public List<QueryResourceResult> searchL1Resources(PlatformCredentials credentials, SearchResourceL1Request request) {
        return webClient.post()
                .uri(Constants.IOTFEDSAPI_RESOURCES_SEARCH_L1 + request.toQueryParameters())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(credentials))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<QueryResourceResult>>() {}).block();
    }

    public List<FederatedResource> searchL2Resources(PlatformCredentials credentials, SearchResourceL2Request request) {
        StringBuilder uriBuilder = new StringBuilder(Constants.IOTFEDSAPI_RESOURCES_SEARCH_L2 + request.toQueryParameters());
        Mono<List<FederatedResource>> response = webClient.post()
                .uri(uriBuilder.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(credentials))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<FederatedResource>>() {});
        return response.block();
    }

    public List<Observation> accessL1ResourceWithResourceId(String resourceId, String fromDate, String toDate, Integer topObservations, IotfedsAccessL1Request request) {
        StringBuilder uriBuilder = new StringBuilder(Constants.ACCESSL1_ID_FEDSAPI + resourceId + "?");
        if(fromDate != null) {
            uriBuilder.append("fromDate=" + fromDate + "&");
        }
        if(toDate != null) {
            uriBuilder.append("toDate=" + toDate + "&");
        }
        if(topObservations != null) {
            uriBuilder.append("top=" + topObservations);
        }
        log.debug("Send the post request at " + uriBuilder.toString());
        return webClient.post()
                .uri(uriBuilder.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Observation>>() {})
                .block();
    }

    public List<Observation> accessL1ResourceWithInternalId(String resourceInternalId, String fromDate, String toDate, Integer topObservations, IotfedsAccessL1Request access) {
        StringBuilder uriBuilder = new StringBuilder(Constants.ACCESSL1_INTERNAL_ID_FEDSAPI + resourceInternalId + "?");
        if(fromDate != null) {
            uriBuilder.append("fromDate=" + fromDate + "&");
        }
        if(toDate != null) {
            uriBuilder.append("toDate=" + toDate + "&");
        }
        if(topObservations != null) {
            uriBuilder.append("top=" + topObservations);
        }
        log.debug("Send the post request at " + uriBuilder.toString());
        return webClient.post()
                .uri(uriBuilder.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(access))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Observation>>() {})
                .block();
    }

    public Observation accessL2ResourceWithInternalId(String resourceInternalId, IotfedsAccessL2Request access) {
        return webClient.post()
                .uri(Constants.ACCESSL2_IOT_FEDSAPI + resourceInternalId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(access))
                .retrieve()
                .bodyToMono(Observation.class)
                .block();
    }

    public ResourceRegistrationResponse registerL1Resource(RegisterResourceL1Request request) {
        return webClient.post()
                .uri(Constants.ADD_L1_FEDSAPI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResourceRegistrationResponse>() {})
                .block();
    }

    public Map<String, List<CloudResource>> shareL2Resource(ShareResourceRequest request) {
        return webClient.post()
                .uri(Constants.SHARE_L2_FEDSAPI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<CloudResource>>>() {})
                .block();
    }

    public Map<String, List<CloudResource>> unShareL2Resource(ShareResourceRequest request) {
        return webClient.post()
                .uri(Constants.UNSHARE_L2_FEDSAPI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<CloudResource>>>() {})
                .block();
    }

    public JsonObject getResourceUrl(String resourceId, List<String> platformIds) {
        StringBuilder uriBuilder = new StringBuilder("/resources/resourceurl")
                .append("?id="+resourceId);
        return webClient.get()
                .uri(uriBuilder.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<JsonObject>() {})
                .block();
    }

}
