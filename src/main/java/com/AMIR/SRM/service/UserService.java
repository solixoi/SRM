package com.AMIR.SRM.service;

import com.AMIR.SRM.domain.Role;
import com.AMIR.SRM.domain.User;
import com.AMIR.SRM.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepo.findByUsername(username);
    }

    public boolean addUser(User user, String role) {
        User userFromDb = userRepo.findByUsername(user.getUsername());
        User mailFromDb = userRepo.findByEmail(user.getEmail());

        if ((userFromDb != null) | (mailFromDb != null)) {
            return false;
        }
        Role roleUser;
        if(role.equals("USER")){
            roleUser = Role.USER;
        } else {
            roleUser = Role.SUPPLIER;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(roleUser));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(user);

//        if (!StringUtils.isEmpty(user.getEmail())) {
//            String message = String.format(
//                    "Здравствуйте, %s! \n" +
//                            "Добро пожаловать в AMIR. Пожалуйста, перейдите по ссылке для подтверждения: http://localhost:8080/activate/%s",
//                    user.getUsername(),
//                    user.getActivationCode()
//            );
//            mailSender.send(user.getEmail(), "Код активации", message);
//        }
        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActivationCode(null);
        user.setActive(true);
        userRepo.save(user);

        return true;
    }
}
