package eu.iotfeds.marketplace.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.h2020.symbiote.model.cim.Observation;
import eu.h2020.symbiote.model.cim.WGS84Location;
import eu.iotfeds.marketplace.dtos.baas.BaasProductDto;
import eu.iotfeds.marketplace.dtos.baas.request.*;
import eu.iotfeds.marketplace.dtos.iotfedsapi.SymbioteResourceDto;
import eu.iotfeds.marketplace.dtos.marketplace.ProductCreateDto;
import eu.iotfeds.marketplace.dtos.marketplace.ProductInfo;
import eu.iotfeds.marketplace.dtos.marketplace.WebSocketInfoDto;
import eu.iotfeds.marketplace.dtos.marketplace.request.AccessProductRequest;
import eu.iotfeds.marketplace.dtos.marketplace.request.IotfedsAccessL1Request;
import eu.iotfeds.marketplace.dtos.marketplace.request.PlatformCredentials;
import eu.iotfeds.marketplace.dtos.marketplace.request.SearchProductRequest;
import eu.iotfeds.marketplace.dtos.marketplace.response.ProductPriceDto;
import eu.iotfeds.marketplace.exception.ApiError;
import eu.iotfeds.marketplace.exception.MarketplaceNotFoundException;
import eu.iotfeds.marketplace.factory.ProductFactory;
import eu.iotfeds.marketplace.http.baas.BaasRest;
import eu.iotfeds.marketplace.http.coretm.CoreTMRest;
import eu.iotfeds.marketplace.http.iotfedsapi.IotfedsApiRest;
import eu.iotfeds.marketplace.mapper.ProductMapper;
import eu.iotfeds.marketplace.models.ObservationParameters;
import eu.iotfeds.marketplace.models.Product;
import eu.iotfeds.marketplace.models.http.GenericHttpResponse;
import eu.iotfeds.marketplace.repository.ProductRepository;
import eu.iotfeds.marketplace.services.platform.OwnedServicesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private OwnedServicesService ownedServicesService;

    @Autowired
    private BaasRest baasRest;

    @Autowired
    private CoreTMRest coreTMRest;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductFactory productFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IotfedsApiRest iotfedsApiRest;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${baas.integration:true}")
    private boolean baasIntegration;

    @Value("${configuration.accessLatestObs}")
    private String accessLatestObs;

    @Value("${symbioteapi.root_url}")
    private String symbioteUrl;

    @Value("${aam.deployment.marketplace.username}")
    private String marketplaceUsername;

    @Value("${aam.deployment.marketplace.password}")
    private String marketplacePassword;

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    public GenericHttpResponse buyProduct(BuyProductRequestDto buyProductRequestDto) throws JsonProcessingException {

        log.info("Buying product with ID: [{}]", buyProductRequestDto.getProduct_id());

        Product product = productRepository.findById(buyProductRequestDto.getProduct_id())
                .orElseThrow(()-> new EntityNotFoundException("Product with ID: ["+buyProductRequestDto.getProduct_id()+"] not found!"));

        return baasRest.buyProduct(buyProductRequestDto, product.getSellerId());
    }

    public String rateProduct (ProductRatingDto productRatingDto){

        return coreTMRest.submitRating(productRatingDto);

    }

    public ResponseEntity<String> subscribe(String productId, String userId, String frequency) throws JsonProcessingException {
        List<WebSocketInfoDto> webSocketResponse = new ArrayList<>();
        DataAccessRequestDto dataAccessRequestDto = new DataAccessRequestDto(
                productId,
                userId,
                "",
                "",
                frequency,
                "");
//        CheckAccessResponse response = baasRest.subscribe(productId, userId, frequency);
        ResponseEntity<Map<String, BaasAccessToken>> response = (ResponseEntity<Map<String, BaasAccessToken>>) baasRest.checkAccessBaas(dataAccessRequestDto);
       BaasAccessToken userTokensList = response.getBody().get("AccessToken");
        if(Integer.parseInt(userTokensList.getAccessTimes())>0){
            Optional<Product> product = productRepository.findById(productId);
            for(ObservationParameters op: product.get().getObservationParameters()){
                webSocketResponse.add(new WebSocketInfoDto(op.getResourceId(), op.getPlatformId(), op.getInterWorkingServiceUrl()));
            }
            return new ResponseEntity<>(objectMapper.writeValueAsString(webSocketResponse), HttpStatus.OK) ;
        }
        else
            return new ResponseEntity<>("Error: Access token depleted.", HttpStatus.NO_CONTENT);
    }

    public List<Product> getProductList(){
        return productRepository.findAll();
    }

    public List<ProductInfo> getGlobalProducts(String userId) {
        Set<Product> products = productRepository.findByGlobalMarketplaceId(true);
        if (baasIntegration) {
            List<BaasProductDto> baasProducts = baasGlobalProducts(userId);
            List<Product> finalProductList = mergeIotfedsWithBaasSearch(products, baasProducts);
            return productMapper.mapProductToProductInfo(finalProductList);
        } else {
            return productMapper.mapProductToProductInfo(new ArrayList<>(products));
        }
    }

    public List<ProductInfo> getFederatedProducts(String userId, String fedId) {
        Set<Product> products = productRepository.findByFedMarketplaceId(fedId);
        if (baasIntegration) {
            List<BaasProductDto> baasProducts = baasFederatedProducts(userId, fedId);
            List<Product> finalProductList = mergeIotfedsWithBaasSearch(products, baasProducts);
            return productMapper.mapProductToProductInfo(finalProductList);
        } else {
            return productMapper.mapProductToProductInfo(new ArrayList<>(products));
        }
    }

