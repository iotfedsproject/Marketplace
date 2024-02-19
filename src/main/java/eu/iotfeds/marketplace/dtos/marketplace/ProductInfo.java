package eu.iotfeds.marketplace.dtos.marketplace;

import eu.h2020.symbiote.model.cim.Location;
import eu.iotfeds.marketplace.dtos.baas.BaasProductDto;
import eu.iotfeds.marketplace.models.ObservationParameters;
import eu.iotfeds.marketplace.models.ObservationProduct;
import eu.iotfeds.marketplace.models.Product;
import eu.iotfeds.marketplace.models.StreamingProduct;
import eu.iotfeds.marketplace.utils.ProductUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class ProductInfo {

    private String fedMarketplaceId;

    private boolean globalMarketplace;

    private String productDetails;

    private String productId;

    private String seller;

    private String vertical;

    private String name;

    private String description;

    private String location;

    private String observationProperties;

    private String dateTimeFrom;

    private String dateTimeTo;

    private Integer access;

    private Long frequency;

    private boolean streaming;

    public ProductInfo() {}

    public ProductInfo(BaasProductDto baasProduct) {
        productId = baasProduct.getProductId();
        fedMarketplaceId = baasProduct.getFedMarketplaceId();
        globalMarketplace = baasProduct.isGlobalMarketplace();
        seller = baasProduct.getSeller();
        productDetails = ProductUtils.convertMapToString(baasProduct.getProductDetails());
        name = baasProduct.getProductId();
    }

    public ProductInfo(Product product) {
        productId = product.getId();
        seller = product.getSellerId();
        fedMarketplaceId = product.getFedMarketplaceId();
        globalMarketplace = product.isGlobalMarketplaceId();
        name = product.getName();
        description = ProductUtils.convertListToString(product.getDescription());
        productDetails = ProductUtils.convertMapToString(product.getProductDetails());
        observationProperties = ProductUtils.convertListToString(product.getObservedProperties());
        vertical = ProductUtils.convertListToString(product.getVertical());
        Set<String> resourceLocations= product.getObservationParameters()
                .stream()
                .map(ObservationParameters::getLocation)
                .map(Location::getName)
                .collect(Collectors.toSet());
        location = resourceLocations == null ? ""
                : ProductUtils.convertListToString(new ArrayList<>(resourceLocations));
        if(product.isStreaming()) {
            StreamingProduct streamingProduct = (StreamingProduct) product;
            dateTimeTo = streamingProduct.getDateTimeTo();
            frequency = streamingProduct.getFrequency();
            streaming = true;
        } else {
            ObservationProduct observationProduct = (ObservationProduct) product;
            dateTimeFrom = observationProduct.getDateTimeFrom();
            dateTimeTo = observationProduct.getDateTimeTo();
            access = observationProduct.getAccess();
            streaming = false;
        }
    }
}
