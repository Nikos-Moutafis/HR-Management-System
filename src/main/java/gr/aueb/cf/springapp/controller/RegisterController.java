package gr.aueb.cf.springapp.controller;

import gr.aueb.cf.springapp.dto.UserDTO;
import gr.aueb.cf.springapp.entity.User;
import gr.aueb.cf.springapp.service.IAutoLoginService;
import gr.aueb.cf.springapp.service.IUserService;
import gr.aueb.cf.springapp.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * This class is a controller responsible for handling user registration.
 * It provides endpoints for displaying the registration form and processing user registration requests.
 */
@Controller
public class RegisterController {
    private final IUserService userService;

    private final UserValidator userValidator;

    private final IAutoLoginService autoLoginService;
    @Autowired
    public RegisterController(IUserService userService, UserValidator userValidator,IAutoLoginService autoLoginService) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.autoLoginService = autoLoginService;
    }

    /**
     * Displays the registration form to the user.
     * @param model The Model instance to add attributes for the view.
     * @return The name of the view to render the registration form.
     */
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("userForm", new UserDTO());
        return "register";
    }


    /**
     * Handles the user registration process by validating the user input, registering the user, and performing auto-login.
     * If there are validation errors, it returns to the registration form.
     * @param userDTO The UserDTO instance containing the user's registration data.
     * @param bindingResult The BindingResult instance for handling validation errors.
     * @return The name of the view to render after successful registration or in case of validation errors.
     */
    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("userForm") UserDTO userDTO, BindingResult bindingResult){
        userValidator.validate(userDTO,bindingResult);
        if (bindingResult.hasErrors()){
            return "register";
        }

        User user = userService.registerUser(userDTO);
        autoLoginService.autoLogin(user);
        return "redirect:/employees/list";
    }
}
