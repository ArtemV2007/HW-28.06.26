package io.github.ArtemV2007.service;

import io.github.ArtemV2007.dao.UserDao;
import io.github.ArtemV2007.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Подключаем расширение Mockito к JUnit 5
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // Создаем виртуальную заглушку (Mock) для слоя DAO
    @Mock
    private UserDao userDao;

    // Автоматически создаем UserService и внедряем в него созданный mock-объект userDao
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Создание пользователя должно корректно передавать объект в DAO")
    void createUser_ShouldSaveUserViaDao() {
        // Arrange (Подготовка данных)
        String name = "Иван";
        String email = "ivan@example.com";
        Integer age = 25;

        // Создаем ArgumentCaptor для перехвата объекта, который сервис передаст в DAO
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act (Выполнение действия)
        userService.createUser(name, email, age);

        // Assert (Проверка результатов)
        // Проверяем, что метод save у userDao был вызван ровно 1 раз, и перехватываем переданный аргумент
        verify(userDao, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser, "Сохраненный пользователь не должен быть null");
        assertEquals(name, savedUser.getName());
        assertEquals(email, savedUser.getEmail());
        assertEquals(age, savedUser.getAge());
    }

    @Test
    @DisplayName("Поиск по ID должен возвращать пользователя, если он существует")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User expectedUser = new User("Анна", "anna@example.com", 30);
        expectedUser.setId(userId);

        // Обучаем Mock: когда вызовут findById(1L), нужно вернуть expectedUser
        when(userDao.findById(userId)).thenReturn(expectedUser);

        // Act
        User actualUser = userService.getUserById(userId);

        // Assert
        assertNotNull(actualUser);
        assertEquals(userId, actualUser.getId());
        assertEquals("Анна", actualUser.getName());
        // Проверяем, что метод findById был вызван ровно с этим ID
        verify(userDao, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Получение всех пользователей должно возвращать список из DAO")
    void getAllUsers_ShouldReturnList() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(
                new User("User1", "user1@mail.com", 20),
                new User("User2", "user2@mail.com", 22)
        );
        when(userDao.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertEquals(2, actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Обновление пользователя должно изменять поля и вызывать DAO, если юзер найден")
    void updateUser_ShouldModifyAndSave_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User("Старый", "old@mail.com", 40);
        existingUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(existingUser);

        // Act
        userService.updateUser(userId, "Новый", "new@mail.com", 45);

        // Assert
        assertEquals("Новый", existingUser.getName());
        assertEquals("new@mail.com", existingUser.getEmail());
        assertEquals(45, existingUser.getAge());

        // Проверяем, что после изменения полей метод update был вызван
        verify(userDao, times(1)).update(existingUser);
    }

    @Test
    @DisplayName("Обновление пользователя не должно вызывать DAO, если юзер не найден")
    void updateUser_ShouldDoNothing_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 99L;
        when(userDao.findById(userId)).thenReturn(null);

        // Act
        userService.updateUser(userId, "Имя", "email@mail.com", 30);

        // Assert
        // Проверяем, что метод update НИ РАЗУ не вызывался, так как юзера нет
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Удаление пользователя должно вызывать метод delete в DAO")
    void deleteUser_ShouldCallDaoDelete() {
        // Arrange
        Long userId = 5L;

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userDao, times(1)).delete(userId);
    }
}
