package org.informatics.transportcompany.repository.transportCompany;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.model.entity.Transport;
import org.informatics.transportcompany.model.entity.TransportCompany;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TransportCompanyRepositoryImpl implements TransportCompanyRepository {

    @Override
    public TransportCompany create(TransportCompany company) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(company);
                tx.commit();
                return company;
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public TransportCompany update(TransportCompany company) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                company = session.merge(company);
                tx.commit();
                return company;
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public Optional<TransportCompany> findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TransportCompany> cq = cb.createQuery(TransportCompany.class);

            Root<TransportCompany> root = cq.from(TransportCompany.class);
            cq.select(root).where(cb.equal(root.get("id"), id));

            List<TransportCompany> result = session.createQuery(cq).getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
        }
    }

    @Override
    public List<TransportCompany> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TransportCompany> cq = cb.createQuery(TransportCompany.class);

            Root<TransportCompany> root = cq.from(TransportCompany.class);
            cq.select(root);

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<TransportCompany> findAllOrderByName() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TransportCompany> cq = cb.createQuery(TransportCompany.class);

            Root<TransportCompany> root = cq.from(TransportCompany.class);
            cq.select(root)
                    .orderBy(cb.asc(root.get("name")));

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Object[]> findAllWithRevenueOrderByRevenueDesc() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

            Root<TransportCompany> companyRoot = cq.from(TransportCompany.class);

            Subquery<BigDecimal> revenueSub = cq.subquery(BigDecimal.class);
            Root<Transport> transportRoot = revenueSub.from(Transport.class);

            revenueSub.select(cb.sum(transportRoot.get("price")));
            revenueSub.where(cb.equal(transportRoot.get("company"), companyRoot));

            Expression<BigDecimal> revenueExpr =
                    cb.coalesce(revenueSub, BigDecimal.ZERO);

            cq.select(cb.array(companyRoot, revenueExpr));
            cq.orderBy(cb.desc(revenueExpr));

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public void deleteById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                TransportCompany company = session.find(TransportCompany.class, id);
                if (company != null) {
                    session.remove(company);
                }
                tx.commit();
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }
}
