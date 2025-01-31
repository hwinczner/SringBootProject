package com.SpringBoot.Project.Controllers;

import com.SpringBoot.Project.Models.Employee;
import com.SpringBoot.Project.Models.Department;
import com.SpringBoot.Project.Services.DepartmentService;
import com.SpringBoot.Project.Services.EmployeeService;
import com.SpringBoot.Project.Models.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    // GET all employees
    @GetMapping
    public Result<List<Employee>> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // GET employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Result<Employee>> getEmployeeById(@PathVariable Long id) {
        Result<Employee> result = employeeService.getEmployeeById(id);

        // If the employee was found
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);  // Return 200 OK with employee data
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);  // Return 404 if employee not found
        }
    }

    // POST create a new employee
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
        if (employee.getDepartment() == null) {
            return new ResponseEntity<>("Department ID is required", HttpStatus.BAD_REQUEST);
        }

        long departmentId = employee.getDepartment().getDepartmentId();
        Result<Department> department = departmentService.getDepartmentById(departmentId);

        if (department == null) {
            return new ResponseEntity<>("Department with ID " + departmentId + " not found", HttpStatus.BAD_REQUEST);
        }

        employee.setDepartment(department.getData());

        Result<Employee> result = employeeService.saveOrUpdateEmployee(employee);

        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getData(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(result.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // PUT update an employee
    @PutMapping("/{id}")
    public ResponseEntity<Result<Employee>> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        // Check if employee ID in the path matches the ID in the request body
        if (!id.equals(employee.getEmployeeId())) {
            return ResponseEntity.badRequest().body(Result.failure("Employee ID in the path does not match the ID in the request body.", List.of("ID mismatch")));
        }

        Result<Employee> result = employeeService.saveOrUpdateEmployee(employee);

        // Return response based on result success
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }


    // DELETE employee by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteEmployee(@PathVariable Long id) {
        // Call service to delete employee by ID
        Result<Void> result = employeeService.deleteEmployeeById(id);

        // Return response based on result success
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }


}
