package com.work.integratedDesign.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.work.integratedDesign.pojo.User;
import com.work.integratedDesign.utility.PasswordUtil;

import java.security.NoSuchAlgorithmException;

public interface UserService extends IService<User> {
    boolean userExistsByEmail(String email);

    void newUserSave(User user);

    default boolean exists(String email, String password) throws NoSuchAlgorithmException {
//        String passwordHash = PasswordUtil.hashPassword(password);
        return this.lambdaQuery().eq(User::getEmail, email).eq(User::getPassword, password).exists();
    }

    void passwordReset(String email, String password);
}
