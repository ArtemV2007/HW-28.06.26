package io.github.ArtemV2007;

import io.github.ArtemV2007.dao.UserDao;
import io.github.ArtemV2007.model.User;
import io.github.ArtemV2007.service.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Временная заглушка вместо реальной БД, чтобы код компилировался
        UserDao mockDao = new UserDao() {
            @Override public void save(User u) { System.out.println("Сохранено в заглушку: " + u); }
            @Override public User findById(Long id) { return null; }
            @Override public java.util.List<User> findAll() { return java.util.Collections.emptyList(); }
            @Override public void update(User u) { System.out.println("Обновлено в заглушке: " + u); }
            @Override public void delete(Long id) { System.out.println("Удалено из заглушки, ID: " + id); }
        };

        UserService userService = new UserService(mockDao);
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
                    System.out.println("Выход из программы.");
                    return;
                default:
                    System.out.println("Неверный пункт меню!");
            }
        }
    }
}
