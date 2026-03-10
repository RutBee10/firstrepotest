package com.example.firstSpringProject.controller;

import com.example.firstSpringProject.exception.ResourceNotFoundException;
import com.example.firstSpringProject.model.Student;
import com.example.firstSpringProject.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student createdStudent = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return ResponseEntity.ok(student);
    }

    @PutMapping("{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable long id, @Valid @RequestBody Student studentDetails) {
        Student updateStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        updateStudent.setFirstName(studentDetails.getFirstName());
        updateStudent.setLastName(studentDetails.getLastName());
        updateStudent.setEmailId(studentDetails.getEmailId());
        updateStudent.setDateOfBirth(studentDetails.getDateOfBirth());
        updateStudent.setAddress(studentDetails.getAddress());
        updateStudent.setPhoneNumber(studentDetails.getPhoneNumber());

        Student updatedStudent = studentRepository.save(updateStudent);

        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        studentRepository.delete(student);

        return ResponseEntity.noContent().build();
    }
}





=============


    import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class StudentControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentController studentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    public void testGetAllStudents() throws Exception {
        List<Student> students = new ArrayList<>();
        students.add(new Student(1, "John Doe", "johndoe@example.com"));
        students.add(new Student(2, "Jane Doe", "janedoe@example.com"));

        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(get("/api/v1/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("John Doe")))
            .andExpect(jsonPath("$[0].email", is("johndoe@example.com")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].name", is("Jane Doe")))
            .andExpect(jsonPath("$[1].email", is("janedoe@example.com")));
    }

    @Test
    public void testGetStudentById() throws Exception {
        Student student = new Student(1, "John Doe", "johndoe@example.com");

        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.of(student));

        mockMvc.perform(get("/api/v1/students/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("John Doe")))
            .andExpect(jsonPath("$.email", is("johndoe@example.com")));
    }

    @Test
    public void testGetStudentByIdNotFound() throws Exception {
        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/v1/students/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateStudent() throws Exception {
        Student student = new Student(1, "John Doe", "johndoe@example.com");

        when(studentRepository.save(student)).thenReturn(student);

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\", \"email\":\"johndoe@example.com\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("John Doe")))
            .andExpect(jsonPath("$.email", is("johndoe@example.com")));
    }

    @Test
    public void testUpdateStudent() throws Exception {
        Student student = new Student(1, "John Doe", "johndoe@example.com");

        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);

        mockMvc.perform(put("/api/v1/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\", \"email\":\"johndoe@example.com\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("John Doe")))
            .andExpect(jsonPath("$.email", is("johndoe@example.com")));
    }

    @Test
    public void testUpdateStudentNotFound() throws Exception {
        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(put("/api/v1/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\", \"email\":\"johndoe@example.com\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteStudent() throws Exception {
        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.of(new Student(1, "John Doe", 
"johndoe@example.com")));

        mockMvc.perform(delete("/api/v1/students/1"))
            .andExpect(status().isOk());
    }

    @Test
    public void testDeleteStudentNotFound() throws Exception {
        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(delete("/api/v1/students/1"))
            .andExpect(status().isNotFound());
    }
}
```

This test case includes test cases for the following endpoints:
* GET /api/v1/students (to retrieve all students)
* GET /api/v1/students/{id} (to retrieve a student by ID)
* GET /api/v1/students/{id} (when the student with the given ID is not found)
* POST /api/v1/students (to create a new student)
* PUT /api/v1/students/{id} (to update an existing student)
* PUT /api/v1/students/{id} (when the student with the given ID is not found)
* DELETE /api/v1/students/{id} (to delete an existing student)
* DELETE /api/v1/students/{id} (when the student with the given ID is not found)
