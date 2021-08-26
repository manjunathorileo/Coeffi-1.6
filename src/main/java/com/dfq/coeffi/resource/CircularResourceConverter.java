package com.dfq.coeffi.resource;

import com.dfq.coeffi.entity.communication.Circular;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class CircularResourceConverter {
	public Circular toEntity(CircularResource circularResource) {
		Circular circular = new Circular();
		Date date = new Date();
		//circular.setCircularMessage(circularResource.getMessage());
		circular.setCircularLevel(circularResource.getCircularLevel());
		circular.setDate(date);
		return circular;
	}
}
