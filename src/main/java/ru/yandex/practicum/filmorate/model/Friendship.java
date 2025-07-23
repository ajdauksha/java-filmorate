package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private int id;
    private int userId;
    private int friendId;
}
