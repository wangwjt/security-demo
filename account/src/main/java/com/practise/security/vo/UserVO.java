package com.practise.security.vo;

import com.practise.security.entity.User;

import java.util.List;

/**
 * @author wangjiantao
 * @date 2020/4/28 10:00
 */
public class UserVO {
    List<User> userList;

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
