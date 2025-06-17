package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.model.User;

public interface UserStorage {


    public User add(User user);

    public User updateUser(User user);

    public void deleteUser(long userId);

    public User getUserById(long userId);
}
