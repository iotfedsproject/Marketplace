package eu.iotfeds.marketplace.repository;

import eu.iotfeds.marketplace.models.DbResourceInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResourceRepository extends MongoRepository<DbResourceInfo, String>, ProductRepositoryCustom {
}
