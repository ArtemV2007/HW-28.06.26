package io.github.ArtemV2007.dao;

import io.github.ArtemV2007.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import io.github.ArtemV2007.util.HibernateUtil;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public void save(User user) {
        Transaction transaction = null;
        // Открываем сессию (подключение) к БД
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Требование: Настроить транзакционность. Открываем транзакцию
            transaction = session.beginTransaction();

            // Требование: Использовать Hibernate в качестве ORM
            session.persist(user);

            // Фиксируем изменения в базе данных
            transaction.commit();
        } catch (Exception e) {
            // Требование: Обработать возможные исключения
            if (transaction != null) {
                transaction.rollback(); // Откатываем изменения при ошибке
            }
            System.err.println("Ошибка при сохранении пользователя: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            System.err.println("Ошибка при поиске пользователя по ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Используем HQL (Hibernate Query Language) для получения всех записей
            return session.createQuery("from User", User.class).list();
        } catch (Exception e) {
            System.err.println("Ошибка при получении списка пользователей: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.merge(user); // Обновляем данные пользователя в БД

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Ошибка при обновлении пользователя: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user); // Удаляем пользователя из БД
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }
}
