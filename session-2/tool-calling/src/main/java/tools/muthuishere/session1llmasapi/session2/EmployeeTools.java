package tools.muthuishere.session1llmasapi.session2;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeTools {

    // ============================================================================
    // EXERCISE 3.1: Get all employees
    // Refer to README.md for implementation
    // ============================================================================
    @Tool(description = "Get all employees in the company with their department and role")
    public List<Employee> getEmployees() {
        // TODO: Implement this method - see README Exercise 3.1
        return List.of();
    }

    // ============================================================================
    // EXERCISE 3.2: Get employees by department
    // Refer to README.md for implementation
    // ============================================================================
    @Tool(description = "Get employees filtered by department name")
    public List<Employee> getEmployeesByDepartment(
            @ToolParam(description = "Department name like Engineering, Product, Design, Sales") String department) {
        // TODO: Implement this method - see README Exercise 3.2
        return List.of();
    }
}
