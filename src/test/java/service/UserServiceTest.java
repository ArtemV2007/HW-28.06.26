package service;

import io.github.ArtemV2007.dto.UserRequestDTO;
import io.github.ArtemV2007.dto.UserResponseDTO;
import io.github.ArtemV2007.model.User;
import io.github.ArtemV2007.repository.UserRepository;
import io.github.ArtemV2007.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Создание пользователя должно корректно сохранять объект через репозиторий и возвращать DTO")
    void createUser_ShouldSaveUserViaRepositoryAndReturnDto() {
        // Arrange
        UserRequestDTO requestDto = new UserRequestDTO("Иван", "ivan@example.com", 25);

        User savedUser = new User("Иван", "ivan@example.com", 25);
        savedUser.setId(1L);
        savedUser.setCreatedAt(LocalDateTime.now());

        // Обучаем mock возвращать сохраненного пользователя с ID при вызове save
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponseDTO responseDto = userService.createUser(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.id());
        assertEquals("Иван", responseDto.name());
        assertEquals("ivan@example.com", responseDto.email());

        // Перехватываем сущность, которую сервис отправлял на сохранение
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User transientUser = userCaptor.getValue();
        assertEquals("Иван", transientUser.getName());
        assertEquals("ivan@example.com", transientUser.getEmail());
    }

    @Test
    @DisplayName("Поиск по ID должен возвращать DTO пользователя, если он существует")
    void getUserById_ShouldReturnDto_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User user = new User("Анна", "anna@example.com", 30);
        user.setId(userId);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO responseDto = userService.getUserById(userId);

        // Assert
        assertNotNull(responseDto);
        assertEquals(userId, responseDto.id());
        assertEquals("Анна", responseDto.name());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Поиск по ID должен выбрасывать исключение 404, если пользователя нет")
    void getUserById_ShouldThrowNotFound_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Получение всех пользователей должно маппиться в список DTO")
    void getAllUsers_ShouldReturnListOfDtos() {
        // Arrange
        User user1 = new User("User1", "user1@mail.com", 20);
        user1.setId(1L);
        User user2 = new User("User2", "user2@mail.com", 22);
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<UserResponseDTO> actualUsers = userService.getAllUsers();

        // Assert
        assertEquals(2, actualUsers.size());
        assertEquals("User1", actualUsers.get(0).name());
        assertEquals("User2", actualUsers.get(1).name());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Обновление пользователя должно изменять поля и вызывать репозиторий, если юзер найден")
    void updateUser_ShouldModifyAndSave_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User("Старый", "old@mail.com", 40);
        existingUser.setId(userId);

        UserRequestDTO updateDto = new UserRequestDTO("Новый", "new@mail.com", 45);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        UserResponseDTO responseDto = userService.updateUser(userId, updateDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals("Новый", existingUser.getName());
        assertEquals("new@mail.com", existingUser.getEmail());
        assertEquals(45, existingUser.getAge());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Обновление пользователя должно бросать 404 ошибку, если юзер не найден")
    void updateUser_ShouldThrowNotFound_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 99L;
        UserRequestDTO updateDto = new UserRequestDTO("Имя", "email@mail.com", 30);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(userId, updateDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Удаление пользователя должно вызывать метод репозитория, если он существует")
    void deleteUser_ShouldCallRepositoryDelete_WhenUserExists() {
        // Arrange
        Long userId = 5L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Удаление пользователя должно бросать 404 ошибку, если его нет")
    void deleteUser_ShouldThrowNotFound_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 5L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
