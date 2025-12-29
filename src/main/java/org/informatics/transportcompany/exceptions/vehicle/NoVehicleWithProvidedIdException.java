package org.informatics.transportcompany.exceptions.vehicle;

public class NoVehicleWithProvidedIdException extends RuntimeException {
    public NoVehicleWithProvidedIdException(String message) {
        super(message);
    }
}
