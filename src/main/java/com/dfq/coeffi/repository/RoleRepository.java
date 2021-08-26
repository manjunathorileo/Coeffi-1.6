/**
 * 
 */
package com.dfq.coeffi.repository;

import com.dfq.coeffi.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author H Kapil Kumar
 *
 */
public interface RoleRepository extends JpaRepository<Role, Long>
{
    List<Role> findByStatus(boolean status);

    public Optional<Role> findByName(String name);
}