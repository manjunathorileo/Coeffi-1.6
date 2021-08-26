package com.dfq.coeffi.i18n;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
@Transactional
public interface LanguageRepository extends JpaRepository<Language,Long> {


}
