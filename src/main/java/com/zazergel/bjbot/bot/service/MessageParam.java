package com.zazergel.bjbot.bot.service;

import com.zazergel.bjbot.bot.Bot;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageParam {
    long chatId;
    int messageId;
    CallbackQuery callbackQuery;
    Bot bot;
}
