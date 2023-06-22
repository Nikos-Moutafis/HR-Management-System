package gr.aueb.cf.springapp.service.exceptions;

public class InvalidEmployeeAssigmentException extends Exception{
    private static final long serialVersionUID = 1L;

    public InvalidEmployeeAssigmentException(Class<?> entityClass, Long projectId, Long employeeId) {
        super("Invalid employee assigment, project with ID " + projectId + " cannot be assigned to employee with "
                + employeeId + " since they don't belong to the same employer ");
    }
}