//    public List<ProductInfo> searchProducts(Principal principal, ListUserServicesResponse platforms, SearchProductRequest request) {
//        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
//        CoreUser user = (CoreUser) token.getPrincipal();
//        PlatformCredentials platformCredentials = productMapper.mapToPlatformCredentials(user, platforms);
//        Set<Product> products = productRepository.findProductByProperties(request.productCriteria());
//        Set<Product> productsExtraSearch = symbioteSearch(platformCredentials, request);
//        products.addAll(productsExtraSearch);
//        if (baasIntegration) {
//            List<String> productIds = productMapper.mapToProductIdsList(products);
//            String username = user.getUsername() == null ? "" : user.getUsername();
//            BaasSearchProductRequest baasRequest = productMapper.mapToBaasSearchProductsRequest(username, productIds, request);
//            List<BaasProductDto> baasProducts = baasSearchProducts(baasRequest);
//            return mergeIotfedsWithBaasSearch(new ArrayList<>(products), baasProducts);
//        } else {
//            return productMapper.mapProductToProductInfo(products);
//        }
//    }

    public ResponseEntity<?> searchProducts(SearchProductRequest request) {
        Set<Product> products = productRepository.findProductByProperties(request.productCriteria());
        if (baasIntegration) {
            List<String> productIds = productMapper.mapToProductIdsList(products);
            String username = request.getPlatformCredentials().getUsername();
            BaasSearchProductRequest baasRequest = productMapper.mapToBaasSearchProductsRequest(username, productIds, request);
            ResponseEntity<?> baasResponse = baasRest.searchProduct(baasRequest);
            if (HttpStatus.OK == baasResponse.getStatusCode()){
                List<BaasProductDto> baasProducts = (List<BaasProductDto>) baasResponse.getBody();
                List<Product> finalProductList = mergeIotfedsWithBaasSearch(products, baasProducts);
                List<ProductInfo> produtInfo = productMapper.mapProductToProductInfo(finalProductList);
                return new ResponseEntity<>(produtInfo, HttpStatus.OK);
            } else {
                ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Error occured in baas.");
                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
            }
        } else {
            List<ProductInfo> produtInfo = productMapper.mapProductToProductInfo(new ArrayList<>(products));
            return new ResponseEntity<>(produtInfo, HttpStatus.OK);
        }
    }

    private List<BaasProductDto> baasGlobalProducts(String userId) {
        try {
            Map<String, List<BaasProductDto>> response = baasRest.getGlobalProducts(userId);
            return response == null ? Collections.emptyList() : response.get("Products");
        } catch (Exception exc) {
            log.error("Error in Baas "+ exc);
            throw new MarketplaceNotFoundException("Nο product(s) were found.");
        }
    }

    private List<BaasProductDto> baasFederatedProducts(String userId, String fedId) {
        try {
            Map<String, List<BaasProductDto>> response = baasRest.getFederatedProducts(userId, fedId);
            return response == null ? Collections.emptyList() : response.get("Products");
        } catch (Exception exc) {
            log.error("Error in Baas "+ exc);
            throw new MarketplaceNotFoundException("Nο product(s) were found.");
        }
    }

    private ResponseEntity<?> baasSearchProducts(BaasSearchProductRequest request) {
            return baasRest.searchProduct(request);
    }

    private List<Product> mergeIotfedsWithBaasSearch(Set<Product> iotfedsProducts, List<BaasProductDto> baasSearchProducts) {
        List<Product> productList = new ArrayList<>();
        baasSearchProducts.stream()
                .forEach(baasProductDto -> {
                    iotfedsProducts.stream()
                            .filter(product -> product.getId().equals(baasProductDto.getProductId()))
                            .findFirst().ifPresent(product -> productList.add(product));
                });
        return productList;
    }

    public ResponseEntity<Object> createProduct(ProductCreateDto productCreateDto) throws JsonProcessingException {
        log.info("Creating new product");
        Object ob;
        ResponseEntity<Object> response = null;

        Product newProduct = productFactory.getProduct(productCreateDto);

        List<ObservationParameters> parameters = new ArrayList<>();

//        try {

            for(String resourceId: productCreateDto.getResourceIds()){
                log.info("Retrieving platform ID from Baas");
                String platformId = baasRest.getResourceFromBaas(resourceId).getPlatform();
                log.info("Retrieving resource details from SymbIoTe");
                PlatformCredentials credentials = new PlatformCredentials(
                        "SymbIoTe_Core_AAM",
                        marketplaceUsername,
                        marketplacePassword,
                        "Test_Client"
                );
                ResponseEntity<List<SymbioteResourceDto>> resp = restTemplate.exchange(
                        symbioteUrl+"/resources/search/?id="+resourceId,
                        HttpMethod.POST,
                        new HttpEntity<>(
                                credentials,
                                new HttpHeaders()),
                        new ParameterizedTypeReference<List<SymbioteResourceDto>>() {
                        });

                if(resp.getBody()==null || resp.getBody().isEmpty()){
                    log.info("Resource with ID [{}] was not found. Aborting operation...", resourceId);
                    return new ResponseEntity<>("Resource with ID ["+resourceId+"] not found! Aborting operation", HttpStatus.NOT_FOUND);
                }

                SymbioteResourceDto resourceDto = resp.getBody().get(0);
                assert resourceDto != null;
                log.info("Retrieving resource URL from SymbIoTe");

                ResponseEntity<String> urlResp = restTemplate.exchange(
                        symbioteUrl+"/resources/resourceurl?id="+resourceId,
                        HttpMethod.POST,
                        new HttpEntity<>(
                                credentials,
                                new HttpHeaders()),
                        new ParameterizedTypeReference<String>() {
                        });
                assert urlResp.getBody() != null;
                String resUrl = urlResp.getBody();


                ObservationParameters parameter = new ObservationParameters(
                        resourceDto.getId(),
                        platformId,
                        resUrl,
                        new WGS84Location(
                                resourceDto.getLocationLongitude(),
                                resourceDto.getLocationLatitude(),
                                resourceDto.getLocationAltitude(),
                                resourceDto.getLocationName(),
                                Arrays.asList(resourceDto.getDescription()))
                );
                parameters.add(parameter);
            }

            newProduct.setObservationParameters(parameters);
            newProduct = productRepository.save(newProduct);


            if (baasIntegration) {
                ResponseEntity baasResponse = baasRest.registerProductToBaas(newProduct);
                if (baasResponse.getStatusCode() == HttpStatus.OK) {
                    log.info("Product [{}] successfully registered to blockchain", newProduct.getId());
                    response = new ResponseEntity(newProduct, HttpStatus.OK);
                }
                else {
                    log.info("Error registering product to blockchain with code [{}]", baasResponse.getStatusCode());
                    log.info("Rolling back local database");
                    productRepository.deleteById(newProduct.getId());
                    log.info("Local database successfully rolled back");
                    response = new ResponseEntity<>(baasResponse.getBody(), baasResponse.getStatusCode());
                }
            }
//        }
//        catch(Exception e) {
//                log.info("Error: {}.", e.getMessage());
//                newProduct = null;
//                response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//            }

            return response;
        }

    public ProductPriceDto calcProductPrice(CalcProductPriceDto calcProductPriceDto) {
        return baasRest.calcProductPrice(calcProductPriceDto);
    }

    public ResponseEntity<?> accessProduct(AccessProductRequest accessProductRequest) {
        String productId = accessProductRequest.getProductId();
        String username = accessProductRequest.getPlatformCredentials().getUsername();
        String reqObservations = accessProductRequest.getReqObservations();

        if (baasIntegration) {
            DataAccessRequestDto dataAccessRequestDto = new DataAccessRequestDto(
                    productId,
                    username,
                    accessProductRequest.getDateFrom(),
                    accessProductRequest.getDateTo(),
                    "",
                    accessProductRequest.getReqObservations()
            );
            ResponseEntity<?> baasResponse = baasRest.checkAccessBaas(dataAccessRequestDto);

            if (baasResponse.getStatusCode() != HttpStatus.OK) {
                ResponseEntity<?> userTokensResponse = baasRest.getUserTokensBaas(username);
                Map<String, String> error = new HashMap<>();
                error.put("baas-error", (String) baasResponse.getBody());
                if (userTokensResponse.getStatusCode() != HttpStatus.OK) {
                    error.put("error","Could not get access tokens for user" );
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }
                Map<String, BaasAccessToken> userTokensList = (Map<String, BaasAccessToken>) userTokensResponse.getBody();
                error.put("error","The max observations that you can have are " + userTokensList.get(productId).getAccessTimes() );
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }

        Map<String, List<Observation>> observationMap = getStringListMap(accessProductRequest, Integer.parseInt(reqObservations));
        Set<Integer> resources = observationMap.values().stream()
                .map(List::size)
                .collect(Collectors.toSet());

        if (resources.isEmpty()){
            return new ResponseEntity<>("No observations found", HttpStatus.BAD_REQUEST);
        }
        if (resources.stream().anyMatch(value -> value == 0)) {
            return new ResponseEntity<>("A resource has 0 observations", HttpStatus.BAD_REQUEST);
        }

        while (resources.size() >= 2) {
            int minimumValue = resources.stream().mapToInt(Integer::intValue).min().orElse(0);
            observationMap = getStringListMap(accessProductRequest, minimumValue); //TODO take observation parameters from database
            resources = observationMap.values().stream()
                    .map(List::size)
                    .collect(Collectors.toSet());
        }

        ResponseEntity<String> baasResponseRedeem = baasRest.redeemAccessTokenUsage(
                new BaasDecreaseAccessRequest(
                        productId,
                        username,
                        String.valueOf(resources.stream().mapToInt(Integer::intValue).min().orElse(0))
                )
        );
        if (baasIntegration) {
            if (baasResponseRedeem.getStatusCode() != HttpStatus.OK) {
                return new ResponseEntity<>("Redeem was not successful", HttpStatus.BAD_REQUEST);
            }
        }
        log.info("Redeemed {} uses", baasResponseRedeem.getBody());
        return new ResponseEntity<>(observationMap, HttpStatus.OK);
    }

    private Map<String, List<Observation>> getStringListMap(AccessProductRequest accessProductRequest, Integer topObservations) {
        String resourceInternalId;
        String platformId;
        String dateFrom = accessProductRequest.getDateFrom();
        String dateTo = accessProductRequest.getDateTo();

        Product product = productRepository.findProductById(accessProductRequest.getProductId());

        Map<String, List<Observation>> observationMap  = new HashMap<>();
        for (ObservationParameters observationParameters : product.getObservationParameters()) {

            resourceInternalId = observationParameters.getResourceId();
            platformId = observationParameters.getPlatformId();

            IotfedsAccessL1Request request = new IotfedsAccessL1Request(
                    new PlatformCredentials(
                            accessProductRequest.getPlatformCredentials().getLocalPlatformId(),
                            marketplaceUsername,
                            marketplacePassword,
                            accessProductRequest.getPlatformCredentials().getClientId()
                    ),
                    platformId
            );
            List<String> platformsWithLatestObservation = Arrays.asList(accessLatestObs.split(",", -1));
            if(platformsWithLatestObservation.contains(platformId)) {
                observationMap.put(resourceInternalId, iotfedsApiRest.accessL1ResourceWithResourceId(
                        resourceInternalId, null, null, null, request));
            } else {
                List<Observation> observedProduct = iotfedsApiRest.accessL1ResourceWithResourceId(
                        resourceInternalId,
                        Objects.equals(dateFrom, "") ? null : dateFrom,
                        Objects.equals(dateTo, "") ? null : dateTo,
                        topObservations,
                        request
                );
                observationMap.put(resourceInternalId, observedProduct);
            }
//                    return (localTime == null) ? null : localTime.format(DateTimeFormatter.ofPattern(pattern));
        }
        return observationMap;
    }

    public GenericHttpResponse deleteProductById(String productId){
           log.info("Deleting product from blockchain");
           GenericHttpResponse response;
           ResponseEntity<String> baasResponse = baasRest.deleteProduct(productId);
           if(baasResponse.getStatusCode()==HttpStatus.OK){
               log.info("Deleting product from Marketplace");
               productRepository.deleteById(productId);
               response = new GenericHttpResponse(HttpStatus.OK, "Product successfully deleted");
           }
           else
               response = new GenericHttpResponse(baasResponse.getStatusCode(), "Error deleting product from blockchain");

           return response;
    }
}
