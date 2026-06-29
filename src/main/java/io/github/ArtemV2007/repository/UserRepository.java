package io.github.ArtemV2007.repository;

import io.github.ArtemV2007.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Наследуясь от JpaRepository<User, Long>, мы автоматически получаем:
    // - save(User user) (работает и на создание, и на обновление)
    // - findById(Long id) (возвращает Optional<User>)
    // - findAll() (возвращает List<User>)
    // - deleteById(Long id) (удаляет по ID)
}
