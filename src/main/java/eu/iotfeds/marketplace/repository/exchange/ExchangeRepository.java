package eu.iotfeds.marketplace.repository.exchange;

import eu.iotfeds.marketplace.persistence.exchange.ExchangeOffer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExchangeRepository extends MongoRepository<ExchangeOffer, String>, ExchangeRepositoryCustom {
}
