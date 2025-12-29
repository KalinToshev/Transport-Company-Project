package org.informatics.transportcompany.service;

import lombok.RequiredArgsConstructor;
import org.informatics.transportcompany.exceptions.company.NoCompanyWithProvidedIdException;
import org.informatics.transportcompany.exceptions.vehicle.NoVehicleWithProvidedIdException;
import org.informatics.transportcompany.model.dto.vehicle.VehicleCreateRequest;
import org.informatics.transportcompany.model.dto.vehicle.VehicleUpdateRequest;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.model.entity.Vehicle;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;
import org.informatics.transportcompany.repository.vehicle.VehicleRepository;

import java.util.List;

@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final TransportCompanyRepository transportCompanyRepository;

    public Vehicle createVehicle(VehicleCreateRequest request) {
        TransportCompany company = transportCompanyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new NoCompanyWithProvidedIdException("No company with id = " + request.getCompanyId()));

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setType(request.getType());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setCompany(company);

        return vehicleRepository.create(vehicle);
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle findById(long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public Vehicle updateVehicle(VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getId())
                .orElseThrow(() -> new NoVehicleWithProvidedIdException("No vehicle with id = " + request.getId()));

        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setType(request.getType());
        vehicle.setCapacity(request.getCapacity());

        return vehicleRepository.update(vehicle);
    }

    public void deleteVehicle(long id) {
        vehicleRepository.deleteById(id);
    }
}