package gr.aueb.cf.springapp.controller;

import gr.aueb.cf.springapp.dto.EmployeeDTO;
import gr.aueb.cf.springapp.dto.EmployerDTO;
import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.service.IEmployerService;
import gr.aueb.cf.springapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.springapp.service.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides endpoints for managing employers in the system.
 * It includes operations for viewing, adding, deleting, and updating employers, releasing employees.
 */
@Controller
@RequestMapping("/employers")
public class EmployerController {

    private final IEmployerService employerService;

    @Autowired
    public EmployerController(IEmployerService employerService) {
        this.employerService = employerService;
    }

    /**
     * Fetches a list of all employers and all  employees,
     * mapping them to DTOs and adding them to the model for the view.
     *
     * @param model The Model instance for populating view attributes.
     * @return Returns a string to direct the application to the employers-table view.
     */
    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public String getEmployers(Model model){
        List<Employer> employers = employerService.findAllEmployers();
        List<Employee> employees = employerService.findAllEmployees();

        List<EmployerDTO> employerDTOS = new ArrayList<>();

        for (Employer employer : employers){
            employerDTOS.add(new EmployerDTO(employer.getId(),employer.getName(),
                    employer.getAddress(),employer.getAllEmployees(),employer.getAllProjects()));
        }

        List<EmployeeDTO> employeeDTOS = new ArrayList<>();

        for (Employee employee : employees){
            employeeDTOS.add(new EmployeeDTO(employee.getId(),employee.getFirstname(),employee.getLastname(),
                    employee.getJobTitle(),employee.getSalary(),employee.getEmployer(),employee.getAllProjects()));
        }

        model.addAttribute("employers", employerDTOS);
        model.addAttribute("employees", employeeDTOS);
        return "employers/employers-table";
    }


    /**
     * Presents a form to the user to add a new employer.
     *
     * @param model The Model instance for populating view attributes.
     * @return Returns a string to direct the application to the employer-form view.
     */
    @RequestMapping(path = "/addEmployer", method = RequestMethod.GET)
    public String showFormForAdd(Model model){

        EmployerDTO employerDTO = new EmployerDTO();

        model.addAttribute("employerForm", employerDTO);
        return "employers/employer-form";
    }

    /**
     * Handles the creation of a new employer based on provided DTO data, validating it before saving.
     * If the validation fails or the employer already exists, it logs the error and returns to the form with an error message.
     * Otherwise, it saves the employer and redirects to the list of employers.
     *
     * @param employerDTO Data Transfer Object representing the new employer's data.
     * @param bindingResult Results of the validation of the employerDTO.
     * @param model The Model instance for populating view attributes.
     * @param redirectAttributes To store flash attributes before redirecting the user.
     * @return Returns a string to direct the application depending on the outcome of the operation.
     */
    @RequestMapping(path = "/save", method = RequestMethod.POST)
    public String insertEmployer(@Valid @ModelAttribute("employerForm")EmployerDTO employerDTO, BindingResult bindingResult,
                                 Model model, RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please correct the errors in order to proceed!");
            LoggerUtil.getCurrentLogger().warning("empty");
            return "employers/employer-form";
        }

