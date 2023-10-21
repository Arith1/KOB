package com.kob.service.impl;

import com.kob.pojo.User;
import com.kob.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public User getUserById(Integer id) {
        return null;
    }

    @Override
    public boolean addUser(User user) {
        return false;
    }

    @Override
    public boolean deleteUserById(Integer id) {
        return false;
    }
}
