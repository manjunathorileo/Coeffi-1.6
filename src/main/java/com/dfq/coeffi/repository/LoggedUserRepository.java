package com.dfq.coeffi.repository;

import com.dfq.coeffi.entity.user.LoggedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoggedUserRepository extends JpaRepository<LoggedUser,Long> {
}
