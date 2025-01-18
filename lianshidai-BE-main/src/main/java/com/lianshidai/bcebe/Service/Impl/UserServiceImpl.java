package com.lianshidai.bcebe.Service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianshidai.bcebe.Pojo.User;
import com.lianshidai.bcebe.Mapper.UserMapper;
import com.lianshidai.bcebe.Service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
