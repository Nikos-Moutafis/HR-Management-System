package gr.aueb.cf.springapp.controller;

import gr.aueb.cf.springapp.dto.EmployeeDTO;
import gr.aueb.cf.springapp.dto.EmployerDTO;
import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.service.IEmployeeService;
import gr.aueb.cf.springapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.springapp.service.exceptions.InvalidHiringException;
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
 * The EmployeeController class handles HTTP requests for employee resources.
 * The class is annotated with @Controller to indicate that it's a Spring MVC Controller.
 * The class is also annotated with @RequestMapping("/employees") to map web requests onto specific handler methods.
 */
@Controller
@RequestMapping("/employees")
public class EmployeeController {

	private final IEmployeeService employeeService;

	@Autowired
	public EmployeeController(IEmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	/**
	 * This method returns a list of all employees and employers.
	 * It maps to the "/list" endpoint and is a GET request.
	 * It retrieves all Employees and Employers from the database calling Employee service, maps them to their DTOs,
	 * and adds them to the Model.
	 * The method returns a String that is used to find the corresponding view (in this case "employees/employees-table").
	 *
	 * @param model the Model object that carries data to the view.
	 * @return a String representing the name of the view.
	 */
	@RequestMapping(path = "/list" ,method = RequestMethod.GET)
	public String listEmployees(Model model)  {

		List<Employee> theEmployees = employeeService.findAllEmployees();
		List<EmployeeDTO> employeeDTOS = new ArrayList<>();

		for (Employee employee : theEmployees){
			employeeDTOS.add(new EmployeeDTO(employee.getId(),employee.getFirstname(),
					employee.getLastname(),employee.getJobTitle(),employee.getSalary(),
					employee.getEmployer(),employee.getAllProjects()));
		}


		List<EmployerDTO> employerDTOS = getAllEmployersInDtos();
		model.addAttribute("employers",employerDTOS);
		model.addAttribute("employees", employeeDTOS);

		return "employees/employees-table";
	}

	/**
	 * This method retrieves all employers from database in a list,
	 * then converts the list of Employer entities to EmployerDTO objects.
	 * It iterates through each Employer, creates a corresponding EmployerDTO object, and adds it to a list.
	 *
	 * @return a List of EmployerDTO objects.
	 */
	private List<EmployerDTO> getAllEmployersInDtos() {
		List<Employer> employers = employeeService.getAllEmployers();
		List<EmployerDTO> employerDTOS = new ArrayList<>();

		for (Employer employer : employers){
			employerDTOS.add(new EmployerDTO(employer.getId(),employer.getName(),employer.getAddress(),
					employer.getAllEmployees(),employer.getAllProjects()));
		}
		return employerDTOS;
	}

	/**
	 * This method is mapped to the "/showFormForAdd" endpoint and is a GET request.
	 * It prepares the model for creating a new Employee by initializing an EmployeeDTO object and adding it to the model.
	 * Then it returns the view in order to add a new Employee.
	 *
	 * @param model the Model object that carries data to the view.
	 * @return a String representing the name of the view.
	 */
	@RequestMapping(path = "/showFormForAdd", method = RequestMethod.GET)
	public String showFormForAdd(Model model){

		EmployeeDTO employeeDTO = new EmployeeDTO();

		model.addAttribute("employeeForm", employeeDTO);
		return "employees/employee-form";
	}


	/**
	 * This method is mapped to the "/save" endpoint and is a POST request.
	 * It attempts to save the Employee details provided by the form to the database.
	 * If the Employee already exists, it handles the error and returns to the form view.
	 * Otherwise, it redirects to the previous view with appropriate message.
	 *
	 * @param employeeDTO the Employee data transfer object carrying the form inputs.
	 * @param bindingResult results of the form validation.
	 * @param model the Model object that carries data to the view.
	 * @param redirectAttributes attributes to pass to the redirected page.
	 * @return a String representing the name of the view.
	 */
	@RequestMapping(path = "/save", method = RequestMethod.POST)
	public String insertEmployee( @ModelAttribute("employeeForm") @Valid EmployeeDTO employeeDTO, BindingResult bindingResult,
								  Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "Please correct the errors in order to proceed!");
			LoggerUtil.getCurrentLogger().warning("empty");
			return "employees/employee-form";
		}

