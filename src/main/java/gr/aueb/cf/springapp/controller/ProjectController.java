package gr.aueb.cf.springapp.controller;

import gr.aueb.cf.springapp.dto.EmployeeDTO;
import gr.aueb.cf.springapp.dto.EmployerDTO;
import gr.aueb.cf.springapp.dto.ProjectDTO;
import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.entity.Project;
import gr.aueb.cf.springapp.service.IProjectService;
import gr.aueb.cf.springapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.springapp.service.exceptions.InvalidEmployeeAssigmentException;
import gr.aueb.cf.springapp.service.exceptions.ProjectAlreadyAssignedInEmployee;
import gr.aueb.cf.springapp.service.util.LoggerUtil;
import gr.aueb.cf.springapp.validator.ProjectValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides endpoints for managing projects in the system.
 * It includes operations for viewing, adding, deleting, and updating projects,
 * assigning or releasing projects from employees.
 */
@Controller
@RequestMapping(path = "/projects")
public class ProjectController {

    private final IProjectService projectService;
    private final ProjectValidator projectValidator;

    @Autowired
    public ProjectController(IProjectService projectService, ProjectValidator projectValidator) {
        this.projectService = projectService;
        this.projectValidator = projectValidator;
    }


    /**
     *
     * Handles the GET request for the "/list" path, which lists all projects.
     * Retrieves all projects from the project service, maps them to DTOs, and adds them to the model.
     * @param model The Model instance for populating view attributes.
     * @return The name of the view to render the projects table.
     * */
    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public String listProjects(Model model) {

        List<Project> projects = projectService.findAll();
        List<ProjectDTO> projectDTOS = new ArrayList<>();

        for (Project project : projects){
            projectDTOS.add(new ProjectDTO(project.getId(),project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate(),
                    project.getStatus(), project.getEmployer(), project.getAllEmployees()));
        }

        model.addAttribute("projects", projectDTOS);

        return "projects/projects-table";
    }


    /**
     * Handles the GET request for the "/create" path, which shows the form to create a new project.
     * Retrieves a list of employers from the project service, maps them to DTOs, and adds them to the model.
     * @param model The Model instance for populating view attributes.
     * @return The name of the view to show the create project form.
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String showFormToCreate(Model model){
        List<Employer> employerList = projectService.getAllEmployers();

        List<EmployerDTO> employerDTOS = new ArrayList<>();
        for (Employer employer : employerList){
            employerDTOS.add(new EmployerDTO(employer.getId(),employer.getName(),
                    employer.getAddress(),employer.getAllEmployees(),employer.getAllProjects()));
        }


        model.addAttribute("employers", employerDTOS);
        model.addAttribute("projectForm", new ProjectDTO());
        return "projects/create-projects";
    }

    /**
     * Handles the POST request for the "/create" path, which creates a new project.
     * Validates the projectDTO using the project validator, and if there are validation errors,
     * adds an error message to the flash attributes and returns to the create project form.
     * Otherwise, inserts the project into the project service, maps it to a DTO, adds it to the flash attributes,
     * and redirects to the project list.
     * @param projectDTO The ProjectDTO containing the project data to be created.
     * @param bindingResult The BindingResult instance for handling validation errors.
     * @param redirectAttributes The RedirectAttributes instance for storing flash attributes.
     * @param model The Model instance for populating view attributes.
     * @return The name of the view to redirect based on the outcome of the operation.
     *
     */
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String createProject(@Valid @ModelAttribute("projectForm") ProjectDTO projectDTO,
                                BindingResult bindingResult,RedirectAttributes redirectAttributes,
                                Model model){

        projectValidator.validate(projectDTO, bindingResult);
        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("errorMessage", "Please correct the errors in order to proceed!");
            LoggerUtil.getCurrentLogger().warning("empty");
            List<Employer> employerList = projectService.getAllEmployers();
            model.addAttribute("employers", employerList);
            model.addAttribute("errorMessage", "Please correct the errors in order to proceed!");
            return "projects/create-projects";
        }

