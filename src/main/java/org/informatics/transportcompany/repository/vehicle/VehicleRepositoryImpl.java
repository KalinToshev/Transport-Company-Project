package org.informatics.transportcompany.repository.vehicle;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.model.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public class VehicleRepositoryImpl implements VehicleRepository {

    @Override
    public Vehicle create(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(vehicle);
                tx.commit();
                return vehicle;
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Vehicle merged = session.merge(vehicle);

                Hibernate.initialize(merged.getCompany());

                tx.commit();
                return merged;
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public Optional<Vehicle> findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Vehicle> cq = cb.createQuery(Vehicle.class);

            Root<Vehicle> root = cq.from(Vehicle.class);
            root.fetch("company", JoinType.INNER);

            cq.select(root)
                    .where(cb.equal(root.get("id"), id));

            List<Vehicle> result = session.createQuery(cq).getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
        }
    }

    @Override
    public List<Vehicle> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Vehicle> cq = cb.createQuery(Vehicle.class);

            Root<Vehicle> root = cq.from(Vehicle.class);
            root.fetch("company", JoinType.INNER);

            cq.select(root)
                    .orderBy(cb.asc(root.get("id")));

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public void deleteById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Vehicle vehicle = session.find(Vehicle.class, id);
                if (vehicle != null) {
                    session.remove(vehicle);
                }
                tx.commit();
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }
}
