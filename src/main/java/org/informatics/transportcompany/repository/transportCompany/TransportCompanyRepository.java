package org.informatics.transportcompany.repository.transportCompany;

import org.informatics.transportcompany.model.entity.TransportCompany;

import java.util.List;
import java.util.Optional;

public interface TransportCompanyRepository {
    TransportCompany create(TransportCompany company);

    TransportCompany update(TransportCompany company);

    Optional<TransportCompany> findById(long id);

    List<TransportCompany> findAll();

    List<TransportCompany> findAllOrderByName();

    List<Object[]> findAllWithRevenueOrderByRevenueDesc();

    void deleteById(long id);
}
