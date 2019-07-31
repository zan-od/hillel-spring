package hillel.spring.doctor.repository;

import hillel.spring.doctor.domain.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DoctorRepositoryCustomImpl implements DoctorRepositoryCustom {

    private EntityManager em;

    @Autowired
    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Doctor> findByCriteria(Map<String, Object> parameters) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Doctor> query = criteriaBuilder.createQuery(Doctor.class);

        Root<Doctor> from = query.from(Doctor.class);

        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            switch (parameter.getKey()) {
                case "name":
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(from.get("name")), ((String) parameter.getValue()).toLowerCase() + "%"));
                    break;
                case "specialization":
                    predicates.add(criteriaBuilder.equal(from.get("specialization"), (String) parameter.getValue()));
                    break;
                case "specializations":
                    predicates.add(from.get("specialization").in((List<String>) parameter.getValue()));
                    break;
            }
        }

        query.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }
}
