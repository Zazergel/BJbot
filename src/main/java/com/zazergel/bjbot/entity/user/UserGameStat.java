package com.zazergel.bjbot.entity.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_game_stat")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserGameStat {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "wins")
    Long wins;
    @Column(name = "loses")
    Long loses;
    @Column(name = "bjwins")
    Long bjWins;
    @Column(name = "draws")
    Long draws;
    @Column(name = "last_game")
    LocalDateTime lastGame;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
}
