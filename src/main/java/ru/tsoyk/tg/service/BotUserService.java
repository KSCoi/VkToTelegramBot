package ru.tsoyk.tg.service;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.tsoyk.tg.dao.BotUserDAO;
import ru.tsoyk.tg.models.BotUser;

@Service
@AllArgsConstructor
public class BotUserService {
    private final BotUserDAO botUserDAO;
    private final JdbcTemplate jdbcTemplate;

    public void save(BotUser user) {
        botUserDAO.save(user);
    }

    public BotUser getUserByChatId(Long id) {
        return botUserDAO.getUserByChatId(id);
    }
}
