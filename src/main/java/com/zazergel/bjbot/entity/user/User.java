package com.zazergel.bjbot.entity.user;

import com.zazergel.bjbot.entity.user.enums.Action;
import com.zazergel.bjbot.entity.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @Column(name = "id")
    Long chatId;

    @Enumerated(EnumType.STRING)
    Role role;

    @Enumerated(EnumType.STRING)
    Action action;

    @Column(name = "score")
    Long score;

    @Column(name = "registered_at", nullable = false)
    LocalDateTime registeredAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_stat_id")
    UserGameStat gameStat;
}
