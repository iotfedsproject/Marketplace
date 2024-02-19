package eu.iotfeds.marketplace.repository.exchange;

import eu.iotfeds.marketplace.persistence.exchange.ExchangeOffer;
import eu.iotfeds.marketplace.persistence.exchange.OfferStatus;

import java.util.List;

interface ExchangeRepositoryCustom {

    List<ExchangeOffer> getOfferList(String userId, OfferStatus status);

}
