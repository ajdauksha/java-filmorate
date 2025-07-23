package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.FriendshipsRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.FriendshipsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        UserDbStorage.class, UserRowMapper.class,
        FriendshipsDbStorage.class, FriendshipsRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class UserDbStorageTests {

    private final UserDbStorage userDbStorage;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testLogin");
        testUser.setName("Test Name");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testFindUserById() {
        User addedUser = userDbStorage.addUser(testUser);
        User user = userDbStorage.getUserById(addedUser.getId());

        assertThat(user)
                .hasFieldOrPropertyWithValue("id", addedUser.getId())
                .hasFieldOrPropertyWithValue("email", "test@example.com")
                .hasFieldOrPropertyWithValue("login", "testLogin");
    }

    @Test
    public void testGetUserById_NotFound() {
        assertThatThrownBy(() -> userDbStorage.getUserById(999)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void testGetAllUsers() {
        User user1 = userDbStorage.addUser(testUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setLogin("anotherLogin");
        anotherUser.setName("Another Name");
        anotherUser.setBirthday(LocalDate.of(1995, 5, 5));
        User user2 = userDbStorage.addUser(anotherUser);

        List<User> users = userDbStorage.getAllUsers();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    public void testAddUser() {
        User addedUser = userDbStorage.addUser(testUser);

        assertThat(addedUser)
                .hasFieldOrPropertyWithValue("email", "test@example.com")
                .hasFieldOrPropertyWithValue("login", "testLogin")
                .hasFieldOrPropertyWithValue("name", "Test Name")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));

        assertThat(addedUser.getId()).isPositive();
    }

    @Test
    public void testUpdateUser() {
        User addedUser = userDbStorage.addUser(testUser);

        addedUser.setEmail("updated@example.com");
        addedUser.setLogin("updatedLogin");
        addedUser.setName("Updated Name");
        addedUser.setBirthday(LocalDate.of(2000, 12, 31));

        User updatedUser = userDbStorage.updateUser(addedUser);

        assertThat(updatedUser)
                .hasFieldOrPropertyWithValue("id", addedUser.getId())
                .hasFieldOrPropertyWithValue("email", "updated@example.com")
                .hasFieldOrPropertyWithValue("login", "updatedLogin")
                .hasFieldOrPropertyWithValue("name", "Updated Name")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 12, 31));

        User retrievedUser = userDbStorage.getUserById(addedUser.getId());
        assertThat(retrievedUser).isEqualTo(updatedUser);
    }

    @Test
    public void testGetFriends_Empty() {
        User user = userDbStorage.addUser(testUser);
        List<User> friends = userDbStorage.getFriends(user.getId());
        assertThat(friends).isEmpty();
    }

}