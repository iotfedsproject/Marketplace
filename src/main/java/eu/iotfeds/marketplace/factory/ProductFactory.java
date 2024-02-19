package eu.iotfeds.marketplace.factory;

import eu.iotfeds.marketplace.dtos.marketplace.ProductCreateDto;
import eu.iotfeds.marketplace.models.ObservationProduct;
import eu.iotfeds.marketplace.models.Product;
import eu.iotfeds.marketplace.models.StreamingProduct;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory {

    public Product getProduct(ProductCreateDto productCreateDto) {
        Product newProduct;
        if (productCreateDto.isSupportStream()) {
            newProduct = new StreamingProduct();
            ((StreamingProduct) newProduct).setDateTimeTo(productCreateDto.getDateTimeTo());
            ((StreamingProduct) newProduct).setFrequency(0L); //TODO add the real value
        } else {
            newProduct = new ObservationProduct();
            ((ObservationProduct) newProduct).setDateTimeFrom(productCreateDto.getDateTimeFrom());
            ((ObservationProduct) newProduct).setDateTimeTo(productCreateDto.getDateTimeTo());
            ((ObservationProduct) newProduct).setAccess(0); //TODO add the real value
        }
        newProduct.setId(productCreateDto.getProductId());
        newProduct.setSellerId(productCreateDto.getSellerId());
        newProduct.setStreaming(productCreateDto.isSupportStream());
        newProduct.setFedMarketplaceId(productCreateDto.getFedMarketplaceId());
        newProduct.setGlobalMarketplaceId(productCreateDto.isGlobalMarketplaceId());
        newProduct.setName(productCreateDto.getName());
        newProduct.setDescription(productCreateDto.getDescription());
//        newProduct.setProductDetails(productCreateDto.getProductDetails());
        newProduct.setObservedProperties(productCreateDto.getObservedProperties());
//        newProduct.setObservationParameters(productCreateDto.getObservationParameters());
        newProduct.setVertical(productCreateDto.getVertical());
        return newProduct;
    }
}
