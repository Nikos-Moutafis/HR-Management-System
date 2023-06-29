package gr.aueb.cf.springapp.service.impl;

import gr.aueb.cf.springapp.dao.EmployeeRepository;
import gr.aueb.cf.springapp.dao.EmployerRepository;
import gr.aueb.cf.springapp.dto.EmployerDTO;
import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.entity.Project;
import gr.aueb.cf.springapp.service.IEmployerService;
import gr.aueb.cf.springapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployerServiceImpl implements IEmployerService {

    private final EmployerRepository employerRepository;

    private final EmployeeRepository employeeRepository;
    @Autowired
    public EmployerServiceImpl(EmployerRepository employerRepository, EmployeeRepository employeeRepository) {
        this.employerRepository = employerRepository;
        this.employeeRepository = employeeRepository;
    }


    /**
     *
     * Retrieves all employers.
     * @return A list of all employers.
     * */
    @Override
    public List<Employer> findAllEmployers() {
        return employerRepository.findAll();
    }

    /**
     * Retrieves an employer by ID.
     * @param id The ID of the employer to retrieve.
     * @return The employer with the specified ID.
     * @throws EntityNotFoundException If the employer with the specified ID does not exist.
     */
    @Override
    public Employer findById(Long id) throws EntityNotFoundException {
        Employer employer;
        employer = employerRepository.findEmployerById(id);
        if (employer == null) throw new EntityNotFoundException(Employer.class, id);
        return employer;
    }

    /**
     * Updates an existing employer.
     * @param employerDTO The updated employer information.
     * @return The updated employer.
     * @throws EntityNotFoundException If the employer with the specified ID does not exist.
     */
    @Override
    @Transactional
    public Employer updateEmployer(EmployerDTO employerDTO) throws EntityNotFoundException {
        Employer employer = employerRepository.findEmployerById(employerDTO.getId());
        if (employer == null) throw new EntityNotFoundException(Employer.class, employerDTO.getId());
        employer.setId(employerDTO.getId());
        employer.setName(employerDTO.getName());
        employer.setAddress(employerDTO.getAddress());
        employerRepository.save(employer);
        return employer;
    }

    /**
     * Deletes an employer by ID.
     * It also removes employer from all of his employees, deletes all employer's projects
     * and removes the relationship between project and employees(since project and employee must belong to the same employer).
     * @param id The ID of the employer to delete.
     * @throws EntityNotFoundException If the employer with the specified ID does not exist.
     */
    @Override
    @Transactional
    public void deleteEmployer(Long id) throws EntityNotFoundException {
        Employer employer = employerRepository.findEmployerById(id);
        if (employer == null) throw new EntityNotFoundException(Employer.class, id);

        List<Employee> employees = new ArrayList<>(employer.getAllEmployees());
        List<Project> projects = new ArrayList<>(employer.getAllProjects());

        if (!employees.isEmpty()){
            for (Employee employee : employees){
                employee.removeEmployer();
            }
        }


        if (!projects.isEmpty()) {
            for (Project project : projects) {
                employer.deleteProject(project);
                removeProjectFromEmployees(project);
            }
        }
        employerRepository.deleteById(id);
    }

    /**
     * Removes a project from all its employees.
     * (Also in the deleteProject convenient method employees are removed from the project)
     *@param project  projects to remove from employees .
     *
     */
    private void removeProjectFromEmployees(Project project) {
        List<Employee> employees = new ArrayList<>(project.getAllEmployees());
        for (Employee employee : employees) {
            employee.deleteProject(project);
        }
    }


    /**
     * Retrieves all employees of an employer.
     * @param id The ID of the employer.
     * @return A list of all employees belonging to the employer.
     * @throws EntityNotFoundException If the employer with the specified ID does not exist.
     */
    @Override
    public List<Employee> findEmployeesByEmployerId(Long id) throws EntityNotFoundException {
        Employer employer = employerRepository.findEmployerById(id);
        if (employer == null) throw new EntityNotFoundException(Employer.class, id);
        return employer.getAllEmployees();
    }


    /**
     * Retrieves all employees.
     * @return A list of all employees.
     */
    @Override
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAllByOrderByLastname();
    }

    /**
     * Retrieves an employee by ID.
     * @param id The ID of the employee to retrieve.
     * @return The employee with the specified ID.
     * @throws EntityNotFoundException If the employee with the specified ID does not exist.
     */
    @Override
    public Employee getEmployeeById(Long id) throws EntityNotFoundException {
        Employee employee = employeeRepository.findEmployeeById(id);
        if (employee == null) throw new EntityNotFoundException(Employee.class, id);
        return employee;
    }


    /**
     * Inserts a new employer.
     * @param employerDTO The employer to insert.
     * @return The inserted employer.
     * @throws EntityAlreadyExistsException If the employer with the specified ID already exists.
     */
    @Override
    @Transactional
    public Employer insertEmployer(EmployerDTO employerDTO) throws EntityAlreadyExistsException {
        if (employerDTO.getId() != null) {
            throw  new EntityAlreadyExistsException(Employer.class, employerDTO.getId());
        }
        return employerRepository.save(mapToEmployer(employerDTO));
    }


    /**
     * Removes an employee from an employer.It also removes all projects from employee
     * since project and employee must belong to the same employer
     *
     * @param employeeId The ID of the employee to remove.
     * @param employerId The ID of the employer from which to remove the employee.
     * @throws EntityNotFoundException If the employee or employer with the specified IDs does not exist.
     */
    @Override
    @Transactional
    public void removeEmployeeFromEmployer(Long employeeId, Long employerId) throws EntityNotFoundException {

        Employee employee = employeeRepository.findEmployeeById(employeeId);
        if (employee == null) throw  new EntityNotFoundException(Employee.class, employeeId);

        Employer employer = employerRepository.findEmployerById(employerId);
        if (employer == null) throw  new EntityNotFoundException(Employer.class, employerId);

        //check if employee has an employer before removing the relationship
        if (employee.getEmployer() == null) {
            throw new EntityNotFoundException(Employer.class, employerId);
        }

        List<Project> projects = new ArrayList<>(employee.getAllProjects());

        removeEmployeeFromProjects(employee,projects);

        employer.deleteEmployee(employee);
    }

    /**
     * Removes an employee from projects.
     * (Also in the deleteEmployee convenient method projects are removed from the employee)
     * @param employee The employee to remove from projects.
     * @param projects The list of projects to remove the employee from.
     */
    private void removeEmployeeFromProjects(Employee employee, List<Project> projects)  {
        for (Project project : projects) {
            project.deleteEmployee(employee);
//            projectService.updateProject(mapToProjectDTO(project));
        }
    }

    /**
     * Maps an EmployerDTO to an Employer entity.
     * @param employerDTO The EmployerDTO to map.
     * @return The mapped Employer entity.
     */
    private Employer mapToEmployer(EmployerDTO employerDTO){
        return new Employer(employerDTO.getId(),employerDTO.getName(),
                employerDTO.getAddress(),employerDTO.getEmployees(),employerDTO.getProjects());
    }
}
