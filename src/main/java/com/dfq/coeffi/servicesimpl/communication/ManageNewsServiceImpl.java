package com.dfq.coeffi.servicesimpl.communication;

import com.dfq.coeffi.entity.communication.ManageNews;
import com.dfq.coeffi.repository.communication.ManageNewsRepository;
import com.dfq.coeffi.service.communication.ManageNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class ManageNewsServiceImpl implements ManageNewsService
{
	@Autowired
	private ManageNewsRepository manageresp;
	
	@Autowired
	public ManageNewsServiceImpl(ManageNewsRepository manageresp) {
		this.manageresp = manageresp;
	}

	@Override
	public void deletenews(long id) {
		manageresp.delete(id);
	}

	@Override
	public Optional<ManageNews> getNews(long id)
	{
		return ofNullable(manageresp.getOne(id));
	}

	@Override
	public List<ManageNews> listofNews() {
		return manageresp.findAll();
	}


	@Override
	public ManageNews createNews(ManageNews manage) {
		return manageresp.save(manage);
	}


	@Override
	public List<ManageNews> getTodayNews(Date date)
	{
		return manageresp.getTodayNews(date);
	}

	public List<ManageNews> getNewsListByDesc() {
		return manageresp.getNewsListByDesc();
	}

	@Override
	public List<ManageNews> getNewsListByAuthor(String name) {
		// TODO Auto-generated method stub
		return manageresp.getNewsListByAuthor(name);
	}

	@Override
	public List<ManageNews> getNewsByVenue(String place) {
		// TODO Auto-generated method stub
		return manageresp.getNewsByVenue(place);
	}

	@Override
	public List<ManageNews> findAll() {
		List<ManageNews> news = (List<ManageNews>) manageresp.findAll();

		return news;
	}	
}
