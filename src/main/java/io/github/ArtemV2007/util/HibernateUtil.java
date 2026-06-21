package io.github.ArtemV2007.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    // Единственный экземпляр фабрики сессий на всё приложение (Singleton)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Читаем настройки из hibernate.cfg.xml и строим фабрику
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Критическая ошибка создания SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Метод для получения фабрики в DAO слое
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // Метод для закрытия всех соединений при выходе из программы
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
