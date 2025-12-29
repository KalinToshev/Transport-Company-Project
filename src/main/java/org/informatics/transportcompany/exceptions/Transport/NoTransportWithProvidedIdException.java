package org.informatics.transportcompany.exceptions.Transport;

public class NoTransportWithProvidedIdException extends RuntimeException {
    public NoTransportWithProvidedIdException(String message) {
        super(message);
    }
}
