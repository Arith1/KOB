package com.kob.controller.user;

import com.kob.mapper.UserMapper;
import com.kob.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @GetMapping("/")
    public List<User> getAll(){
        return  userMapper.selectList(null);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id){
        return userMapper.selectById(id);
    }

    @PostMapping
    public boolean addUser(@RequestBody User user){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insert(user) > 0 ;
    }

    @DeleteMapping("/{id}")
    public boolean deleteUserById(@PathVariable Integer id){
        return userMapper.deleteById(id) > 0;
    }
}
