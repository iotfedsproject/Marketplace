package eu.iotfeds.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MarketplaceNotFoundException extends RuntimeException {
    public MarketplaceNotFoundException() {
            super();
        }
    public MarketplaceNotFoundException(String message) {
            super(message);
        }
}
