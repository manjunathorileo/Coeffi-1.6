package com.dfq.coeffi.service;

import com.dfq.coeffi.entity.user.LoggedUser;
import com.dfq.coeffi.entity.user.Role;
import com.dfq.coeffi.entity.user.User;

import java.util.List;
import java.util.Optional;

/**
 * @author H Kapil Kumar
 */
public interface UserService {
    List<User> getUsers();

    User getUser(long id);

    List<Role> getRoles();

    Role getRole(long id);

    void isUserExists(String email);

    User saveUser(User user);

    LoggedUser saveLoggedUser(LoggedUser loggedUser);

    Optional<User> sendPasswordToEmail(String userEmail);

}