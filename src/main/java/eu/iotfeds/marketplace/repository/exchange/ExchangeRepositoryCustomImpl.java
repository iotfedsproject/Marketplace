package eu.iotfeds.marketplace.repository.exchange;

import eu.iotfeds.marketplace.persistence.exchange.ExchangeOffer;
import eu.iotfeds.marketplace.persistence.exchange.OfferStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class ExchangeRepositoryCustomImpl implements  ExchangeRepositoryCustom{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ExchangeOffer> getOfferList(String userId, OfferStatus status) {

        Query query = new Query();
        if(status!=null)
            query.addCriteria(Criteria.where("status").is(status));
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, ExchangeOffer.class);
    }
}
