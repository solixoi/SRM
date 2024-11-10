package com.AMIR.SRM.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public String fetchProviderNameByProviderId(Long providerId) {
        String query = "SELECT u.username FROM User u WHERE u.id = :providerId";
        try {
            return (String) entityManager.createQuery(query)
                    .setParameter("providerId",  providerId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