		try {
			Employee employee =	employeeService.insertEmployee(employeeDTO);

			redirectAttributes.addFlashAttribute("addedDTO", map(employee));
			return "redirect:/employees/list";
		}catch (EntityAlreadyExistsException exception) {
			LoggerUtil.getCurrentLogger().warning(exception.getMessage());
			model.addAttribute("error", exception.getMessage());
			return "error";
		}
	}


	/**
	 * This method is mapped to the "/showFormForUpdate" endpoint and is a GET request.
	 * It retrieves the Employee with the given ID from the database and prepares the model for updating
	 * by adding the EmployeeDTO to the model. Then it returns the view name for the form to update the Employee.
	 *
	 * @param id the ID of the Employee to update.
	 * @param model the Model object that carries data to the view.
	 * @return a String representing the name of the view.
	 */
	@RequestMapping(path = "/showFormForUpdate", method = RequestMethod.GET)
	public String showFormForUpdate(@RequestParam("employeeId") Long id, Model model){
		try {
			Employee employee = employeeService.findById(id);

			EmployeeDTO employeeDTO = new EmployeeDTO(employee.getId(),employee.getFirstname()
					,employee.getLastname(),employee.getJobTitle(),employee.getSalary(),
					employee.getEmployer(),employee.getAllProjects());

			model.addAttribute("employeeForm", employeeDTO);

			return "employees/update-form";

		}catch (EntityNotFoundException ex){
			model.addAttribute("error", ex.getMessage());
			return "error";
		}
	}


	/**
	 * This method is mapped to the "/update" endpoint and is a POST request.
	 * It attempts to update the Employee details provided by the form into the database.
	 * If the Employee doesn't exist, it handles the error and returns to the form view. Otherwise,
	 * it redirects to the previous view.
	 *
	 * @param employeeDTO the Employee data transfer object carrying the form inputs.
	 * @param bindingResult results of the form validation.
	 * @param model the Model object that carries data to the view.
	 * @param redirectAttributes attributes to pass to the redirected page.
	 * @return a String representing the name of the view.
	 */
	@RequestMapping(path = "/update", method = RequestMethod.POST)
	public String updateEmployee(@Valid @ModelAttribute("employeeForm") EmployeeDTO employeeDTO, BindingResult bindingResult,
								 Model model, RedirectAttributes redirectAttributes) {


		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "Please correct the errors in order to proceed!");
			LoggerUtil.getCurrentLogger().warning("empty");
			return "employees/update-form";
		}
		try {
			Employee updateEmployee =	employeeService.updateEmployee(employeeDTO);
			EmployeeDTO dtoUpdated = map(updateEmployee);
			redirectAttributes.addFlashAttribute("updatedDTO", dtoUpdated);

			return "redirect:/employees/list";
		} catch (EntityNotFoundException e) {
			LoggerUtil.getCurrentLogger().warning(e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}

	/**
	 * This method is mapped to the "/delete" endpoint.
	 * It deletes the Employee with the given ID from the database and then redirects to the list view.
	 *
	 * @param employeeId the ID of the Employee to delete.
	 * @param model the Model object that carries data to the view.
	 * @param redirectAttributes attributes to pass to the redirected page.
	 * @return a String representing the name of the view.
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(@RequestParam("employeeId") Long employeeId,
						 Model model, RedirectAttributes redirectAttributes){

		try {
			Employee employee = employeeService.findById(employeeId);
			employeeService.deleteById(employeeId);

			EmployeeDTO employeeDTO = map(employee);
			redirectAttributes.addFlashAttribute("deletedDTO",   employeeDTO);

			return "redirect:/employees/list";
		}catch (EntityNotFoundException e){
			LoggerUtil.getCurrentLogger().warning(e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}

	/**
	 * This method is mapped to the "/showFormForEmployer" endpoint and is a GET request.
	 * It retrieves the Employee with the given ID from the database and prepares the model for adding a new Employer.
	 * Then it returns the view name for the form to add the Employer.
	 *
	 * @param employeeId the ID of the Employee to add the Employer.
	 * @param model the Model object that carries data to the view.
	 * @return a String representing the name of the view.
	 */

	@GetMapping("/showFormForEmployer")
	public String showFormForEmployer(@RequestParam("employeeId") Long employeeId, Model model) {
		try {
			Employee employee = employeeService.findById(employeeId);

			EmployerDTO employerDTO = new EmployerDTO();

			EmployeeDTO employeeDTO = map(employee);

			model.addAttribute("employee", employeeDTO);
			model.addAttribute("employer", employerDTO);
			return "employees/employer-form";
		}catch (EntityNotFoundException e){
			LoggerUtil.getCurrentLogger().warning(e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}

	/**
	 * This method is mapped to the "/addEmployer" endpoint and is a POST request.
	 * It adds a new Employer to the Employee with the given ID and then redirects to the list view.
	 *
	 * @param employeeId the ID of the Employee to add the Employer.
	 * @param employerDTO the Employer data transfer object carrying the form inputs.
	 * @param bindingResult results of the form validation.
	 * @param model the Model object that carries data to the view.
	 * @param redirectAttributes attributes to pass to the redirected page.
	 * @return a String representing the name of the view.
	 */
	@PostMapping("/addEmployer")
	public String addNewEmployerToEmployee(@RequestParam("employeeId") Long employeeId,
										@ModelAttribute("employer")@Valid EmployerDTO employerDTO,
										BindingResult bindingResult,Model model, RedirectAttributes redirectAttributes) {
		try {
			Employee employee = employeeService.findById(employeeId);

			if (bindingResult.hasErrors()) {
				model.addAttribute("employee", map(employee));
				model.addAttribute("errorMessage", "Please correct the errors in order to proceed!");
				LoggerUtil.getCurrentLogger().warning("empty");
				return "employees/employer-form";
			}

			Employee employeeHired = employeeService.addNewEmployerToEmployee(employeeId, employerDTO);

			redirectAttributes.addFlashAttribute("employeeAddedEmployer",map(employeeHired));
			return "redirect:/employees/list";
		}catch (EntityNotFoundException e) {
			LoggerUtil.getCurrentLogger().warning(e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}

	/**
	 * This method is mapped to the "/addExistingEmployer" endpoint and is a POST request.
	 * It adds an existing Employer to the Employee with the given ID and then redirects to the list view.
	 *
	 * @param employeeId the ID of the Employee to add the Employer.
	 * @param employerId the ID of the Employer to add to the Employee.
	 * @param redirectAttributes attributes to pass to the redirected page.
	 * @param model the Model object that carries data to the view.
	 * @return a String representing the name of the view.
	 */
	@PostMapping("/addExistingEmployer")
	public String addExistingEmployer(@RequestParam(value = "employeeId") Long employeeId,
									  @RequestParam(value = "employerId")  Long employerId,
									  RedirectAttributes redirectAttributes,Model model) {
		try {
			Employee employee = employeeService.findById(employeeId);
			Employer employer = employeeService.getEmployerById(employerId);

			Employee employeeHired = employeeService.addEmployerToEmployee(employeeId,employerId);
			redirectAttributes.addFlashAttribute("employeeAddedEmployer", map(employeeHired));
			return "redirect:/employees/list";
		}catch (EntityNotFoundException e){
			LoggerUtil.getCurrentLogger().warning(e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "error";
		}catch (InvalidHiringException e) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}

	/**
	 * This method maps the Employee entity to the EmployeeDTO object.
	 * It's a helper function that encapsulates the process of mapping the properties of the Employee to the EmployeeDTO.
	 *
	 * @param employee the Employee entity to map.
	 * @return an EmployeeDTO object that corresponds to the given Employee.
	 */
	private EmployeeDTO map(Employee employee) {
		EmployeeDTO employeeDTO = new EmployeeDTO();
		employeeDTO.setId(employee.getId());
		employeeDTO.setFirstname(employee.getFirstname());
		employeeDTO.setLastname(employee.getLastname());
		employeeDTO.setJobTitle(employee.getJobTitle());
		employeeDTO.setEmployer(employee.getEmployer());
		employeeDTO.setSalary(employee.getSalary());
		employeeDTO.setProjects(employee.getAllProjects());
		return employeeDTO;
	}
}