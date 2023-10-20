package com.kob.service;

import com.kob.mapper.UserMapper;
import com.kob.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface UserService {


    public List<User> getAll();

    public User getUserById(Integer id);

    public boolean addUser(User user);

    public boolean deleteUserById(Integer id);
}
