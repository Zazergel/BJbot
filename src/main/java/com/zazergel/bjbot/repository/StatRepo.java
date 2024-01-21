package com.zazergel.bjbot.repository;

import com.zazergel.bjbot.entity.user.UserGameStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StatRepo extends JpaRepository<UserGameStat, UUID> {

}
