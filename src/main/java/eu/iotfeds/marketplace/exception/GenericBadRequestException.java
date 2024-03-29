package eu.iotfeds.marketplace.exception;

import org.springframework.http.HttpStatus;

public class GenericBadRequestException extends GenericHttpErrorException {

    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public GenericBadRequestException(String s) {
        super(s, status);
    }

    public GenericBadRequestException(String s, Object response) {
        super(s, response, status);
    }
}
