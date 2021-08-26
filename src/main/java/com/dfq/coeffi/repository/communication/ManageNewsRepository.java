package com.dfq.coeffi.repository.communication;

import com.dfq.coeffi.entity.communication.ManageNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface ManageNewsRepository extends JpaRepository<ManageNews, Long> {

	@Query("select mn from ManageNews mn where mn.postedDate=:date")
	List<ManageNews> getTodayNews(@Param("date") Date date);
	
	@Query("SELECT ni from ManageNews ni ORDER BY id DESC ")
	List<ManageNews> getNewsListByDesc();

	@Query("select ran from ManageNews ran where ran.author=:name")
	List<ManageNews> getNewsListByAuthor(@Param("name") String name);
	
	@Query("select jan from ManageNews jan where jan.venue=:place")
	List<ManageNews> getNewsByVenue(@Param("place") String place);
}
