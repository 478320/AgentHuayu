package org.huayu;

import org.huayu.domain.user.service.UserDomainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 */
@SpringBootTest
public class UserDomainServiceTest {

    @Autowired
    UserDomainService userDomainService;

    @Test
    public void registerUser(){

        String phone = "";
        String email = "test@qq.com";
        String password = "123456";

        userDomainService.register(email, phone, password);


    }
}
