package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addUserToFriends(@PathVariable int userId, @PathVariable int friendId) {
        return userService.addUserToFriends(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFromFriends(@PathVariable int userId, @PathVariable int friendId) {
        return userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable int userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherUserId) {
        return userService.getCommonFriends(userId, otherUserId);
    }

}
