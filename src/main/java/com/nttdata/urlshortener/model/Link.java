package com.nttdata.urlshortener.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Document("links")
public class Link implements Serializable
{
	private static final long serialVersionUID = 1L;
	@Id
	String id;
	@Field("ShortUrl")
	@Indexed(unique = true)
	private String shortUrl;
	@Field("LongUrl")
	private String longUrl;
	@Field("Created")
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date created;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Field("Expiration")
	private Date expiration;
	@Field("Clicks")
	private Integer clicks;
	
	public Link()
	{		
	}
	public Link(String id, String shortUrl, String longUrl, Date created, Date expiration, Integer clicks)
	{
		super();
		this.id = id;
		this.shortUrl = shortUrl;
		this.longUrl = longUrl;
		this.created = created;
		this.expiration = expiration;
		this.clicks = clicks;
	}
	public Link(String shortUrl, String longUrl, Date created, Date expiration, Integer clicks)
	{
		super();
		//this.id = id;
		this.shortUrl = shortUrl;
		this.longUrl = longUrl;
		this.created = created;
		this.expiration = expiration;
		this.clicks = clicks;
	}


	public String getId()
	{
		return id;
	}


	public void setId(String id)
	{
		this.id = id;
	}


	public String getShortUrl()
	{
		return shortUrl;
	}


	public void setShortUrl(String shortUrl)
	{
		this.shortUrl = shortUrl;
	}


	public String getLongUrl()
	{
		return longUrl;
	}


	public void setLongUrl(String longUrl)
	{
		this.longUrl = longUrl;
	}


	public Date getCreated()
	{
		return created;
	}


	public void setCreated(Date created)
	{
		this.created = created;
	}


	public Date getExpiration()
	{
		return expiration;
	}


	public void setExpiration(Date expiration)
	{
		this.expiration = expiration;
	}


	public Integer getClicks()
	{
		return clicks;
	}


	public void setClicks(Integer clicks)
	{
		this.clicks = clicks;
	}
}