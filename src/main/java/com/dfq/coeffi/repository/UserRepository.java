/**
 * 
 */
package com.dfq.coeffi.repository;

import com.dfq.coeffi.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


/**
 * @author H Kapil Kumar
 *
 */
public interface UserRepository extends JpaRepository<User, Long>
{
	@Query("select u from User u join fetch u.roles r" +
            " where u.email=:email and u.active=true")
	public Optional<User> findUserByEmail(@Param("email") String email);

	@Query("select u from User u where u.email=:userEmail and u.active=true")
	Optional<User> sendPasswordToEmail(@Param("userEmail") String userEmail);
}