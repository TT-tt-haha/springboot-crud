package com.kuang;

import com.kuang.mapper.UserMapper;
import com.kuang.pojo.User;
//import com.kuang.service.UserService;
//import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
class SpringbootCrudApplicationTests {

    @Autowired
    private UserMapper userMapper;


}
