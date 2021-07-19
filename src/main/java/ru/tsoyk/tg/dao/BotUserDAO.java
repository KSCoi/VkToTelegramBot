package ru.tsoyk.tg.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.tsoyk.tg.models.BotUser;



import java.util.List;
import java.util.Set;

@Repository
public class BotUserDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BotUserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable
    public List<BotUser> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM bot_users",
                new BeanPropertyRowMapper<>(BotUser.class)
        );
    }

    @Nullable
    public BotUser getUserByChatId(Long chatId) {
        return jdbcTemplate.query("SELECT * FROM bot_users WHERE id=?", new BeanPropertyRowMapper<>(BotUser.class),
                chatId).stream().findAny().orElse(null);
    }

    public void save(BotUser user) {
        if (user.getPassword().contains("secret"))
            jdbcTemplate.update("INSERT INTO bot_users VALUES(?,?,'ADMIN')", user.getId(), user.getPassword());
        else jdbcTemplate.update("INSERT INTO bot_users VALUES(?,?,'USER')", user.getId(), user.getPassword());
    }

    public void update(BotUser user) {
        jdbcTemplate.update("UPDATE bot_users SET password=?,'USER'", user.getPassword());
    }
}
