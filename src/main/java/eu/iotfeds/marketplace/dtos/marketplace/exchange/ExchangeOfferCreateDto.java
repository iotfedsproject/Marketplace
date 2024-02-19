package eu.iotfeds.marketplace.dtos.marketplace.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeOfferCreateDto {

    @NotNull
    private String userId;

    @NotNull
    private UserToken token;

    @NotNull
    private UserToken targetToken;
}
