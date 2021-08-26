package com.dfq.coeffi.service.communication;

import com.dfq.coeffi.entity.communication.ManageNews;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ManageNewsService {

	List<ManageNews> listofNews();
	ManageNews createNews(ManageNews manage);
	void deletenews(long id);
	Optional<ManageNews> getNews(long id);
	List<ManageNews> getTodayNews(Date date);
    List<ManageNews> getNewsListByDesc();
    List<ManageNews> getNewsListByAuthor(String name);
    List<ManageNews> getNewsByVenue(String place);
    List<ManageNews> findAll();
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
}
