package com.lianshidai.bcebe.Service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lianshidai.bcebe.Pojo.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService extends IService<User> {
}
