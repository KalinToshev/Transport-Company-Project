package org.informatics.transportcompany.service;

import lombok.RequiredArgsConstructor;
import org.informatics.transportcompany.exceptions.company.NoCompanyWithProvidedIdException;
import org.informatics.transportcompany.model.dto.transportCompany.TransportCompanyCreateRequest;
import org.informatics.transportcompany.model.dto.transportCompany.TransportCompanyUpdateRequest;
import org.informatics.transportcompany.model.entity.TransportCompany;
import org.informatics.transportcompany.repository.transportCompany.TransportCompanyRepository;

import java.util.List;

@RequiredArgsConstructor
public class TransportCompanyService {

    private final TransportCompanyRepository transportCompanyRepository;

    public TransportCompany createCompany(TransportCompanyCreateRequest request) {
        TransportCompany company = new TransportCompany();
        company.setName(request.getName());
        company.setAddress(request.getAddress());

        return transportCompanyRepository.create(company);
    }

    public List<TransportCompany> findAll() {
        return transportCompanyRepository.findAll();
    }

    public List<TransportCompany> findAllOrderByName() {
        return transportCompanyRepository.findAllOrderByName();
    }

    public List<Object[]> findAllWithRevenueOrderByRevenueDesc() {
        return transportCompanyRepository.findAllWithRevenueOrderByRevenueDesc();
    }

    public TransportCompany findById(long id) {
        return transportCompanyRepository.findById(id)
                .orElseThrow(() -> new NoCompanyWithProvidedIdException("No company with id = " + id));
    }

    public TransportCompany updateCompany(TransportCompanyUpdateRequest request) {
        TransportCompany company = transportCompanyRepository.findById(request.getId())
                .orElseThrow(() -> new NoCompanyWithProvidedIdException("No company with id = " + request.getId()));

        company.setName(request.getName());
        company.setAddress(request.getAddress());

        return transportCompanyRepository.update(company);
    }

    public void deleteCompany(long id) {
        transportCompanyRepository.deleteById(id);
    }
}