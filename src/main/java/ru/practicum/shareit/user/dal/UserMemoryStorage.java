package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserMemoryStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long counterId = 0L;


    @Override
    public User add(User newUser) {
        User user = new User();
        user.setId(++counterId);
        user.setName(newUser.getName());
        validateEmail(newUser);
        user.setEmail(newUser.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
        validateEmail(updateUser);
        return users.put(updateUser.getId(), updateUser);
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    @Override
    public User getUserById(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("User с таким id не найден");
        }
        return user;
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