        try {
            Project project = projectService.insertProject(projectDTO);
            ProjectDTO projectAddedDTO = mapToProjectDTO(project);
            redirectAttributes.addFlashAttribute("projectDTO", projectAddedDTO );
            return "redirect:/projects/list";
        }catch (EntityAlreadyExistsException exception) {
            LoggerUtil.getCurrentLogger().warning(exception.getMessage());
            model.addAttribute("error", exception.getMessage());
            return "error";
        }
    }

    /**
     * Handles the GET request for the "/assign" path, which shows the form to assign a project to an employee.
     * Retrieve lists of employees and employers from the project service and adds them to the model.
     * @param model The Model instance for populating view attributes.
     * @return The name of the view to show the assign project form.
     */
    @RequestMapping(path = "/assign", method = RequestMethod.GET)
    public String assignProjectForm(Model model) {
        List<Employee> employeeList = projectService.findAllEmployees();
        List<Employer> employerList = projectService.getAllEmployers();

        model.addAttribute("employeeList", employeeList);
        model.addAttribute("employerList", employerList);
        return "projects/assign-projects";
    }

    /**
     * Handles the POST request for the "/assign" path, which assigns a project to an employee.
     * Attempts to assign the employee to the project based on the provided employeeId and projectId.
     * If successful, adds the projectDTO and employeeDTO to the flash attributes and redirects to the assign project form.
     * Otherwise, logs the error, adds the error message to the model, and returns the error page.
     * @param employeeId The ID of the employee to assign.
     * @param projectId The ID of the project to assign.
     * @param model The Model instance for populating view attributes.
     * @param redirectAttributes The RedirectAttributes instance for storing flash attributes.
     * @return The name of the view to redirect based on the outcome of the operation.
     *
     */
    @RequestMapping(path ="/assign", method = RequestMethod.POST)
    public String assignProjectToEmployee(@RequestParam ("employeeId") Long employeeId,
                                          @RequestParam("projectId") Long projectId, Model model,
                RedirectAttributes  redirectAttributes)  {

       try {

           Project project = projectService.findById(projectId);
           Employee employee = projectService.assignProjectToEmployee(projectId,employeeId);
           ProjectDTO projectDTO = mapToProjectDTO(project);
           EmployeeDTO employeeDTO = mapToEmployeeDTO(employee);
           redirectAttributes.addFlashAttribute("projectDTO",projectDTO );
           redirectAttributes.addFlashAttribute("employeeDTO",employeeDTO );
           return "redirect:/projects/assign";
       }catch (EntityNotFoundException | InvalidEmployeeAssigmentException | ProjectAlreadyAssignedInEmployee e){
           model.addAttribute("error", e.getMessage());
           LoggerUtil.getCurrentLogger().warning(e.getMessage());
           return "error";
       }
    }

    /**
     * Handles the POST request for the "/unassign" path, which releases an employee from a project.
     * Attempts to unassign the employee from the project based on the provided employeeId and projectId.
     * If successful, adds the employeeRemovedDTO and projectRemovedDTO to the flash attributes and
     * redirects to the assign projects page.
     * Otherwise, logs the error, adds the error message to the model, and returns the error page.
     * @param projectId The ID of the project to unassign the employee from.
     * @param employeeId The ID of the employee to unassign from the project.
     * @param redirectAttributes The RedirectAttributes instance for storing flash attributes.
     * @param model The Model instance for populating view attributes.
     * @return The name of the view to redirect based on the outcome of the operation.
     */
    @RequestMapping(path = "/unassign",method = RequestMethod.POST)
    public String releaseEmployeeFromProject(@RequestParam("projectId") Long projectId,
                                             @RequestParam("employeeId") Long employeeId,
                                             RedirectAttributes redirectAttributes,Model model){

        try {

            Employee employee = projectService.unassignEmployeeFromProject(projectId, employeeId);
            Project project = projectService.findById(projectId);
            EmployeeDTO employeeDTO = mapToEmployeeDTO(employee);
            ProjectDTO projectDTO = mapToProjectDTO(project);
            redirectAttributes.addFlashAttribute("employeeRemovedDTO", employeeDTO);
            redirectAttributes.addFlashAttribute("projectRemovedDTO", projectDTO);
            return "redirect:/projects/assign";

        }catch (EntityNotFoundException e){
            model.addAttribute("error", e.getMessage());
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return "error";
        }
    }


    /**
     * Handles the GET request for the "/showFormForUpdate" path, which shows the form to update a project.
     * Retrieves the project based on the provided projectId and maps it to a ProjectDTO.
     * Adds the projectDTO to the model and returns the update-form view.
     * @param id The ID of the project to update.
     * @param model The Model instance for populating view attributes.
     * @return The name of the view to show the update project form.
     */
    @RequestMapping(path = "/showFormForUpdate", method = RequestMethod.GET)
    public String showFormForUpdate(@RequestParam("projectId") Long id, Model model){
        try {
            Project project = projectService.findById(id);

            ProjectDTO projectDTO = new ProjectDTO(project.getId(), project.getName(),project.getDescription(),
                    project.getStartDate(),project.getEndDate(), project.getStatus(), project.getEmployer(), project.getAllEmployees());

            model.addAttribute("project", projectDTO);

            return "projects/update-form";

        }catch (EntityNotFoundException ex){
            model.addAttribute("error", "Project not found");
            LoggerUtil.getCurrentLogger().warning(ex.getMessage());
            return "error";
        }
    }

    /**
     * Handles the POST request for the "/update" path, which updates a project.
     * Validates the projectDTO using the project validator, and if there are validation errors,
     * adds an error message to the model and returns the update-form view.
     * Otherwise, updates the project in the project service, maps it to a DTO,
     * adds the updatedDTO to the flash attributes, and redirects to the project list.
     * @param projectDTO The ProjectDTO containing the updated project data.
     * @param bindingResult The BindingResult instance for handling validation errors.
     * @param model The Model instance for populating view attributes.
     * @param redirectAttributes The RedirectAttributes instance for storing flash attributes.
     * @return The name of the view to redirect based on the outcome of the operation.
     */
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updateEmployee(@Valid @ModelAttribute("project") ProjectDTO projectDTO, BindingResult bindingResult,
                                 Model model, RedirectAttributes redirectAttributes) {

        projectValidator.validate(projectDTO,bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please correct the errors in order to proceed!");
            LoggerUtil.getCurrentLogger().warning("empty");
            model.addAttribute("project", projectDTO);
            return "projects/update-form";
        }
        try {
            Project project =	projectService.updateProject(projectDTO);
            ProjectDTO dtoUpdated = mapToProjectDTO(project);
            redirectAttributes.addFlashAttribute("updatedDTO", dtoUpdated);

            return "redirect:/projects/list";
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            model.addAttribute("error", "Project not found");
            return "error";
        }
    }


    /**
     * Handles the request for the "/delete" path, which deletes a project.
     * Deletes the project based on the provided id and adds the deletedDTO to the flash attributes.
     * Then, redirects to the project list.
     * @param projectId The ID of the project to delete.
     * @param model The Model instance for populating view attributes.
     * @param redirectAttributes The RedirectAttributes instance for storing flash attributes.
     * @return The name of the view to redirect based on the outcome of the operation.
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam("projectId") Long projectId,
                         Model model, RedirectAttributes redirectAttributes){

        if (projectId == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Please choose a project to delete");
            return "redirect:/employees/list";
        }

        try {
            Project project = projectService.findById(projectId);
            projectService.deleteProject(projectId);

            ProjectDTO projectDTO = mapToProjectDTO(project);
            redirectAttributes.addFlashAttribute("deletedDTO",   projectDTO);

            return "redirect:/projects/list";
        }catch (EntityNotFoundException e){
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Maps an Employee entity to an EmployeeDTO.
     * @param employee The Employee entity to be mapped.
     * @return The EmployeeDTO instance created
     */
    private EmployeeDTO mapToEmployeeDTO(Employee employee) {
        return new EmployeeDTO(employee.getId(),employee.getFirstname(),employee.getLastname(),
                employee.getJobTitle(),employee.getSalary(),employee.getEmployer(),employee.getAllProjects());
    }

    /**
     * Maps a Project entity to a ProjectDTO.
     * @param project The Project entity to be mapped.
     * @return The ProjectDTO created
     */
    private ProjectDTO mapToProjectDTO(Project project) {

        return new ProjectDTO(project.getId(), project.getName(), project.getDescription(),
                project.getStartDate(),project.getEndDate(), project.getStatus(),
                project.getEmployer(),project.getAllEmployees());
    }
}
