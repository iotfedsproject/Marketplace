package eu.iotfeds.marketplace.persistence.exchange;

import eu.iotfeds.marketplace.dtos.marketplace.exchange.UserToken;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("exchange")
@ToString
public class ExchangeOffer {

    @Id
    private String id;

    private String userId; //Owner of the token

    private UserToken token; // Personal token selected for exchange

    private UserToken targetToken; // Foreign token selected for exchange

    private OfferStatus status;

    private boolean accepted;

    @CreatedDate
    private String dateSubmitted;

}
