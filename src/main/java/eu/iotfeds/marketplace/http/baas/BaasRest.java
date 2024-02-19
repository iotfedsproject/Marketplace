package eu.iotfeds.marketplace.http.baas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.iotfeds.marketplace.dtos.baas.BaasProductDto;
import eu.iotfeds.marketplace.dtos.baas.BaasResourceDto;
import eu.iotfeds.marketplace.dtos.baas.request.*;
import eu.iotfeds.marketplace.dtos.baas.response.BuyProductResponse;
import eu.iotfeds.marketplace.dtos.baas.response.CheckAccessResponse;
import eu.iotfeds.marketplace.dtos.marketplace.exchange.TokenExchange;
import eu.iotfeds.marketplace.dtos.marketplace.response.ProductPriceDto;
import eu.iotfeds.marketplace.exception.ApiError;
import eu.iotfeds.marketplace.models.ObservationParameters;
import eu.iotfeds.marketplace.models.Product;
import eu.iotfeds.marketplace.models.http.GenericHttpResponse;
import eu.iotfeds.marketplace.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BaasRest {

    @Autowired
    @Qualifier("baasWebClient")
    private WebClient webClient;

    @Value("${iotfeds.baas.root_url}")
    private String baasRootUrl;

    private final String baasProducts = "/products";

    private final String baasUser = "/user";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    public GenericHttpResponse buyProduct(BuyProductRequestDto buyProductRequestDto, String seller) throws JsonProcessingException {

            ResponseEntity<String> resp = restTemplate.exchange(baasRootUrl + baasProducts + "/buy_product", HttpMethod.POST, new HttpEntity<>(buyProductRequestDto, new HttpHeaders()),
                    new ParameterizedTypeReference<String>() {
                    });

            if(resp.getStatusCode()==HttpStatus.OK) {
                JSONObject receipt = new JSONObject(resp.getBody()).getJSONObject("Receipt");
                BuyProductResponse buyProductResponse = objectMapper.readValue(receipt.toString(), BuyProductResponse.class);
                return new GenericHttpResponse(HttpStatus.OK, buyProductResponse);
            }
            else
                return new GenericHttpResponse(resp.getStatusCode(), null);
    }

    public Map<String, List<BaasProductDto>> getGlobalProducts(String user){
        StringBuilder uriBuilder = new StringBuilder(baasRootUrl + Constants.BAAS_PRODUCTS + Constants.BAAS_GET_GLOBAL_PRODUCTS)
                .append("?user_id=" + user);
        return webClient.get()
                .uri(uriBuilder.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<BaasProductDto>>>() {})
                .block();
    }

    public Map<String, List<BaasProductDto>> getFederatedProducts(String user, String federation){
        StringBuilder uriBuilder = new StringBuilder(baasRootUrl + Constants.BAAS_PRODUCTS + Constants.BAAS_GET_FEDERATED_PRODUCTS)
                .append("?user_id=" + user)
                .append("&fed_id=" + federation);
        return webClient.get()
                .uri(uriBuilder.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<BaasProductDto>>>() {})
                .block();
    }

    public ResponseEntity<?> searchProduct(BaasSearchProductRequest dto){
        log.info("BaasSearchProductRequest: "+dto);
        String url = baasRootUrl + Constants.BAAS_PRODUCTS + Constants.SEARCH_MARKETPLACE;
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST, new HttpEntity<>(dto, new HttpHeaders()), String.class);
        if(HttpStatus.OK == response.getStatusCode()) {
            log.debug("####response = "+response);
            String respBody = response.getBody();
            log.debug("####response body = "+respBody);
            JSONObject jsonBody = new JSONObject(respBody);
            log.debug("####jsonBody = "+jsonBody);
            if("{}".equals(respBody)) {
                log.debug("####empty body");
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            } else {
                log.debug("####full body");
                try {
                    JsonNode jsonArray = objectMapper.readTree(String.valueOf((jsonBody.get("Products"))));
                    log.debug("####jsonArray = " + jsonArray);
                    List<BaasProductDto> products = new ArrayList<>();
                    for (JsonNode element : jsonArray) {
                        BaasProductDto productDto = objectMapper.treeToValue(element, BaasProductDto.class);
                        products.add(productDto);
                    }
                    return new ResponseEntity<>(products, HttpStatus.OK);
                } catch (Exception exc) {
                    log.debug("Error occured while parsing the baas response." + exc);
                    throw new RuntimeException(exc);
                }
            }
        } else {
            List<String> errors = Arrays.asList("Exception occured in baas.");
            ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Exception occured in baas.", errors);
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> searchResource(BaasSearchResourceRequest dto){
        ResponseEntity<String> response = restTemplate.exchange(baasRootUrl + Constants.BAAS_PRODUCTS + Constants.SEARCH_RESOURCES_IN_MARKETPLACE,
                HttpMethod.POST, new HttpEntity<>(dto, new HttpHeaders()), String.class);
        if(HttpStatus.OK == response.getStatusCode()) {
            log.debug("####response = "+response);
            String respBody = response.getBody();
            log.debug("####response body = "+respBody);
//        JSONObject body = new JSONObject("{\n" +
//                "    \"error\": \"Search for resources failed ...Error: Minimum price or reputation cannot be bigger than the maximum price or reputation\"\n" +
//                "}");
//        ResponseEntity<JSONObject> response = new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.FORBIDDEN);
            assert respBody != null;
            JSONObject jsonBody = new JSONObject(respBody);
            log.debug("####jsonBody = "+jsonBody);

            if("{}".equals(respBody)) {
                log.debug("####empty body");
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            } else {
                log.debug("####full body");
                try {
                    JsonNode jsonArray = objectMapper.readTree(String.valueOf((jsonBody.get("Resources"))));
                    log.debug("####jsonArray = "+jsonArray);
                    List<BaasResourceDto> resources = new ArrayList<>();
                    for (JsonNode element : jsonArray) {
                        BaasResourceDto resource = objectMapper.treeToValue(element, BaasResourceDto.class);
                        resources.add(resource);
                    }
                    return new ResponseEntity<>(resources, HttpStatus.OK);
                } catch (Exception exc) {
                    log.debug("Error occured while parsing the baas response." + exc);
                    throw new RuntimeException(exc);
                }
            }
        } else {
            List<String> errors = Arrays.asList("Exception occured in baas.");
            ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Exception occured in baas.", errors);
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
    }

    public String shareResource(BaasShareResourceRequest dto){
        return webClient.patch()
                .uri(Constants.BAAS_PRODUCTS + Constants.BAAS_ADD_RESOURCE_TO_FEDERATION)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public CheckAccessResponse subscribe(String productId, String userId, String frequency) throws JsonProcessingException {
        DataAccessRequestDto dataAccessRequestDto = new DataAccessRequestDto(
                productId,
                userId,
                "",
                "",
                frequency,
                "");

        log.info(objectMapper.writeValueAsString(dataAccessRequestDto));

        JSONObject ob = restTemplate.
                exchange(baasRootUrl+baasProducts+"/check_access",
                        HttpMethod.POST,
                        new HttpEntity<>(
                                dataAccessRequestDto,
                                new HttpHeaders()),
                new ParameterizedTypeReference<JSONObject>() {
                }).getBody();

        CheckAccessResponse checkAccessResponse = objectMapper.readValue((String)ob.get("AccessToken"), CheckAccessResponse.class);
        return checkAccessResponse;
    }

    public String getUserTokens(String userId) throws Exception{
        ResponseEntity<String> response =  restTemplate.exchange(baasRootUrl+baasProducts+"/get_user_tokens?user_id="+userId, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<String>() {
                });

        if(response.getStatusCode()== HttpStatus.OK)
            return response.getBody();
        else {
            log.info("Error getting user token list with code: [{}] and reason: [{}]", response.getStatusCode(), response.getBody());
            throw new Exception();
        }
    }

    public ResponseEntity registerProductToBaas(Product product) throws JsonProcessingException {
        log.info("Registering product to blockchain");
        RegisterProductDto registerProductDto = new RegisterProductDto(
                product.getId(),
                product.getSellerId(),
                product.getObservationParameters()
                        .stream()
                        .map(ObservationParameters::getResourceId)
                        .collect(Collectors.toList()),
                product.getFedMarketplaceId(),
                null
        );

        return restTemplate.exchange(
                baasRootUrl+baasProducts+"/create_product",
                HttpMethod.POST,
                new HttpEntity<>(registerProductDto,
                new HttpHeaders()),
                new ParameterizedTypeReference<String>() {
                });

    }

    public ProductPriceDto calcProductPrice(CalcProductPriceDto calcProductPriceDto)  {
        log.info("Calculating product price with product ID: [{}]", calcProductPriceDto.getProduct_id());

        ResponseEntity<ProductPriceDto> calc_resp = restTemplate.exchange(baasRootUrl+baasProducts+"/calc_product_price", HttpMethod.POST, new HttpEntity<>(calcProductPriceDto, new HttpHeaders()),
                new ParameterizedTypeReference<ProductPriceDto>() {
                });


        return calc_resp.getBody();
    }

    public BaasResourceDto getResourceFromBaas(String resourceId){
        return restTemplate.exchange(
                baasRootUrl+baasProducts+"/get_resource_info/?resource_id="+resourceId,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<BaasResourceDto>() {
                }).getBody();
    }

    public ResponseEntity<String> deleteProduct(String product_id){
        return restTemplate.exchange(
                baasRootUrl+baasProducts+"/delete",
                HttpMethod.DELETE,
                new HttpEntity<>(product_id, new HttpHeaders()),
                new ParameterizedTypeReference<String>() {
                });
    }

    public ResponseEntity<String> redeemAccessTokenUsage(BaasDecreaseAccessRequest baasDecreaseAccessRequest) {

        try {
            return restTemplate.exchange(
                    baasRootUrl + Constants.BAAS_PRODUCTS + Constants.BAAS_POST_DECREASE_ACCESS,
                    HttpMethod.POST,
                    new HttpEntity<>(baasDecreaseAccessRequest, new HttpHeaders()),
                    String.class
            );
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<?> getUserTokensBaas(String userId) {
        String uriBuilder = baasRootUrl + Constants.BAAS_PRODUCTS + Constants.BAAS_GET_USER_TOKENS +
                "?user_id=" + userId;
        try {
            return restTemplate.exchange(
                    uriBuilder,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    new ParameterizedTypeReference<Map<String, BaasAccessToken>>() {}
            );
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<?> checkAccessBaas(DataAccessRequestDto dataAccessRequestDto) {
        String uriBuilder = baasRootUrl + Constants.BAAS_PRODUCTS + Constants.BAAS_POST_CHECK_ACCESS;
        try {
            return restTemplate.exchange(
                    uriBuilder,
                    HttpMethod.POST,
                    new HttpEntity<>(dataAccessRequestDto, new HttpHeaders()),
                    new ParameterizedTypeReference<Map<String, BaasAccessToken>>() {}
            );
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
        }
    }

    /********************************EXCHANGE*************************/

    public ResponseEntity<String> putToken(Map<String, String> body) {

        return restTemplate.exchange(
                baasRootUrl + baasProducts + "/put_token_for_exchange",
                HttpMethod.POST, new HttpEntity<>(body, new HttpHeaders()),
                new ParameterizedTypeReference<String>() {
                });
    }

    public ResponseEntity<String> removeToken(Map<String, String> body){

        return restTemplate.exchange(
                baasRootUrl + baasProducts + "/remove_token_from_exchange",
                HttpMethod.POST, new HttpEntity<>(body, new HttpHeaders()),
                new ParameterizedTypeReference<String>() {
                });
    }

    public ResponseEntity<String> exchangeTokens(TokenExchange tokenExchange){

        return restTemplate.exchange(
                baasRootUrl + baasProducts + "/exchange_tokens",
                HttpMethod.POST, new HttpEntity<>(tokenExchange, new HttpHeaders()),
                new ParameterizedTypeReference<String>() {
                });
    }

    /********************************EXCHANGE END*************************/

}
