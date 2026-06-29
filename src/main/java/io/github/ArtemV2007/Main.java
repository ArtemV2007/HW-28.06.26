package io.github.ArtemV2007;

import io.github.ArtemV2007.model.User;
import io.github.ArtemV2007.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class Main implements CommandLineRunner {

    // Spring сам внедрит сервис, когда мы его напишем
    private final UserService userService;

    public Main(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
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

            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите число от 1 до 6!");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Введите имя: ");
                    String name = scanner.nextLine();
                    System.out.print("Введите email: ");
                    String email = scanner.nextLine();
                    System.out.print("Введите возраст: ");
                    int age = readIntSafe(scanner);

                    // Передаем данные в сервис (в будущем адаптируем под DTO, если нужно)
                    userService.createUser(name, email, age);
                    System.out.println("Запрос на создание пользователя отправлен.");
                    break;

                case 2:
                    System.out.print("Введите ID: ");
                    long id = readLongSafe(scanner);
                    // Сервис теперь будет возвращать DTO, поэтому тип переменной временно опустим или поменяем позже
                    Object user = userService.getUserById(id);
                    System.out.println(user != null ? user : "Пользователь не найден.");
                    break;

                case 3:
                    userService.getAllUsers().forEach(System.out::println);
                    break;

                case 4:
                    System.out.print("Введите ID пользователя для обновления: ");
                    long updateId = readLongSafe(scanner);
                    System.out.print("Введите новое имя: ");
                    String newName = scanner.nextLine();
                    System.out.print("Введите новый email: ");
                    String newEmail = scanner.nextLine();
                    System.out.print("Введите новый возраст: ");
                    int newAge = readIntSafe(scanner);

                    userService.updateUser(updateId, newName, newEmail, newAge);
                    break;

                case 5:
                    System.out.print("Введите ID для удаления: ");
                    long deleteId = readLongSafe(scanner);
                    userService.deleteUser(deleteId);
                    System.out.println("Запрос на удаление выполнен.");
                    break;

                case 6:
                    System.out.println("Выход из программы.");
                    // Закрытие контекста Spring произойдет автоматически при выходе из метода run
                    return;

                default:
                    System.out.println("Неверный пункт меню!");
            }
        }
    }

    private static int readIntSafe(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Некорректный ввод! Введите целое число: ");
            }
        }
    }

    private static long readLongSafe(Scanner scanner) {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Некорректный ввод! Введите числовой ID: ");
            }
        }
    }
}
