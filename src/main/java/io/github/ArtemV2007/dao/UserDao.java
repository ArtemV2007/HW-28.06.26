package io.github.ArtemV2007.dao;

import io.github.ArtemV2007.model.User;
import java.util.List;

public interface UserDao {
    void save(User user);
    User findById(Long id);
    List<User> findAll();
    void update(User user);
    void delete(Long id);
}
