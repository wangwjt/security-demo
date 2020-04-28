package com.practise.security.controller;

import com.practise.security.entity.User;
import com.practise.security.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangjiantao
 * @date 2020/4/28 9:47
 */
@RestController
public class UserInfoController {
    @GetMapping("/user")
    public UserVO getAllUserInfo() {
        UserVO userVO = new UserVO();
        List<User> userVOList = Arrays.asList(new User("123", "abc"),
                new User("456", "def"),
                new User("789", "qwr"));
        userVO.setUserList(userVOList);
        return userVO;
    }
}
