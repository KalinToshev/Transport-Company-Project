package org.informatics.transportcompany.exceptions.employee;

public class NoEmployeeWithProvidedIdException extends RuntimeException {
    public NoEmployeeWithProvidedIdException(String message) {
        super(message);
    }
}
