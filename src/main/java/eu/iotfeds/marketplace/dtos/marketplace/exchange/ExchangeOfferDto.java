package eu.iotfeds.marketplace.dtos.marketplace.exchange;

import eu.iotfeds.marketplace.persistence.exchange.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeOfferDto {

    private String id;

    private String userId;

    private UserToken token;

    private UserToken targetToken;

    private OfferStatus status;

    private boolean accepted;

    private String dateSubmitted;
}
