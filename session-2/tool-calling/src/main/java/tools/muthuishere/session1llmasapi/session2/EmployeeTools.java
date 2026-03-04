package tools.muthuishere.session1llmasapi.session2;

import org.springframework.ai.chat.model.ToolCallbackDescription;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeTools {

    // ============================================================================
    // EXERCISE 3.1: Get all employees
    // Refer to README.md for implementation
    // ============================================================================
    @ToolCallbackDescription("Get all employees in the company with their department and role")
    public List<Employee> getEmployees() {
        // TODO: Implement this method - see README Exercise 3.1
        return List.of();
    }

    // ============================================================================
    // EXERCISE 3.2: Get employees by department
    // Refer to README.md for implementation
    // ============================================================================
    @ToolCallbackDescription("Get employees filtered by department name")
    public List<Employee> getEmployeesByDepartment(
            @ToolCallbackDescription("Department name like Engineering, Product, Design, Sales") String department) {
        // TODO: Implement this method - see README Exercise 3.2
        return List.of();
    }
}
