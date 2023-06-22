package gr.aueb.cf.springapp.service;

import gr.aueb.cf.springapp.dto.EmployeeDTO;
import gr.aueb.cf.springapp.dto.EmployerDTO;
import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.entity.Project;
import gr.aueb.cf.springapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.springapp.service.exceptions.InvalidHiringException;

import java.util.List;

public interface IEmployeeService {

	List<Employee> findAllEmployees();

	Employee findById(Long theId) throws EntityNotFoundException;
	List<Employer> getAllEmployers() ;
	Employee insertEmployee(EmployeeDTO employeeDTO) throws EntityAlreadyExistsException;
	List<Project> getProjectsByEmployeeId(Long employeeId) throws EntityNotFoundException;
	Employer getEmployerById(Long id) throws EntityNotFoundException;
	Employee updateEmployee(EmployeeDTO employeeDTO) throws EntityNotFoundException;

	public Employee addEmployerToEmployee(Long employeeId, Long employerId) throws EntityNotFoundException,
			InvalidHiringException;

	Employee addNewEmployerToEmployee(Long employeeId, EmployerDTO employerDTO) throws EntityNotFoundException;

	void deleteById(Long theId) throws EntityNotFoundException;
}
