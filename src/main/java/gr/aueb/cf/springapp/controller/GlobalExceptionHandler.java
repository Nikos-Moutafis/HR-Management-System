package gr.aueb.cf.springapp.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;


/**
 *
 *  GlobalExceptionHandler is a controller advice class that handles exceptions thrown during
 *  the application's request handling process.
 *  It provides centralized exception handling and redirects the user to the error view,
 *  displaying appropriate error messages.
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     *
     * Handles requests to the "/error" path, which is the default error page for the application.
     * @return Returns the name of the error view.
     */
    @GetMapping("/error")
    public String handleError() {
        return "error";
    }

    /**
     * Handles exceptions  type IllegalArgumentException and IllegalStateException.
     * It logs the occurrence of a bad request and returns the error view with the corresponding error message.
     * @param ex The exception that occurred.
     * @param model The Model instance for populating view attributes.
     * @return Returns the name of the error view.
     */
    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    public String handleBadRequest(RuntimeException ex,Model model) {
        String message = ex.getMessage();
        model.addAttribute("error",message);
        return "error";
    }

    /**
     * Handles generic exceptions of type Exception.
     * It logs the occurrence of the exception, prints the stack trace,
     * and returns the error view with the corresponding error message.
     * @param e The exception that occurred.
     * @param model The Model instance for populating view attributes.
     * @return Returns the name of the error view.
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e,Model model) {
        e.printStackTrace();
        String message = e.getMessage();
        model.addAttribute("error",message);
        return "error";
    }

    /**
     * Handles exceptions of type MissingServletRequestParameterException.
     * It sets an error message indicating that all fields need to be selected and returns the error view.
     * @param e The exception that occurred.
     * @param model The Model instance for populating view attributes.
     * @return Returns the name of the error view.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequest(MissingServletRequestParameterException e, Model model){
        model.addAttribute("error","Please select all fields in order to proceed");
        return "error";
    }

}
