package com.kob;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void testEnconder(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("miyan"));
        System.out.println(passwordEncoder.encode("user"));
        System.out.println(passwordEncoder.matches("user",passwordEncoder.encode("user")));
        System.out.println(passwordEncoder.matches("miyan","$2a$10$UaSPRfuUSOT7NfpJ/LDq3eS7uJV6NszRtayYGPo9d7Ut2j2/o3Iwe"));

    }

}
