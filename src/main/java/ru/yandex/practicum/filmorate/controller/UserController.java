package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private static int id = 1;

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        user.setId(id);
        users.put(user.getId(), validateUserName(user));
        id++;
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь не найден");
        }
        users.put(user.getId(), validateUserName(user));
        log.info("Обновлен пользователь {}", user);
        return user;
    }

    private User validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не указано. Заполняется логином.");
            user.setName(user.getLogin());
        }
        return user;
    }


}
