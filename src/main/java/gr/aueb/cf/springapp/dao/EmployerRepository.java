package gr.aueb.cf.springapp.dao;

import gr.aueb.cf.springapp.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {

    Employer findEmployerById(Long Id);

    List<Employer> findAll();

}
