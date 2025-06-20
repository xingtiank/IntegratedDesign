package com.work.integratedDesign.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.integratedDesign.mapper.UserMapper;
import com.work.integratedDesign.pojo.User;
import com.work.integratedDesign.service.UserService;
import org.springframework.stereotype.Service;



@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public boolean userExistsByEmail(String email) {
        return lambdaQuery().eq(User::getEmail, email).exists();
    }

    @Override
    public void newUserSave(User user) {
        this.save(user);
    }
    @Override
    public void passwordReset(String email, String password){
        User user = lambdaQuery().eq(User::getEmail, email).one();
        if (user == null) {
            return;
        }
        user.setPassword(password);
        this.updateById(user);
    }
}
