package com.dfq.coeffi.entity.communication;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

/**
 * @Auther H Kapil Kumar on 7/3/18.
 * @Company Orileo Technologies
 */

@Setter
@Getter
@Embeddable
public class EventPhotos {
	private String url;
}