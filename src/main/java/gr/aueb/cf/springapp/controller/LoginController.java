package gr.aueb.cf.springapp.controller;

import gr.aueb.cf.springapp.authentication.CustomAuthenticationSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * LoginController is a controller class that handles login-related requests and
 * redirects the user to the appropriate views.
 */
@Controller
public class LoginController {

    /**
     * Handles the GET request for the "/login" path, which is the login page.
     * It sets the redirect URL in the session attribute to the previous page the user accessed
     * and returns the login view.
     * @param model The Model instance for populating view attributes.
     * @param principal The Principal instance representing the currently authenticated user.
     * @param request The HttpServletRequest instance representing the current request.
     * @return Returns the name of the login view if the user is not authenticated, or redirects
     * to the home page if already authenticated.
     * @throws Exception If an exception occurs during the request handling process.
     */
    @GetMapping(path = "/login")
    String login(Model model, Principal principal, HttpServletRequest request) throws Exception {

        String referer = request.getHeader("Referer");
        request.getSession().setAttribute(CustomAuthenticationSuccessHandler.REDIRECT_URL_SECTION_ATTRIBUTE_NAME,
                referer);

        return principal == null ? "login" : "redirect:/home";
    }

    /**
     *
     * Handles the GET request for the root ("/") path.
     * It checks if the user is authenticated and either returns the login view if not authenticated or redirects
     * to the home page if already authenticated.
     * @param model The Model instance for populating view attributes.
     * @param principal The Principal instance representing the currently authenticated user.
     * @param request The HttpServletRequest instance representing the current request.
     * @return Returns the name of the login view if the user is not authenticated, or redirects to
     * the home page if already authenticated.
     * @throws Exception If an exception occurs during the request handling process.
     * */

    @GetMapping(path = "/")
    String root(Model model, Principal principal, HttpServletRequest request) throws Exception {
        return principal == null ? "/login" : "redirect:/home";
    }

    /**
     * Handles the GET request for the "/home" path, which is the home page.
     * @return Returns the name of the home-page view.
     */
    @GetMapping("/home")
    String getHome(){
        return "home-page";
    }
}
