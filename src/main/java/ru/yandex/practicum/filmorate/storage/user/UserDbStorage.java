package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendshipsRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final UserRepository userRepository;
    private final FriendshipsRepository friendshipsRepository;

    @Autowired
    public UserDbStorage(UserRepository userRepository, FriendshipsRepository friendshipsRepository) {
        this.userRepository = userRepository;
        this.friendshipsRepository = friendshipsRepository;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        users.forEach(user -> {
            Set<Integer> friends = new HashSet<>(friendshipsRepository.getAllUserFriends(user.getId()).stream()
                    .map(Friendship::getFriendId).toList());
            user.setFriends(friends);
        });
        return new ArrayList<>(userRepository.getAllUsers());
    }

    @Override
    public User addUser(User user) {
        userRepository.addUser(validateUserName(user));
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateUserId(user.getId());
        userRepository.updateUser(user);
        Set<Integer> friends = user.getFriends();
        if (friends != null) {
            friendshipsRepository.deleteAllFriends(user.getId());
            friends.forEach(friendId -> friendshipsRepository.addUserToFriends(user.getId(), friendId));
        }
        log.info("Обновлен пользователь {}", user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        User user = validateUserId(id);
        List<Integer> friends = friendshipsRepository.getAllUserFriends(id).stream().map(Friendship::getFriendId).toList();
        user.setFriends(new HashSet<>(friends));
        return user;
    }

    public User validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не указано. Заполняется логином.");
            user.setName(user.getLogin());
        }
        return user;
    }

    public User validateUserId(int id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь c id %d не найден".formatted(id)));
    }

}
