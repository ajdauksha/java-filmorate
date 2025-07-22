package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserRepository.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class UserRepositoryTests {

    private final UserRepository userRepository;
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
        User addedUser = userRepository.addUser(testUser);
        Optional<User> userOptional = userRepository.getUserById(addedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("id", addedUser.getId())
                        .hasFieldOrPropertyWithValue("email", "test@example.com")
                        .hasFieldOrPropertyWithValue("login", "testLogin"));
    }

    @Test
    public void testGetUserById_NotFound() {
        Optional<User> userOptional = userRepository.getUserById(999);
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void testGetAllUsers() {
        User user1 = userRepository.addUser(testUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setLogin("anotherLogin");
        anotherUser.setName("Another Name");
        anotherUser.setBirthday(LocalDate.of(1995, 5, 5));
        User user2 = userRepository.addUser(anotherUser);

        List<User> users = userRepository.getAllUsers();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    public void testAddUser() {
        User addedUser = userRepository.addUser(testUser);

        assertThat(addedUser)
                .hasFieldOrPropertyWithValue("email", "test@example.com")
                .hasFieldOrPropertyWithValue("login", "testLogin")
                .hasFieldOrPropertyWithValue("name", "Test Name")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));

        assertThat(addedUser.getId()).isPositive();
    }

    @Test
    public void testUpdateUser() {
        User addedUser = userRepository.addUser(testUser);

        addedUser.setEmail("updated@example.com");
        addedUser.setLogin("updatedLogin");
        addedUser.setName("Updated Name");
        addedUser.setBirthday(LocalDate.of(2000, 12, 31));

        User updatedUser = userRepository.updateUser(addedUser);

        assertThat(updatedUser)
                .hasFieldOrPropertyWithValue("id", addedUser.getId())
                .hasFieldOrPropertyWithValue("email", "updated@example.com")
                .hasFieldOrPropertyWithValue("login", "updatedLogin")
                .hasFieldOrPropertyWithValue("name", "Updated Name")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 12, 31));

        Optional<User> retrievedUser = userRepository.getUserById(addedUser.getId());
        assertThat(retrievedUser).hasValue(updatedUser);
    }

    @Test
    public void testAddToFriends() {
        User user1 = userRepository.addUser(testUser);

        User user2 = new User();
        user2.setEmail("friend@example.com");
        user2.setLogin("friendLogin");
        user2.setName("Friend Name");
        user2.setBirthday(LocalDate.of(1995, 5, 5));
        user2 = userRepository.addUser(user2);

        userRepository.addUserToFriends(user1.getId(), user2.getId());

        List<User> friends = userRepository.getFriends(user1.getId());
        assertThat(friends)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", user2.getId());
    }

    @Test
    public void testGetFriends_Empty() {
        User user = userRepository.addUser(testUser);
        List<User> friends = userRepository.getFriends(user.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    public void testMultipleFriends() {
        User user1 = userRepository.addUser(testUser);

        User user2 = new User();
        user2.setEmail("friend1@example.com");
        user2.setLogin("friend1Login");
        user2.setName("Friend 1 Name");
        user2.setBirthday(LocalDate.of(1995, 5, 5));
        user2 = userRepository.addUser(user2);

        User user3 = new User();
        user3.setEmail("friend2@example.com");
        user3.setLogin("friend2Login");
        user3.setName("Friend 2 Name");
        user3.setBirthday(LocalDate.of(1996, 6, 6));
        user3 = userRepository.addUser(user3);

        userRepository.addUserToFriends(user1.getId(), user2.getId());
        userRepository.addUserToFriends(user1.getId(), user3.getId());

        List<User> friends = userRepository.getFriends(user1.getId());
        assertThat(friends)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user2.getId(), user3.getId());
    }
}