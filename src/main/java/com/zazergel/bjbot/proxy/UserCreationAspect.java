package com.zazergel.bjbot.proxy;

import com.zazergel.bjbot.entity.user.UserDetails;
import com.zazergel.bjbot.entity.user.UserGameStat;
import com.zazergel.bjbot.entity.user.enums.Action;
import com.zazergel.bjbot.entity.user.enums.Role;
import com.zazergel.bjbot.repository.DetailsRepo;
import com.zazergel.bjbot.repository.StatRepo;
import com.zazergel.bjbot.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

@Aspect
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationAspect {

    final UserRepo userRepo;
    final StatRepo statRepo;
    final DetailsRepo detailsRepo;

    @Autowired
    public UserCreationAspect(UserRepo userRepo, StatRepo statRepo, DetailsRepo detailsRepo) {
        this.userRepo = userRepo;
        this.statRepo = statRepo;
        this.detailsRepo = detailsRepo;
    }

    @Pointcut("execution(* com.zazergel.bjbot.bot.service.command.StartCommand.sendStartAnswer(..))")
    public void sendStartAnswerMethodPointcut() {
    }

    @Around("sendStartAnswerMethodPointcut()")
    public Object distributeMethodAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Message message = (Message) args[0];
        User telegramUser = message.getFrom();

        if (userRepo.existsById(telegramUser.getId())) {
            return joinPoint.proceed();
        }
        UserDetails details = UserDetails.builder()
                .firstName(telegramUser.getFirstName())
                .lastName(telegramUser.getLastName())
                .username(telegramUser.getUserName())
                .registeredAt(LocalDateTime.now())
                .build();
        detailsRepo.save(details);

        UserGameStat stat = UserGameStat.builder()
                .wins(0L)
                .bjWins(0L)
                .loses(0L)
                .draws(0L)
                .build();
        statRepo.save(stat);

        com.zazergel.bjbot.entity.user.User newUser =
                com.zazergel.bjbot.entity.user.User.builder()
                        .chatId(telegramUser.getId())
                        .action(Action.FREE)
                        .role(Role.USER)
                        .score(1000L)
                        .userDetails(details)
                        .gameStat(stat)
                        .build();
        userRepo.save(newUser);

        return joinPoint.proceed();
    }
}


