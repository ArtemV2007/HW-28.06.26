package repository;

import io.github.ArtemV2007.model.User;
import io.github.ArtemV2007.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Поднимает только репозитории и слой JPA, изолируя тест от контроллеров и консоли
@Testcontainers
// Отключает встроенную H2 базу данных, заставляя Spring использовать конфигурацию Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    @Autowired
    private UserRepository userRepository;

    // Динамически подставляет порты и урл из запущенного Docker-контейнера в конфигурацию Spring
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        // Очищаем таблицу перед каждым тестом для полной изоляции
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Сохранение пользователя должно записывать данные в реальную БД")
    void save_ShouldPersistUserInDatabase() {
        // Arrange
        User user = new User("Петр", "petr@mail.com", 28);

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getId(), "ID должен сгенерироваться базой данных");

        Optional<User> dbUserOpt = userRepository.findById(savedUser.getId());
        assertTrue(dbUserOpt.isPresent());
        assertEquals("Петр", dbUserOpt.get().getName());
        assertEquals("petr@mail.com", dbUserOpt.get().getEmail());
        assertNotNull(dbUserOpt.get().getCreatedAt(), "Дата создания должна быть заполнена");
    }

    @Test
    @DisplayName("Поиск по ID должен возвращать корректную запись")
    void findById_ShouldReturnCorrectUser() {
        // Arrange
        User user = userRepository.save(new User("Елена", "elena@mail.com", 33));

        // Act
        Optional<User> foundUserOpt = userRepository.findById(user.getId());

        // Assert
        assertTrue(foundUserOpt.isPresent());
        User foundUser = foundUserOpt.get();
        assertEquals("Елена", foundUser.getName());
        assertEquals("elena@mail.com", foundUser.getEmail());
    }

    @Test
    @DisplayName("Получение всех пользователей должно возвращать полный список записей")
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        userRepository.save(new User("User1", "u1@mail.com", 20));
        userRepository.save(new User("User2", "u2@mail.com", 25));

        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Обновление пользователя должно изменять данные в таблице")
    void update_ShouldModifyDataInDatabase() {
        // Arrange
        User user = userRepository.save(new User("Игорь", "igor@mail.com", 19));

        // Меняем данные через сеттеры
        user.setName("Игорь Обновленный");
        user.setEmail("new_igor@mail.com");

        // Act
        userRepository.save(user); // Переиспользуем save для обновления данных

        // Assert
        Optional<User> dbUserOpt = userRepository.findById(user.getId());
        assertTrue(dbUserOpt.isPresent());
        assertEquals("Игорь Обновленный", dbUserOpt.get().getName());
        assertEquals("new_igor@mail.com", dbUserOpt.get().getEmail());
    }

    @Test
    @DisplayName("Удаление пользователя должно стирать запись из БД")
    void delete_ShouldRemoveUserFromDatabase() {
        // Arrange
        User user = userRepository.save(new User("Клон", "clone@mail.com", 50));

        // Act
        userRepository.deleteById(user.getId());

        // Assert
        Optional<User> dbUserOpt = userRepository.findById(user.getId());
        assertTrue(dbUserOpt.isEmpty(), "Пользователь должен быть удален из базы данных");
    }
}
