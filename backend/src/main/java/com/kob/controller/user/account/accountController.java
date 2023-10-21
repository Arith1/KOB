package com.kob.controller.user.account;

import com.kob.service.user.account.InfoService;
import com.kob.service.user.account.LoginService;
import com.kob.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/account/")
public class accountController {

    @Autowired
    private InfoService infoService;



    @GetMapping("/info/")
    public Map<String, String> getinfo() {
        return infoService.getInfo();
    }

    @Autowired
    private LoginService loginService;

    @PostMapping("/token/")
    public Map<String, String> getToken(@RequestParam Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        System.out.println(username + ' ' + password);
        return loginService.getToken(username, password);
    }

    @Autowired
    private RegisterService registerService;

    @PostMapping("register")
    public Map<String, String> register(@RequestParam Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String confirmedPassword = map.get("confirmedPassword");
        return registerService.register(username, password, confirmedPassword);
    }

}
