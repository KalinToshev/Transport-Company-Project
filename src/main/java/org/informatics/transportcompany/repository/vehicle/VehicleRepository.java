package org.informatics.transportcompany.repository.vehicle;

import org.informatics.transportcompany.model.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {

    Vehicle create(Vehicle vehicle);

    Vehicle update(Vehicle vehicle);

    Optional<Vehicle> findById(long id);

    List<Vehicle> findAll();

    void deleteById(long id);
}
