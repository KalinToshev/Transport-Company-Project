package org.informatics.transportcompany.repository.transport;

import org.informatics.transportcompany.model.entity.Transport;
import org.informatics.transportcompany.model.entity.TransportCompany;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransportRepository {

    Transport create(Transport transport);

    Transport update(Transport transport);

    Optional<Transport> findById(long id);

    Optional<Transport> findByIdWithClient(long id);

    List<Transport> findAllWithAllJoins();

    List<Transport> findAllOrderByToLocationWithClient();

    List<Transport> findByToLocationWithClient(String toLocation);

    long countAll();
    BigDecimal sumTotalRevenue();

    List<Object[]> driverTransportStats();

    BigDecimal sumCompanyRevenueForPeriod(TransportCompany company, LocalDateTime from, LocalDateTime to);

    List<Object[]> driverRevenue();
}
