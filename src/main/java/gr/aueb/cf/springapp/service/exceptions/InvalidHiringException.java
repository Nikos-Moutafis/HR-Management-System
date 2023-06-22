package gr.aueb.cf.springapp.service.exceptions;

public class InvalidHiringException extends Exception{

    private static final long serialVersionUID = 1L;

    public InvalidHiringException(Class<?> entityClass, Long employeeId) {
        super("Invalid hiring exception  employee with id: " + employeeId  + " cannot be hired by " +
                "another employer because he is already assigned to one, you must release him from current employer" +
                "  first  "  + entityClass.getSimpleName());
    }
}
