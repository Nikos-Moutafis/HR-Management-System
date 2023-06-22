package gr.aueb.cf.springapp.service;

import gr.aueb.cf.springapp.dto.ProjectDTO;
import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.entity.Project;
import gr.aueb.cf.springapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.springapp.service.exceptions.InvalidEmployeeAssigmentException;
import gr.aueb.cf.springapp.service.exceptions.ProjectAlreadyAssignedInEmployee;

import java.util.List;

public interface IProjectService {

    List<Project> findAll();

    Project findById(Long id) throws EntityNotFoundException;

    Employee assignProjectToEmployee(Long projectId, Long employeeId)
            throws EntityNotFoundException, InvalidEmployeeAssigmentException, ProjectAlreadyAssignedInEmployee;

    Employee unassignEmployeeFromProject(Long projectId, Long employeeId) throws EntityNotFoundException;

    Project insertProject(ProjectDTO projectDTO) throws EntityAlreadyExistsException;

    Project updateProject(ProjectDTO projectDTO) throws EntityNotFoundException;

    void deleteProject(Long id) throws EntityNotFoundException;
    List<Employee> findAllEmployees();
    List<Employer> getAllEmployers();


    boolean isDateFormatted(String date);

    boolean isDateValid(String date);

    boolean isEndAfterStartDate(String startDateToValid, String endDateToValid);
}
