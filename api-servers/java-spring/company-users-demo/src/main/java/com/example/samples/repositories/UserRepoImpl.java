package com.example.samples.repositories;

import com.example.samples.models.Login;
import com.example.samples.models.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;


@Repository
public class UserRepoImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Boolean auth(Login login) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.username = :username AND u.email = :email";
        Long count = entityManager.createQuery(jpql, Long.class)
                                  .setParameter("username", login.username)
                                  .setParameter("email", login.email)
                                  .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user != null) {
            entityManager.persist(user); // Insert new entity
        } else {
            entityManager.merge(user); // Update existing entity
        }
        return user;
    }

    @Override
    @Transactional
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    @Transactional
    public List<User> findAll() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }

}
