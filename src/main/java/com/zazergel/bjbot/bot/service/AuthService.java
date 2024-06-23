package com.zazergel.bjbot.bot.service;

import com.zazergel.bjbot.entity.user.User;
import com.zazergel.bjbot.entity.user.enums.Role;
import com.zazergel.bjbot.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepo userRepo;

    @Autowired
    public AuthService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public boolean checkUserRole(Long chatId) {
        return getUserById(chatId).getRole().equals(Role.ADMIN);
    }

    private User getUserById(Long chatId) {
        return userRepo.findById(chatId).orElseThrow();
    }
}

