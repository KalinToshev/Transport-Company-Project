package org.informatics.transportcompany.exceptions.client;

public class NoClientWithProvidedIdException extends RuntimeException {
    public NoClientWithProvidedIdException(String message) {
        super(message);
    }
}
