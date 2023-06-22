package gr.aueb.cf.springapp.service.exceptions;

public class ProjectAlreadyAssignedInEmployee extends Exception{
    private static final long serialVersionUID = 1L;

    public ProjectAlreadyAssignedInEmployee(Class<?> entityClass, Long employeeId) {
        super( "Entity: "  + entityClass.getSimpleName() +
                " is already assigned to employee with id: " + employeeId);
    }
}
