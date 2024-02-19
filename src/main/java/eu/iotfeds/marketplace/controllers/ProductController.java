package eu.iotfeds.marketplace.controllers;

import eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory;
import eu.h2020.symbiote.model.cim.Observation;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;
import eu.iotfeds.marketplace.dtos.baas.request.BuyProductRequestDto;
import eu.iotfeds.marketplace.dtos.baas.request.CalcProductPriceDto;
import eu.iotfeds.marketplace.dtos.baas.request.ProductRatingDto;
import eu.iotfeds.marketplace.dtos.marketplace.ProductCreateDto;
import eu.iotfeds.marketplace.dtos.marketplace.ProductInfo;
import eu.iotfeds.marketplace.dtos.marketplace.request.AccessProductRequest;
import eu.iotfeds.marketplace.dtos.marketplace.request.SearchProductRequest;
import eu.iotfeds.marketplace.dtos.marketplace.response.ProductPriceDto;
import eu.iotfeds.marketplace.exception.MarketplaceNotFoundException;
import eu.iotfeds.marketplace.models.Product;
import eu.iotfeds.marketplace.models.http.GenericHttpResponse;
import eu.iotfeds.marketplace.services.ProductService;
import eu.iotfeds.marketplace.services.platform.OwnedServicesService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OwnedServicesService ownedServicesService;


    @ApiOperation(value = "Buy product")
    @RequestMapping(path = "/buy", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> buyProduct(@RequestBody BuyProductRequestDto buyProductRequestDto)
//                                                         @ApiIgnore @AuthenticationPrincipal Principal principal)
            throws IOException {

        GenericHttpResponse response = productService.buyProduct(buyProductRequestDto);

        return new ResponseEntity<>(response.getBody(), response.getCode());

    }

    @ApiOperation(value = "Rate product")
    @RequestMapping(path = "/rate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rateProduct(@RequestBody ProductRatingDto productRatingDto,
                                              @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {

        return new ResponseEntity<>(productService.rateProduct(productRatingDto), HttpStatus.OK);

    }

    @ApiOperation(value = "Access streaming data")
    @RequestMapping(path = "/subscribe", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> accessStreamData(@RequestParam(value="product_id") String productId,
                                                   @RequestParam(value="frequency") String frequency,
                                                   @RequestParam(value="userId") String username)
//                                           @ApiIgnore @AuthenticationPrincipal Principal principal)
                                           throws IOException {

//        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
//        CoreUser user = (CoreUser) token.getPrincipal();
//        String username = user.getValidUsername();

        return productService.subscribe(productId, username, frequency);

    }

    @GetMapping("/global")
    @ResponseBody
    public ResponseEntity<?> getGlobalProducts(@RequestParam(name = "user_id") String userId){
        try {
            List<ProductInfo> result = productService.getGlobalProducts(userId);
            return ResponseEntity.ok(result);
        } catch(MarketplaceNotFoundException exception) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage());
        }
    }

    @GetMapping("/federation")
    @ResponseBody
    public ResponseEntity<?> getFederatedProducts(@RequestParam(name = "user_id") String userId,
                                                  @RequestParam(name = "fed_id") String fedId){
        try {
            List<ProductInfo> result = productService.getFederatedProducts(userId, fedId);
            return ResponseEntity.ok(result);
        } catch(MarketplaceNotFoundException exception) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage());
        }
    }


//    @PostMapping("/search")
//    public ResponseEntity<?> searchProducts(@RequestBody SearchProductRequest request,
//                                            @ApiIgnore @AuthenticationPrincipal Principal principal){
//        if(principal == null) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized user");
//        }
//        ResponseEntity<ListUserServicesResponse> userServicesResponse = ownedServicesService.listUserServices(principal);
//        if(HttpStatus.OK == userServicesResponse.getStatusCode() ||
//                HttpStatus.PARTIAL_CONTENT == userServicesResponse.getStatusCode()) {
//            List<ProductInfo> result = productService.searchProducts(principal, userServicesResponse.getBody(), request);
//            return ResponseEntity.ok(result);
//        } else {
//            return userServicesResponse;
//        }
    @PostMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestBody SearchProductRequest request){
        return new ResponseEntity<>(productService.searchProducts(request), HttpStatus.OK);
    }

    @ApiOperation(value = "End-point to access product")
    @RequestMapping(path = "/access", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> AccessL1Resource(@RequestBody AccessProductRequest accessProductRequest) {

        return productService.accessProduct(accessProductRequest);
    }

    @ApiOperation(value = "Create product")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createProduct(@RequestBody ProductCreateDto productCreateDto)
//                                                 @ApiIgnore @AuthenticationPrincipal Principal principal)
                                                 throws IOException {

        return productService.createProduct(productCreateDto);
    }

    @ApiOperation(value = "Calculate product price")
    @RequestMapping(path = "/get-price", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductPriceDto> calculateProductPrice(@RequestBody CalcProductPriceDto calcProductPriceDto)
//                                                 @ApiIgnore @AuthenticationPrincipal Principal principal)
            throws IOException {

        return new ResponseEntity<>(productService.calcProductPrice(calcProductPriceDto), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete product by ID")
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public GenericHttpResponse deleteProduct(@PathVariable("Product ID") String productId)
//                                                 @ApiIgnore @AuthenticationPrincipal Principal principal)
            throws IOException {

        return productService.deleteProductById(productId);
    }
}
