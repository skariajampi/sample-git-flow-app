// Unit Test - src/test/java/com/example/myapp/GreetingControllerTest.java
package com.example.samplegitflowspringboot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GreetingController.class)
class GreetingControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void greetingShouldReturnHelloWorld() throws Exception {
        mockMvc.perform(get("/api/greeting"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("Hello, World!"));
    }
    
    @Test
    void greetingShouldReturnHelloWithName() throws Exception {
        mockMvc.perform(get("/api/greeting").param("name", "Jenkins"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("Hello, Jenkins!"));
    }
}