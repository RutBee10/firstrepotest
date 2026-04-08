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


/*

Make this prompt proper it will use input for LLM models. You are expert of creating working java spring boot AI gradle application with integrated UI. Single page UI app having facility to select LLM like generic configuration like it can use local ollama LLM or chat gpt or Perplexity or google gemini or microsoft copilot configuration from UI only no need to go to write configuration manually default LLM is Claude Sonnet 4.6. Application will get input from textarea or having facility to upload multiple documents that is taken as prompt for AI models and generate outputs that can be download via links or displayed in output textarea,  at a time multiple model can also be selected like it can select chat gpt and Microsoft copilot so two document will be generated from each llm.


Prompt:
You are an expert in building Java Spring Boot AI Gradle applications with an integrated single-page UI.
Requirements:
1. 	UI Features
• 	Single-page application with a clean, interactive interface.
• 	Dropdown or selection panel to choose the LLM configuration.
• 	Supported LLMs:
• 	Local Ollama LLM
• 	ChatGPT
• 	Perplexity
• 	Google Gemini
• 	Microsoft Copilot
• 	Default LLM: Claude Sonnet 4.6.
• 	No manual configuration required — all model selection happens from the UI.
2. 	Input Handling
• 	Textarea for direct prompt input.
• 	Option to upload multiple documents (PDF, DOCX, TXT) as input prompts.
3. 	Output Handling
• 	AI-generated outputs can be:
• 	Displayed in an output textarea.
• 	Downloaded via links (e.g., as text or document files).
• 	If multiple models are selected (e.g., ChatGPT + Microsoft Copilot), the system generates separate outputs for each model simultaneously.
4. 	Application Behavior
• 	Backend: Java Spring Boot with Gradle build system.
• 	Frontend: Integrated UI (React/Angular/Vue or Thymeleaf — flexible choice).
• 	API endpoints handle:
• 	Model selection
• 	Input submission (text or documents)
• 	Output retrieval and download

Example Scenario:
• 	User selects ChatGPT and Microsoft Copilot from the UI.
• 	Uploads two documents + enters a custom prompt in the textarea.
• 	Application sends the combined input to both models.
• 	Two separate outputs are generated:
• 	One from ChatGPT
• 	One from Microsoft Copilot
• 	Outputs are displayed in the UI and downloadable via links.
    */
    
