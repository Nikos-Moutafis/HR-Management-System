package gr.aueb.cf.springapp.service;

import gr.aueb.cf.springapp.dto.EmployerDTO;
import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;

import java.util.List;

public interface IEmployerService {

     List<Employer> findAllEmployers();

     Employer findById(Long id)throws EntityNotFoundException;
     Employer insertEmployer(EmployerDTO employerDTO) throws EntityAlreadyExistsException;

     void removeEmployeeFromEmployer(Long employeeId, Long employerId) throws EntityNotFoundException;

     Employer updateEmployer(EmployerDTO employerDTO) throws EntityNotFoundException;

     void deleteEmployer(Long id) throws EntityNotFoundException;

     List<Employee> findAllEmployees();

     Employee getEmployeeById(Long id) throws EntityNotFoundException;


     List<Employee> findEmployeesByEmployerId(Long id) throws EntityNotFoundException;

}
