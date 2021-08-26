package com.dfq.coeffi.sam.privileges;

import com.dfq.coeffi.sam.module.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PrivilegesRepository extends JpaRepository<Privileges, Long> {

    List<Privileges> findByModule(Module module);
}