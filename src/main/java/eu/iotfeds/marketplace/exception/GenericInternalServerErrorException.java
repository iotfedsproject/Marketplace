package eu.iotfeds.marketplace.exception;

import org.springframework.http.HttpStatus;

public class GenericInternalServerErrorException extends GenericHttpErrorException {
    private static final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public GenericInternalServerErrorException(String s) {
        super(s, status);
    }

    public GenericInternalServerErrorException(String s, Object response) {
        super(s, response, status);
    }
}
