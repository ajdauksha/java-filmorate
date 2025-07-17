package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private static int id = 1;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(id);
        users.put(user.getId(), validateUserName(user));
        id++;
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateUserId(user.getId());
        users.put(user.getId(), validateUserName(user));
        log.info("Обновлен пользователь {}", user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        validateUserId(id);
        return users.get(id);
    }

    public User validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не указано. Заполняется логином.");
            user.setName(user.getLogin());
        }
        return user;
    }

    public void validateUserId(int id) {
        if (!users.containsKey(id)) {
            throw new ResourceNotFoundException("Пользователь c id %d не найден".formatted(id));
        }
    }
}
