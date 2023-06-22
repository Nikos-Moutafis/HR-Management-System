package gr.aueb.cf.springapp.service.impl;

import gr.aueb.cf.springapp.dao.EmployeeRepository;
import gr.aueb.cf.springapp.dao.EmployerRepository;
import gr.aueb.cf.springapp.dto.EmployeeDTO;
import gr.aueb.cf.springapp.dto.EmployerDTO;
import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.entity.Project;
import gr.aueb.cf.springapp.service.IEmployeeService;
import gr.aueb.cf.springapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.springapp.service.exceptions.InvalidHiringException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class EmployeeServiceImpl implements IEmployeeService {
	private final EmployeeRepository employeeRepository;
	private final EmployerRepository employerRepository;
	@Autowired
	public EmployeeServiceImpl(EmployeeRepository theEmployeeRepository, EmployerRepository employerRepository) {
		employeeRepository = theEmployeeRepository;
		this.employerRepository = employerRepository;
	}

	/**
	 * Retrieves a list of all employees.
	 * @return A list of all employees.
	 */
	@Override
	public List<Employee> findAllEmployees() {
		return employeeRepository.findAllByOrderByLastname();
	}


	/**
	 * Retrieves an employee by their ID.
	 * @param theId The ID of the employee to retrieve.
	 * @return The retrieved employee.
	 * @throws EntityNotFoundException if no employee is found with the specified ID.
	 */
	@Override
	public Employee findById(Long theId) throws EntityNotFoundException {
		Employee employee;
		employee = employeeRepository.findEmployeeById(theId);
		if (employee == null) throw new EntityNotFoundException(Employee.class, theId);
		return employee;
	}


	/**
	 * Retrieves an employer by their ID.
	 * @param id The ID of the employer to retrieve.
	 * @return The retrieved employer.
	 * @throws EntityNotFoundException if no employer is found with the specified ID.
	 */
	@Override
	public Employer getEmployerById(Long id) throws EntityNotFoundException {
		Employer employer = employerRepository.findEmployerById(id);
		if (employer == null) throw  new EntityNotFoundException(Employer.class, id);
		return employer;
	}

	/**
	 * Retrieves a list of projects associated with an employee.
	 * @param employeeId The ID of the employee.
	 * @return A list of projects associated with the employee.
	 * @throws EntityNotFoundException if no employee is found with the specified ID.
	 */
	@Override
	public List<Project> getProjectsByEmployeeId(Long employeeId) throws EntityNotFoundException {
		Employee employee = employeeRepository.findEmployeeById(employeeId);
		if (employee == null )throw new EntityNotFoundException(Employee.class, employeeId);
		return employeeRepository.findProjectsByEmployeeId(employeeId);

	}

	/**
	 * Inserts a new employee.
	 * @param employeeDTO The DTO object containing employee information.
	 * @return The inserted employee.
	 * @throws EntityAlreadyExistsException if the employee already exists (ID is not null).
	 */
	@Override
	@Transactional
	public Employee insertEmployee(EmployeeDTO employeeDTO) throws EntityAlreadyExistsException {
		if (employeeDTO.getId() != null) {
			throw  new EntityAlreadyExistsException(Employee.class, employeeDTO.getId());
		}

		return 	employeeRepository.save(mapToNewEmployee(employeeDTO));
	}

	/**
	 * Updates an existing employee.
	 * @param employeeDTO The DTO object containing updated employee information.
	 * @return The updated employee.
	 * @throws EntityNotFoundException if no employee is found with the specified ID.
	 */
	@Transactional
	@Override
	public Employee updateEmployee(EmployeeDTO employeeDTO) throws EntityNotFoundException {
		Employee employee = employeeRepository.findEmployeeById(employeeDTO.getId());
		if (employee == null) throw new EntityNotFoundException(Employee.class, employeeDTO.getId());

		employee.setId(employeeDTO.getId());
		employee.setFirstname(employeeDTO.getFirstname());
		employee.setLastname(employeeDTO.getLastname());
		employee.setJobTitle(employeeDTO.getJobTitle());
		employee.setSalary(employeeDTO.getSalary());

		return employeeRepository.save(employee);
	}


	/**
	 * Deletes an employee by his ID after deleting the relationship between employee and all of his projects
	 * in their respective lists and after removing employer from employee.
	 * @param id The ID of the employee to delete.
	 * @throws EntityNotFoundException if no employee is found with the specified ID.
	 */
	@Override
	@Transactional
	public void deleteById(Long id) throws EntityNotFoundException {
		Employee employee = employeeRepository.findEmployeeById(id);
		if (employee == null )throw new EntityNotFoundException(Employee.class, id);

		removeEmployeeFromProjects(employee);
		employee.removeEmployer();
		employeeRepository.deleteById(id);
	}


	/**
	 * Removes an employee from all projects he is associated with.
	 * @param employee The employee to remove from projects.
	 */
	private void removeEmployeeFromProjects(Employee employee)  {
		List<Project> projects = new ArrayList<>(employee.getAllProjects());

		for (Project project : projects) {
			project.deleteEmployee(employee);
		}
	}


	/**
	 * Adds an employer to an employee.
	 *
	 * @param employeeId The ID of the employee.
	 * @param employerId The ID of the employer to add.
	 * @return The updated employee.
	 * @throws EntityNotFoundException if no employee is found with the specified employee ID.
	 * @throws InvalidHiringException if the employee already has an employer.
	 */
	@Override
	@Transactional
	public Employee addEmployerToEmployee(Long employeeId, Long employerId) throws EntityNotFoundException,
			InvalidHiringException {
		Employee employee = employeeRepository.findEmployeeById(employeeId);
		if (employee == null) throw new EntityNotFoundException(Employee.class, employeeId);

		Employer employer = employerRepository.findEmployerById(employerId);
		if (employer == null) throw new EntityNotFoundException(Employer.class,employerId);

		if (employee.getEmployer() != null){
			throw new InvalidHiringException(Employee.class, employeeId);
		}
		employee.addEmployer(employer);

		return employee;
	}


	/**
	 * Adds a new employer(that doesn't exist in database) to an employee.
	 *
	 * @param employeeId The ID of the employee.
	 * @param employerDTO The DTO object containing the new employer information.
	 * @return The updated employee.
	 * @throws EntityNotFoundException if no employee is found with the specified employee ID.
	 */
	@Override
	@Transactional
	public Employee addNewEmployerToEmployee(Long employeeId, EmployerDTO employerDTO) throws EntityNotFoundException {
		Employee employee = employeeRepository.findEmployeeById(employeeId);
		if (employee == null) throw new EntityNotFoundException(Employee.class, employeeId);

		Employer employer = mapToNewEmployer(employerDTO, employee);

		employerRepository.save(employer);
		return employee;
	}

	/**
	 * Retrieves a list of all employers.
	 * @return A list of all employers.
	 * */
	@Override
	public List<Employer> getAllEmployers() {
		return employerRepository.findAll();
	}

	/**
	 * Maps an EmployerDTO object to a new Employer entity.
	 * @param employerDTO The DTO object containing employer information.
	 * @param employee The employee to associate with the new employer.
	 * @return The new Employer entity.
	 */
	private Employer mapToNewEmployer(EmployerDTO employerDTO, Employee employee) {
		Employer employer = new Employer();
		employer.setName(employerDTO.getName());
		employer.setAddress(employerDTO.getAddress());
		employer.addEmployee(employee);
		return employer;
	}


	/**
	 * Maps an EmployeeDTO object to a new Employee entity.
	 * @param employeeDTO The DTO object containing employee information.
	 * @return The new Employee entity.
	 * */
	private Employee mapToNewEmployee(EmployeeDTO employeeDTO){
		Employee employee = new Employee();
		employee.setId(employeeDTO.getId());
		employee.setFirstname(employeeDTO.getFirstname());
		employee.setLastname(employeeDTO.getLastname());
		employee.setJobTitle(employeeDTO.getJobTitle());
		employee.setSalary(employeeDTO.getSalary());
		return employee;
	}
}






