package eu.iotfeds.marketplace.repository;

import eu.iotfeds.marketplace.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface ProductRepository extends MongoRepository<Product, String>, ProductRepositoryCustom {

    Set<Product> findByFedMarketplaceId(String fedMarketplaceId);

    Set<Product> findByGlobalMarketplaceId(boolean globalMarketplaceId);

    Product findProductById(String id);
}
