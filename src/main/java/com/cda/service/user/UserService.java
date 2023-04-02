package com.cda.service.user;

import com.cda.model.User;
import com.cda.viewmodel.UserViewModel;

import java.util.List;

public interface UserService {
    User getOrCreateUser(UserViewModel model) throws Exception;

    List<User> getUsers();

    User getUser(long id) throws Exception;

    User createUser(UserViewModel model) throws Exception;

    void deleteUser(Long userId) throws Exception;
}
