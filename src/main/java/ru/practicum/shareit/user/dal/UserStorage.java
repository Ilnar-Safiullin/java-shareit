package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.model.User;

public interface UserStorage {

    User add(User user);

    User updateUser(User user);

    void deleteUser(long userId);

    User getUserById(long userId);
}
