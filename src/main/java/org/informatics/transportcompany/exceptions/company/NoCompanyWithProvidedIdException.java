package org.informatics.transportcompany.exceptions.company;

public class NoCompanyWithProvidedIdException extends RuntimeException {
    public NoCompanyWithProvidedIdException(String message) {
        super(message);
    }
}
