package com.dfq.coeffi.servicesimpl;
/**
 * @Auther : H Kapil Kumar
 * @Date : May-18
 */

import com.dfq.coeffi.entity.user.LoggedUser;
import com.dfq.coeffi.entity.user.Role;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.exception.DuplicateUserException;
import com.dfq.coeffi.repository.LoggedUserRepository;
import com.dfq.coeffi.repository.RoleRepository;
import com.dfq.coeffi.repository.UserRepository;
import com.dfq.coeffi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LoggedUserRepository loggedUserRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(long id) {
        return userRepository.findOne(id);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRole(long id) {
        return roleRepository.findOne(id);
    }

    @Override
    public void isUserExists(String email) {
        Optional<User> userDb = userRepository.findUserByEmail(email);
        if (userDb.isPresent()) {
            log.warn("User already exists");
            throw new DuplicateUserException("User", "email " + email);
        }
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public LoggedUser saveLoggedUser(LoggedUser loggedUser) {
        return loggedUserRepository.save(loggedUser);
    }

    @Override
    public Optional<User> sendPasswordToEmail(String userEmail) {
        return userRepository.sendPasswordToEmail(userEmail);
    }
}