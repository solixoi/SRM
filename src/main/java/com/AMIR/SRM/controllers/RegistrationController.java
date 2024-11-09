package com.AMIR.SRM.controllers;


import com.AMIR.SRM.domain.Role;
import com.AMIR.SRM.domain.User;
import com.AMIR.SRM.repositories.UserRepo;
import com.AMIR.SRM.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;
    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, @RequestParam("role") String role, Map<String, Object> model){

        if (!userService.addUser(user, role)){

            User userFromDb = userRepo.findByUsername(user.getUsername());
            User mailFromDb = userRepo.findByEmail(user.getEmail());
            if (userFromDb != null) {
                model.put("message", "Данный логин уже занят!");
            }
            else  if (mailFromDb != null){
                model.put("message", "Данная почта уже занята!");
            }
            else model.put("message", "Упс, что-то пошло не так!");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if (isActivated){
            model.addAttribute("message", "Учётная запись активирована!");
        } else {
            model.addAttribute("message", "Код активации не найден :(");
        }

        return "login";
    }
}
