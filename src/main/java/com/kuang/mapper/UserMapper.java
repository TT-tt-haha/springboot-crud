package com.kuang.mapper;

import com.kuang.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper {

    List<User> getUsers();
//    List<User> getUsersByLimit(Map<String,Integer> map);
    User getUser(Integer id);

    int deleteUser(Integer id);
    int addUser(User user);
    int updateUser(User user);

    List<User> searchUser(String firstname);
}
