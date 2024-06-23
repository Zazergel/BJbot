package com.zazergel.bjbot.repository;

import com.zazergel.bjbot.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {


    @Query("SELECT COUNT(DISTINCT u.chatId) FROM User u")
    Long getCountOfUsers();
}
