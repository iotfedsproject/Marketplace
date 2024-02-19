package eu.iotfeds.marketplace.repository;

import eu.iotfeds.marketplace.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Set<Product> findProductByProperties(Criteria criteria) {
        Query query = new Query(criteria);
        List<Product> productsList = mongoTemplate.find(query, Product.class);
        return new HashSet<>(productsList);
    }

    @Override
    public Set<Product> findDistinctProductByResourceId(List<String> resourceId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("observationParameters.resourceId").in(resourceId));
        List<Product> productsList = mongoTemplate.findDistinct(query, "id", Product.class, Product.class);
        return new HashSet<>(productsList);
    }
}
