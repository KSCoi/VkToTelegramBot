package ru.tsoyk.tg.models;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BotUser {

    private Long id;
    private String password;
    private Role role = Role.USER;
    //private Set<EventTypes> currentEvents;
}
