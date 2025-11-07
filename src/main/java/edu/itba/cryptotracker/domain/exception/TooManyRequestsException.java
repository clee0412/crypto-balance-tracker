package edu.itba.cryptotracker.domain.exception;

import static com.distasilucas.cryptobalancetracker.constants.ExceptionConstants.REQUEST_LIMIT_REACHED;

public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException() {
        super(REQUEST_LIMIT_REACHED);
    }
}
