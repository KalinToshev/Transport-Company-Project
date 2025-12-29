package org.informatics.transportcompany.repository.employee;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.informatics.transportcompany.config.HibernateUtil;
import org.informatics.transportcompany.model.entity.Employee;
import org.informatics.transportcompany.model.enums.EmployeeQualification;

import java.util.List;
import java.util.Optional;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    @Override
    public Employee create(Employee employee) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(employee);
                tx.commit();
                return employee;
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public Employee update(Employee employee) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Employee merged = session.merge(employee);

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
    public Optional<Employee> findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);

            Root<Employee> root = cq.from(Employee.class);
            root.fetch("company", JoinType.INNER);

            cq.select(root)
                    .where(cb.equal(root.get("id"), id));

            List<Employee> result = session.createQuery(cq).getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
        }
    }

    @Override
    public List<Employee> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);

            Root<Employee> root = cq.from(Employee.class);
            root.fetch("company", JoinType.INNER);

            cq.select(root);

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Employee> findAllOrderByQualificationThenSalary() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);

            Root<Employee> root = cq.from(Employee.class);
            root.fetch("company", JoinType.INNER);

            cq.select(root)
                    .orderBy(
                            cb.asc(root.get("qualification")),
                            cb.desc(root.get("salary")),
                            cb.asc(root.get("lastName")),
                            cb.asc(root.get("firstName"))
                    );

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Employee> findAllOrderBySalaryDesc() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);

            Root<Employee> root = cq.from(Employee.class);
            root.fetch("company", JoinType.INNER);

            cq.select(root)
                    .orderBy(
                            cb.desc(root.get("salary")),
                            cb.asc(root.get("lastName")),
                            cb.asc(root.get("firstName"))
                    );

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Employee> findByQualificationOrderBySalaryDesc(EmployeeQualification qualification) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);

            Root<Employee> root = cq.from(Employee.class);
            root.fetch("company", JoinType.INNER);

            cq.select(root)
                    .where(cb.equal(root.get("qualification"), qualification))
                    .orderBy(
                            cb.desc(root.get("salary")),
                            cb.asc(root.get("lastName")),
                            cb.asc(root.get("firstName"))
                    );

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public void deleteById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Employee employee = session.find(Employee.class, id);
                if (employee != null) {
                    session.remove(employee);
                }
                tx.commit();
            } catch (Exception ex) {
                tx.rollback();
                throw ex;
            }
        }
    }
}
