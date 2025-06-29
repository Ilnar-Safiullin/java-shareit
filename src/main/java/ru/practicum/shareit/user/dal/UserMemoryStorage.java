package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserMemoryStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long counterId = 0L;


    @Override
    public User add(User user) {
        validateEmail(user);
        user.setId(++counterId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateEmail(user);
        return users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    @Override
    public User getUserById(long userId) {
        Optional<User> user = Optional.ofNullable(users.get(userId));

        if (user.isEmpty()) {
            throw new NotFoundException("User с таким id не найден");
        }

        return user.get();
    }

    private void validateEmail(User newUser) {
        boolean emailExists = users.values().stream()
                .filter(user -> !user.getId().equals(newUser.getId()))
                .anyMatch(user -> user.getEmail().equals(newUser.getEmail()));

        if (emailExists) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
    }
}
