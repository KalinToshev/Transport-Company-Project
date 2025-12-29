package org.informatics.transportcompany.repository.transport;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.entity.Transport;
import org.informatics.transportcompany.model.entity.TransportCompany;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransportRepositoryImpl implements TransportRepository {
    @Override
    public Transport create(Transport transport) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(transport);
                tx.commit();
                return transport;
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public Transport update(Transport transport) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Transport merged = session.merge(transport);

                Hibernate.initialize(merged.getCompany());
                Hibernate.initialize(merged.getClient());
                Hibernate.initialize(merged.getVehicle());
                Hibernate.initialize(merged.getDriver());

                tx.commit();
                return merged;
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public Optional<Transport> findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(Transport.class, id));
        }
    }

    @Override
    public Optional<Transport> findByIdWithClient(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Transport> cq = cb.createQuery(Transport.class);

            Root<Transport> root = cq.from(Transport.class);
            root.fetch("client", JoinType.INNER);

            cq.select(root)
                    .where(cb.equal(root.get("id"), id));

            List<Transport> result = session.createQuery(cq).getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
        }
    }

    @Override
    public List<Transport> findAllWithAllJoins() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Transport> cq = cb.createQuery(Transport.class);

            Root<Transport> root = cq.from(Transport.class);
            root.fetch("company", JoinType.INNER);
            root.fetch("client", JoinType.INNER);
            root.fetch("vehicle", JoinType.INNER);
            root.fetch("driver", JoinType.INNER);

            cq.select(root);

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Transport> findAllOrderByToLocationWithClient() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Transport> cq = cb.createQuery(Transport.class);

            Root<Transport> root = cq.from(Transport.class);
            root.fetch("client", JoinType.INNER);

            cq.select(root)
                    .orderBy(
                            cb.asc(root.get("toLocation")),
                            cb.asc(root.get("fromLocation")),
                            cb.asc(root.get("departureDateTime"))
                    );

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Transport> findByToLocationWithClient(String toLocation) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Transport> cq = cb.createQuery(Transport.class);

            Root<Transport> root = cq.from(Transport.class);
            root.fetch("client", JoinType.INNER);

            cq.select(root)
                    .where(cb.equal(cb.trim(root.get("toLocation")), toLocation.trim()))
                    .orderBy(cb.asc(root.get("departureDateTime")));

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public long countAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);

            Root<Transport> root = cq.from(Transport.class);
            cq.select(cb.count(root));

            Long count = session.createQuery(cq).getSingleResult();
            return count != null ? count : 0L;
        }
    }

    @Override
    public BigDecimal sumTotalRevenue() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);

            Root<Transport> root = cq.from(Transport.class);
            Expression<BigDecimal> sumExpr = cb.sum(root.get("price"));
            cq.select(sumExpr);

            BigDecimal total = session.createQuery(cq).getSingleResult();
            return total != null ? total : BigDecimal.ZERO;
        }
    }

    @Override
    public List<Object[]> driverTransportStats() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

            Root<Transport> root = cq.from(Transport.class);
            Expression<Long> countExpr = cb.count(root);

            cq.select(cb.array(root.get("driver"), countExpr));
            cq.groupBy(root.get("driver"));
            cq.orderBy(cb.desc(countExpr));

            List<Object[]> rows = session.createQuery(cq).getResultList();

            for (Object[] row : rows) {
                Employee driver = (Employee) row[0];
                if (driver != null) {
                    Hibernate.initialize(driver);
                    Hibernate.initialize(driver.getCompany());
                }
            }
            return rows;
        }
    }

    @Override
    public BigDecimal sumCompanyRevenueForPeriod(TransportCompany company,
                                                 LocalDateTime from,
                                                 LocalDateTime to) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);

            Root<Transport> root = cq.from(Transport.class);
            Expression<BigDecimal> sumExpr = cb.sum(root.get("price"));

            cq.select(sumExpr)
                    .where(
                            cb.equal(root.get("company"), company),
                            cb.between(root.get("departureDateTime"), from, to),
                            cb.isTrue(root.get("paid"))
                    );

            BigDecimal total = session.createQuery(cq).getSingleResult();
            return total != null ? total : BigDecimal.ZERO;
        }
    }

    @Override
    public List<Object[]> driverRevenue() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

            Root<Transport> root = cq.from(Transport.class);
            Expression<BigDecimal> sumExpr = cb.sum(root.get("price"));

            cq.select(cb.array(root.get("driver"), sumExpr));
            cq.where(cb.isTrue(root.get("paid")));
            cq.groupBy(root.get("driver"));
            cq.orderBy(cb.desc(sumExpr));

            List<Object[]> rows = session.createQuery(cq).getResultList();

            for (Object[] row : rows) {
                Employee driver = (Employee) row[0];
                if (driver != null) {
                    Hibernate.initialize(driver);
                    Hibernate.initialize(driver.getCompany());
                }
            }

            return rows;
        }
    }
}
