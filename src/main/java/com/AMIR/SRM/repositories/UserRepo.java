package com.AMIR.SRM.repositories;

import com.AMIR.SRM.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String mail);
    User findByActivationCode(String code);
}
