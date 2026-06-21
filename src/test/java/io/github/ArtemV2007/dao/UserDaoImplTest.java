package io.github.ArtemV2007.dao;

import io.github.ArtemV2007.model.User;
import io.github.ArtemV2007.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Включаем поддержку Testcontainers в JUnit 5
@Testcontainers
class UserDaoImplTest {

    // 1. Объявляем и настраиваем Docker-контейнер с PostgreSQL
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    private static UserDao userDao;

    // 2. Настраиваем Hibernate на работу с контейнером ПЕРЕД запуском всех тестов
    @BeforeAll
    static void beforeAll() throws Exception {
        // Запускаем контейнер (если аннотация @Container не сделала это автоматически)
        postgres.start();

        // Переопределяем конфигурацию Hibernate динамическими данными из Docker
        Configuration configuration = new Configuration().configure();
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());

        // Для тестов лучше использовать режим create-drop, чтобы схема пересоздавалась
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        // С помощью рефлексии подменяем SessionFactory в HibernateUtil на тестовую
        Field sessionFactoryField = HibernateUtil.class.getDeclaredField("sessionFactory");
        sessionFactoryField.setAccessible(true);

        // Снимаем модификатор final у поля через дескрипторы (или просто собираем новую фабрику)
        // Но проще и надежнее переинициализировать фабрику, если бы у нас был не static final,
        // а метод инициализации. Так как у нас в HibernateUtil жесткий Singleton, мы создаем фабрику напрямую:
        var testSessionFactory = configuration.buildSessionFactory();

        // Записываем тестовую фабрику в наш HibernateUtil
        sessionFactoryField.set(null, testSessionFactory);

        userDao = new UserDaoImpl();
    }

    // 3. Очищаем таблицу перед КАЖДЫМ тестом для обеспечения их полной изоляции
    @BeforeEach
    void setUp() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @DisplayName("Сохранение пользователя должно записывать данные в реальную БД")
    void save_ShouldPersistUserInDatabase() {
        // Arrange
        User user = new User("Петр", "petr@mail.com", 28);

        // Act
        userDao.save(user);

        // Assert
        assertNotNull(user.getId(), "ID должен сгенерироваться базой данных");

        // Проверяем напрямую через сессию, что данные записались
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User dbUser = session.get(User.class, user.getId());
            assertNotNull(dbUser);
            assertEquals("Петр", dbUser.getName());
            assertEquals("petr@mail.com", dbUser.getEmail());
            assertNotNull(dbUser.getCreatedAt(), "Дата создания должна быть заполнена");
        }
    }

    @Test
    @DisplayName("Поиск по ID должен возвращать корректную запись")
    void findById_ShouldReturnCorrectUser() {
        // Arrange
        User user = new User("Елена", "elena@mail.com", 33);
        saveDirectly(user); // сохраняем напрямую, минуя тестируемый метод dao

        // Act
        User foundUser = userDao.findById(user.getId());

        // Assert
        assertNotNull(foundUser);
        assertEquals("Елена", foundUser.getName());
        assertEquals("elena@mail.com", foundUser.getEmail());
    }

    @Test
    @DisplayName("Получение всех пользователей должно возвращать полный список записей")
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        saveDirectly(new User("User1", "u1@mail.com", 20));
        saveDirectly(new User("User2", "u2@mail.com", 25));

        // Act
        List<User> users = userDao.findAll();

        // Assert
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Обновление пользователя должно изменять данные в таблице")
    void update_ShouldModifyDataInDatabase() {
        // Arrange
        User user = new User("Игорь", "igor@mail.com", 19);
        saveDirectly(user);

        // Меняем данные в объекте
        user.setName("Игорь Обновленный");
        user.setEmail("new_igor@mail.com");

        // Act
        userDao.update(user);

        // Assert
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User dbUser = session.get(User.class, user.getId());
            assertEquals("Игорь Обновленный", dbUser.getName());
            assertEquals("new_igor@mail.com", dbUser.getEmail());
        }
    }

    @Test
    @DisplayName("Удаление пользователя должно стирать запись из БД")
    void delete_ShouldRemoveUserFromDatabase() {
        // Arrange
        User user = new User("Клон", "clone@mail.com", 50);
        saveDirectly(user);

        // Act
        userDao.delete(user.getId());

        // Assert
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User dbUser = session.get(User.class, user.getId());
            assertNull(dbUser, "Пользователь должен быть удален из базы данных");
        }
    }

    // Вспомогательный метод для сохранения сущностей в тестах в обход UserDaoImpl
    private void saveDirectly(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
        }
    }
}