        try {
            Employer employer =  employerService.insertEmployer(employerDTO);
            EmployerDTO addedDTO = map(employer);

            redirectAttributes.addFlashAttribute("addedDTO", addedDTO );

            return "redirect:/employers/list";

        }catch (EntityAlreadyExistsException exception) {
            LoggerUtil.getCurrentLogger().warning(exception.getMessage());
            model.addAttribute("error", exception.getMessage());
            return "error";
        }
    }


    /**
     * Deletes an employer identified by the provided employerId.
     * If the employer is found, it deletes the employer and redirects to the list of employers.
     * Otherwise, it logs the error and redirects to an error page.
     *
     * @param employerId the id of the employer to be deleted.
     * @param model The Model instance for populating view attributes.
     * @param redirectAttributes To store flash attributes before redirecting the user.
     * @return Returns a string to direct the application depending on the outcome of the operation.
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam("employerId") Long employerId, Model model,
                         RedirectAttributes redirectAttributes){

        if (employerId == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Please choose an employer");
            return "redirect:/employers/list";
        }

        try {
            Employer employer = employerService.findById(employerId);

            employerService.deleteEmployer(employerId);
            EmployerDTO employerDTO =map (employer);
            redirectAttributes.addFlashAttribute("deletedDTO",   employerDTO);

            return "redirect:/employers/list";
        }catch (EntityNotFoundException e){
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Displays the form for updating the data of an employer identified by the provided employerId.
     * If the employer is not found, it redirects to an error page.
     * Otherwise, it populates the model with the current data and redirects to the form.
     *
     * @param employerId The id of the employer to be updated.
     * @param model The Model instance for populating view attributes.
     * @param redirectAttributes To store flash attributes before redirecting the user.
     * @return Returns a string to direct the application depending on the outcome of the operation.
     */
    @RequestMapping(path = "/showFormForUpdate", method = RequestMethod.GET)
    public String showFormForUpdate(@RequestParam("employerId") Long employerId,
                                    Model model, RedirectAttributes redirectAttributes){
        if (employerId == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Please choose an employer");
            return "redirect:/employers/list";
        }

        try {
            Employer employer = employerService.findById(employerId);

            EmployerDTO employerDTO = new EmployerDTO(employer.getId(),employer.getName()
                    ,employer.getAddress(),employer.getAllEmployees(),employer.getAllProjects());

            List<Employee> employees = employerService.findEmployeesByEmployerId(employerId);
            List<EmployeeDTO> employeeDTOS = new ArrayList<>();

            for (Employee employee : employees){
                employeeDTOS.add(new EmployeeDTO(employee.getId(),employee.getFirstname(),employee.getLastname(),
                        employee.getJobTitle(),employee.getSalary(),employee.getEmployer(),employee.getAllProjects()));
            }


            model.addAttribute("employees", employeeDTOS);
            model.addAttribute("employerForm", employerDTO);

            return "employers/update-form";

        }catch (EntityNotFoundException ex){
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
    }

    /**
     * Updates an employer's data based on provided DTO data, validating it before saving.
     * If the validation fails or the employer doesn't exist, it logs the error and returns to the form with an error message.
     * Otherwise, it updates the employer and redirects to the list of employers.
     *
     * @param employerDTO Data Transfer Object representing the updated employer's data.
     * @param bindingResult Results of the validation of the employerDTO.
     * @param model The Model instance for populating view attributes.
     * @param redirectAttributes To store flash attributes before redirecting the user.
     * @return Returns a string to direct the application depending on the outcome of the operation.
     */
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updateEmployee(@Valid @ModelAttribute("employerForm") EmployerDTO employerDTO, BindingResult bindingResult, Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please correct the errors in order to proceed!");
            LoggerUtil.getCurrentLogger().warning("empty");
            return "employers/update-form";
        }

        try {
            Employer employer =	employerService.updateEmployer(employerDTO);
            EmployerDTO dtoUpdated = map(employer);
            redirectAttributes.addFlashAttribute("updatedDTO", dtoUpdated);

            return "redirect:/employers/list";
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Releases an employee from an employer and his projects.
     * If the employee or employer is not found, it logs the error and redirects to an error page.
     * Otherwise, it removes the association and redirects to the list of employers.
     *
     * @param employeeId Identifier of the employee to be released.
     * @param employerId Identifier of the employer from which the employee is to be released.
     * @param model The Model instance for populating view attributes.
     * @return Returns a string to direct the application depending on the outcome of the operation.
     */
    @RequestMapping(path = "/releaseEmployee", method = RequestMethod.POST)
    public String releaseEmployeeFromEmployer(@RequestParam("employeeId") Long employeeId,
                                              @RequestParam("employerId") Long employerId,
                                              Model model) {

        try {

            Employee employee = employerService.getEmployeeById(employeeId);
            Employer employer = employerService.findById(employerId);

            employerService.removeEmployeeFromEmployer(employeeId, employerId);


            return "redirect:/employers/list";

        }catch (EntityNotFoundException e){
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            model.addAttribute("error",  e.getMessage());
            return "error";
        }
    }


    /**
     * Maps an Employer entity into an EmployerDTO.
     *
     * @param employer The Employer entity to be mapped.
     * @return Returns the EmployerDTO instance corresponding to the provided employer.
     */
    private EmployerDTO map(Employer employer) {

        return new EmployerDTO(employer.getId(),
                employer.getName(),employer.getAddress()
                ,employer.getAllEmployees(),employer.getAllProjects());
    }
}

