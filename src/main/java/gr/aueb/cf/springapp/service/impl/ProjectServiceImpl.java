package gr.aueb.cf.springapp.service.impl;

import gr.aueb.cf.springapp.dao.EmployeeRepository;
import gr.aueb.cf.springapp.dao.EmployerRepository;
import gr.aueb.cf.springapp.dao.ProjectRepository;
import gr.aueb.cf.springapp.dto.ProjectDTO;
import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.entity.Project;
import gr.aueb.cf.springapp.service.IProjectService;
import gr.aueb.cf.springapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.springapp.service.exceptions.InvalidEmployeeAssigmentException;
import gr.aueb.cf.springapp.service.exceptions.ProjectAlreadyAssignedInEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProjectServiceImpl implements IProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    private final EmployerRepository employerRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, EmployeeRepository employeeRepository, EmployerRepository employerRepository) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.employerRepository = employerRepository;
    }

    /**
     *
     * Retrieves a list of all employees.
     *@return The list of employees
     */
    @Override
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAllByOrderByLastname();
    }

    /**
     * Retrieves a list of all employers.
     *
     * @return The list of employers
     */
    @Override
    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }

    /**
     * Retrieves a list of all projects.
     *
     * @return The list of projects
     */
    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }


    /**
     * Retrieves a project by its ID.
     *
     * @param id The ID of the project
     * @return The project with the specified ID
     * @throws EntityNotFoundException If the project is not found
     */
    @Override
    @Transactional
    public Project findById(Long id) throws EntityNotFoundException {
        Project project;
        project = projectRepository.findProjectById(id);
        if (project == null) throw new EntityNotFoundException(Project.class, id);
        return project;
    }

    /**
     * Inserts a new project.
     *
     * @param projectDTO The project data transfer object
     * @return The inserted project
     * @throws EntityAlreadyExistsException If the project already exists
     */
    @Override
    @Transactional
    public Project insertProject(ProjectDTO projectDTO) throws EntityAlreadyExistsException {
        if (projectDTO.getId() != null) {
            throw  new EntityAlreadyExistsException(Project.class, projectDTO.getId());
        }

        Project project = mapToProject(projectDTO);
        Employer employer = projectDTO.getEmployer();
        project.setEmployer(employer);
        employer.addProject(project);
        return projectRepository.save(project);

    }

    /**
     * Updates an existing project.
     *
     * @param projectDTO The project data transfer object
     * @return The updated project
     * @throws EntityNotFoundException If the project is not found
     */
    @Override
    @Transactional
    public Project updateProject(ProjectDTO projectDTO) throws EntityNotFoundException {
        Project project = projectRepository.findProjectById(projectDTO.getId());
        if (project == null) throw new EntityNotFoundException(Project.class, projectDTO.getId());
        project.setId(projectDTO.getId());
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setStatus(projectDTO.getStatus());

        projectRepository.save(project);
        return project;
    }

    /**
     * Deletes a project by its ID.It also removes project from all employees associated with it
     * and sets its employer to null.
     *
     * @param id The ID of the project to delete
     * @throws EntityNotFoundException If the project is not found
     */
    @Override
    @Transactional
    public void deleteProject(Long id) throws EntityNotFoundException {
        Project project;
        project = projectRepository.findProjectById(id);
        if (project == null) throw new EntityNotFoundException(Project.class, id);

        removeProjectFromEmployees(project);

        project.setEmployer(null);
        projectRepository.deleteById(id);
    }


    /**
     * Removes a project from all associated employees.
     *
     * @param project The project to remove
     */
    private void removeProjectFromEmployees(Project project) {
        List<Employee> employees = new ArrayList<>(project.getAllEmployees());
        if (employees.size() == 0) {
            return;
        }
        for (Employee employee : employees) {
            employee.deleteProject(project);
        }
    }


    /**
     * Assigns a project to an employee.
     *
     * @param projectId   The ID of the project
     * @param employeeId  The ID of the employee
     * @return The assigned employee
     * @throws EntityNotFoundException          If the project or employee is not found
     * @throws InvalidEmployeeAssigmentException If the employee cannot be assigned to the project due to different employer
     * @throws ProjectAlreadyAssignedInEmployee  If the project is already assigned to the employee
     */
    @Override
    @Transactional
    public Employee assignProjectToEmployee(Long projectId, Long employeeId)
            throws EntityNotFoundException, InvalidEmployeeAssigmentException, ProjectAlreadyAssignedInEmployee {

        Project project = projectRepository.findProjectById(projectId);
        if (project == null) throw new EntityNotFoundException(Project.class, projectId);


        Employee employee = employeeRepository.findEmployeeById(employeeId);
        if (employee == null) throw new EntityNotFoundException(Employee.class, employeeId);

        if (employee.getEmployer() == null || !employee.getEmployer().getId().equals(project.getEmployer().getId())){
            throw new InvalidEmployeeAssigmentException(Project.class, projectId, employeeId);
        }

        if (employee.getAllProjects().contains(project)) {
            throw new ProjectAlreadyAssignedInEmployee(Project.class, employeeId);
        }

        project.addEmployee(employee);
        return employee;
    }


    /**
     * Unassigns an employee from a project.
     *
     * @param projectId  The ID of the project
     * @param employeeId The ID of the employee
     * @return The unassigned employee
     * @throws EntityNotFoundException If the project or employee is not found
     */
    @Override
    @Transactional
    public Employee unassignEmployeeFromProject(Long projectId, Long employeeId) throws EntityNotFoundException {

        Project project = projectRepository.findProjectById(projectId);
        if (project == null) throw new EntityNotFoundException(Project.class, projectId);

        Employee employee = employeeRepository.findEmployeeById(employeeId);
        if (employee == null) throw new EntityNotFoundException(Employee.class, employeeId);

        project.deleteEmployee(employee);
        employee.deleteProject(project);

        return employee;
    }


    /**
     * Checks if a date string is in the correct format (yyyy-MM-dd).
     *
     * @param date The date string to check
     * @return true if the date is formatted correctly, false otherwise
     */
    @Override
    public boolean isDateFormatted(String date) {
        Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }


    /**
     * Checks if a date string is a valid date and not before the current date.
     *
     * @param dateToValid The date string to validate
     * @return true if the date is valid, false otherwise
     */
    @Override
    public boolean isDateValid(String dateToValid){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date;

        try {
            date = LocalDate.parse(dateToValid, formatter);
        }catch (DateTimeParseException e) {
            return false;
        }

        return !date.isBefore(LocalDate.now());
    }

    /**
     * Checks if end date of the project is after the start date.
     *
     * @param startDateToValid The start date string
     * @param endDateToValid   The end date string
     * @return true if the end date is after the start date, false otherwise
     */
    @Override
    public boolean isEndAfterStartDate(String startDateToValid, String endDateToValid) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(startDateToValid, formatter);
        endDate = LocalDate.parse(endDateToValid, formatter);
        }catch (DateTimeParseException e) {
            return false;
        }

        return endDate.isAfter(startDate);
    }

    /**
     * Maps a project DTO to a project entity.
     *
     * @param projectDTO The project DTO to map
     * @return The mapped project entity
     */
    private Project mapToProject(ProjectDTO projectDTO) {
        return new Project(projectDTO.getId(), projectDTO.getName(), projectDTO.getDescription()
                , projectDTO.getStartDate(), projectDTO.getEndDate(), projectDTO.getStatus(),
                projectDTO.getEmployer(), projectDTO.getEmployeeList());
    }
}
