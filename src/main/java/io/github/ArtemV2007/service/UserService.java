package io.github.ArtemV2007.service;

import io.github.ArtemV2007.dao.UserDao;
import io.github.ArtemV2007.model.User;
import java.util.List;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser(String name, String email, Integer age) {
        User user = new User(name, email, age);
        userDao.save(user);
    }

    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public void updateUser(Long id, String newName, String newEmail, Integer newAge) {
        User user = userDao.findById(id);
        if (user != null) {
            user.setName(newName);
            user.setEmail(newEmail);
            user.setAge(newAge);
            userDao.update(user);
        } else {
            System.out.println("Пользователь с ID " + id + " не найден!");
        }
    }

    public void deleteUser(Long id) {
        userDao.delete(id);
    }
}
