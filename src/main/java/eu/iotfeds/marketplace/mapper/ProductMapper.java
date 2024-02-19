package eu.iotfeds.marketplace.mapper;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.FederatedResource;
import eu.iotfeds.marketplace.dtos.baas.request.BaasSearchProductRequest;
import eu.iotfeds.marketplace.dtos.marketplace.ProductInfo;
import eu.iotfeds.marketplace.dtos.marketplace.request.PlatformCredentials;
import eu.iotfeds.marketplace.dtos.marketplace.request.SearchProductRequest;
import eu.iotfeds.marketplace.exception.MarketplaceNotFoundException;
import eu.iotfeds.marketplace.models.CoreUser;
import eu.iotfeds.marketplace.models.ListUserServicesResponse;
import eu.iotfeds.marketplace.models.PlatformDetails;
import eu.iotfeds.marketplace.models.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public BaasSearchProductRequest mapToBaasSearchProductsRequest(String user, List<String> product_ids, SearchProductRequest criteria) {
        BaasSearchProductRequest searchProductsBaasRequest = new BaasSearchProductRequest();
        searchProductsBaasRequest.setUserId(user);
        searchProductsBaasRequest.setProducts(product_ids);
        String doubleQuotes = "";
        searchProductsBaasRequest.setPriceMin(criteria.getPriceMin() == null ? doubleQuotes : criteria.getPriceMin());
        searchProductsBaasRequest.setPriceMax(criteria.getPriceMax() == null ? doubleQuotes : criteria.getPriceMax());
        searchProductsBaasRequest.setRepMin(criteria.getRepMin() == null ? doubleQuotes : criteria.getRepMin());
        searchProductsBaasRequest.setRepMax(criteria.getRepMax() == null ? doubleQuotes : criteria.getRepMax());
        return  searchProductsBaasRequest;
    }

    public PlatformCredentials mapToPlatformCredentials(CoreUser user, ListUserServicesResponse userServices) {
        PlatformCredentials platformCredentials = new PlatformCredentials();
        List<String> availablePlatformIds = userServices.getAvailablePlatforms()
                .stream().map(PlatformDetails::getId)
                .collect(Collectors.toList());
        if(availablePlatformIds.isEmpty()) {
            throw new MarketplaceNotFoundException("No available platforms were found.");
        } else {
            platformCredentials.setUsername(user.getUsername());
            platformCredentials.setPassword(user.getPassword());
            platformCredentials.setLocalPlatformId(availablePlatformIds.get(0));
            return platformCredentials;
        }
    }

    public List<String> mapToProductIdsList(Set<Product> products) {
       return products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
    }

    public List<String> mapToResourceIdsList(List<FederatedResource> resources) {
        return resources.stream()
                .map(FederatedResource::getCloudResource)
                .map(CloudResource::getResource)
                .map(eu.h2020.symbiote.model.cim.Resource::getId)
                .collect(Collectors.toList());
    }


    public List<ProductInfo> mapProductToProductInfo(List<Product> products) {
        List<ProductInfo> result = new ArrayList<>();
        products.stream()
                .forEach(product -> {
                    ProductInfo productInfo = new ProductInfo(product);
                    result.add(productInfo);
                });
        return result;
    }
}
