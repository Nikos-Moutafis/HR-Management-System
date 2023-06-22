package gr.aueb.cf.springapp.dao;

import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findEmployeeById(Long id);

    List<Employee> findAllByOrderByLastname();

    @Query("SELECT p FROM Project p JOIN p.employees e WHERE e.id = :employeeId")
    List<Project> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);


}
