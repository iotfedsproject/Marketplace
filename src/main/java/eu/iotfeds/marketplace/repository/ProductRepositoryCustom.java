package eu.iotfeds.marketplace.repository;

import eu.iotfeds.marketplace.models.Product;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Set;

interface ProductRepositoryCustom {
    Set<Product> findProductByProperties(Criteria criteria);

    Set<Product> findDistinctProductByResourceId(List<String> resourceId);

}
