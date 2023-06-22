package gr.aueb.cf.springapp.validator;

import gr.aueb.cf.springapp.dto.UserDTO;
import gr.aueb.cf.springapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * A validator for validating the fields of UserDTO object.
 */
@Component
public class UserValidator implements Validator {

    private final IUserService userService;

    @Autowired
    public UserValidator(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UserDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userToRegister = (UserDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "empty");
        if (userToRegister.getUsername().length() < 3 || userToRegister.getUsername().length() > 32) {
            errors.rejectValue("username", "size");
        }

        if (userService.usernameAlreadyExists(userToRegister.getUsername())) {
            errors.rejectValue("username", "duplicate");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty");
        if (userToRegister.getPassword().length() < 3 || userToRegister.getPassword().length() > 32) {
            errors.rejectValue("password", "size");
        }

        String password = userToRegister.getPassword();
        if (!userService.isPasswordValid(password)){
            errors.rejectValue("password","invalidPass");
        }
    }
}
