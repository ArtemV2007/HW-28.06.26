package io.github.ArtemV2007.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// 1. Говорим Hibernate, что этот класс является сущностью БД
@Entity
// 2. Указываем имя таблицы в PostgreSQL (желательно во множественном числе или по стандарту)
@Table(name = "users")
public class User {

    // 3. Помечаем поле как первичный ключ (Primary Key)
    @Id
    // 4. Настраиваем автогенерацию ID. IDENTITY идеально подходит для SERIAL/BIGSERIAL в PostgreSQL
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 5. Размечаем обычные колонки. Можно указать ограничения (не null, длина, уникальность)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // Email сделаем уникальным, чтобы нельзя было создать двух пользователей с одинаковой почтой
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "age")
    private Integer age;

    // Настраиваем имя колонки через snake_case, как принято в базах данных
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Пустой конструктор ОБЯЗАТЕЛЕН для Hibernate, чтобы он мог восстанавливать объекты из БД
    public User() {}

    // Конструктор для удобного создания новых пользователей в коде
    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры (остаются без изменений)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createdAt=" + createdAt +
                '}';
    }
}
