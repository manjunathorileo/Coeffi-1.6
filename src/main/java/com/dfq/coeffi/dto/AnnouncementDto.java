/**
 * 
 */
package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class AnnouncementDto 
{
	private List <Integer> alumniId;
	
	private String message;	
}
