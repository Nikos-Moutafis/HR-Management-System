package gr.aueb.cf.springapp.validator;

import gr.aueb.cf.springapp.dto.ProjectDTO;
import gr.aueb.cf.springapp.enums.Status;
import gr.aueb.cf.springapp.service.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * A validator for validating the fields of ProjectDTO object.
 */
@Component
public class ProjectValidator implements Validator {

    private final IProjectService projectService;

    @Autowired
    public ProjectValidator(IProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ProjectDTO.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProjectDTO projectDTO = (ProjectDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"name","empty");
        if (projectDTO.getName().length() < 3 || projectDTO.getName().length() > 20){
            errors.rejectValue("name", "name.size");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "empty");
        if (projectDTO.getDescription().length() < 5 || projectDTO.getDescription().length() > 35){
            errors.rejectValue("description", "description.size");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "empty");
        String startDateToValid = projectDTO.getStartDate();
        if (!projectService.isDateFormatted(startDateToValid)){
            errors.rejectValue("startDate","formatDate");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDate", "empty");
        String endDateToValid = projectDTO.getEndDate();
        if (!projectService.isDateFormatted(endDateToValid)){
            errors.rejectValue("endDate","formatDate");
        }

        if (!projectService.isDateValid(startDateToValid)){
            errors.rejectValue("startDate","validDate");
        }

        if (!projectService.isDateValid(endDateToValid)){
            errors.rejectValue("endDate","validDate");
        }


        if (!projectService.isEndAfterStartDate(startDateToValid,endDateToValid)){
            errors.rejectValue("endDate", "datesOrder");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "employer", "employerRequired");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors , "status", "validStatus");
        if (projectDTO.getStatus()== null || (projectDTO.getStatus() != Status.ACTIVE) && (projectDTO.getStatus() != Status.INACTIVE)){
            errors.rejectValue("status","validStatus");
        }

    }
}
