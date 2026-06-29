package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ArtemV2007.controller.UserController;
import io.github.ArtemV2007.dto.UserRequestDTO;
import io.github.ArtemV2007.dto.UserResponseDTO;
import io.github.ArtemV2007.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class) // Настраивает MockMvc только для UserController
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Создает mock-заглушку сервиса в контексте Spring

    @Autowired
    private ObjectMapper objectMapper; // Поможет превращать DTO в JSON-строку

    @Test
    @DisplayName("GET /api/users/{id} должен возвращать 200 OK и данные пользователя")
    void getUserById_ShouldReturnUser() throws Exception {
        // Arrange
        Long userId = 1L;
        UserResponseDTO response = new UserResponseDTO(userId, "Иван", "ivan@mail.com", 25, LocalDateTime.now());
        Mockito.when(userService.getUserById(userId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@mail.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    @DisplayName("POST /api/users должен возвращать 201 Created при валидных данных")
    void createUser_ShouldReturnCreated() throws Exception {
        // Arrange
        UserRequestDTO request = new UserRequestDTO("Анна", "anna@mail.com", 30);
        UserResponseDTO response = new UserResponseDTO(1L, "Анна", "anna@mail.com", 30, LocalDateTime.now());
        Mockito.when(userService.createUser(Mockito.any(UserRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Анна"));
    }

    @Test
    @DisplayName("POST /api/users должен возвращать 400 Bad Request, если email некорректен")
    void createUser_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        // Arrange & Act & Assert
        // Передаем некорректный email "invalid-email" и отрицательный возраст
        UserRequestDTO invalidRequest = new UserRequestDTO("Анна", "invalid-email", -5);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Срабатывает встроенная валидация Spring (@Valid)
    }

    @Test
    @DisplayName("DELETE /api/users/{id} должен возвращать 244 No Content")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Arrange
        Long userId = 1L;
        Mockito.doNothing().when(userService).deleteUser(userId);

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}
