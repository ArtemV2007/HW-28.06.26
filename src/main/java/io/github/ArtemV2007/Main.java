import io.github.ArtemV2007.dao.UserDao;
import io.github.ArtemV2007.dao.UserDaoImpl;
import io.github.ArtemV2007.model.User;
import io.github.ArtemV2007.service.UserService;
import io.github.ArtemV2007.util.HibernateUtil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Подключаем реальную реализацию DAO вместо заглушки
        UserDao userDao = new UserDaoImpl();
        UserService userService = new UserService(userDao);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- МЕНЮ ---");
            System.out.println("1. Создать пользователя");
            System.out.println("2. Найти пользователя по ID");
            System.out.println("3. Показать всех пользователей");
            System.out.println("4. Обновить пользователя");
            System.out.println("5. Удалить пользователя");
            System.out.println("6. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера после nextInt()

            switch (choice) {
                case 1:
                    System.out.print("Введите имя: ");
                    String name = scanner.nextLine();
                    System.out.print("Введите email: ");
                    String email = scanner.nextLine();
                    System.out.print("Введите возраст: ");
                    int age = scanner.nextInt();
                    userService.createUser(name, email, age);
                    break;
                case 2:
                    System.out.print("Введите ID: ");
                    long id = scanner.nextLong();
                    User user = userService.getUserById(id);
                    System.out.println(user != null ? user : "Пользователь не найден.");
                    break;
                case 3:
                    // Исправленный синтаксис вывода списка
                    userService.getAllUsers().forEach(System.out::println);
                    break;
                case 4:
                    System.out.print("Введите ID пользователя для обновления: ");
                    long updateId = scanner.nextLong();
                    scanner.nextLine();
                    System.out.print("Введите новое имя: ");
                    String newName = scanner.nextLine();
                    System.out.print("Введите новый email: ");
                    String newEmail = scanner.nextLine();
                    System.out.print("Введите новый возраст: ");
                    int newAge = scanner.nextInt();
                    userService.updateUser(updateId, newName, newEmail, newAge);
                    break;
                case 5:
                    System.out.print("Введите ID для удаления: ");
                    long deleteId = scanner.nextLong();
                    userService.deleteUser(deleteId);
                    break;
                case 6:
                    System.out.println("Закрытие соединений с базой данных...");
                    HibernateUtil.shutdown(); // Корректно закрываем фабрику сессий Hibernate
                    System.out.println("Выход из программы.");
                    return;
                default:
                    System.out.println("Неверный пункт меню!");
            }
        }
    }
}
