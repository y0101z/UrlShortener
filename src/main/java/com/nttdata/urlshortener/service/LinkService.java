package com.nttdata.urlshortener.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.urlshortener.model.Link;
import com.nttdata.urlshortener.repository.ILinkRepository;

@Service
public class LinkService
{
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ILinkRepository repository;

	public Link create(String shortUrl, String longUrl, Date created, Date expiration, Integer clicks)
	{
		Link newLink = new Link(shortUrl, longUrl, created, expiration, clicks);
		return repository.save(newLink);
	}

	public List<Link> getAll()
	{
		return repository.findAll();
	}

	public Link getByShortUrl(String shortUrl)
	{
		return repository.findByShortUrl(shortUrl);
	}

	public Link getByLongUrl(String longUrl)
	{
		return repository.findByLongUrl(longUrl);
	}

	public Link update(Link link)
	{
		return repository.save(link);
	}

	public void delete(String shortUrl)
	{
		Link link = repository.findByShortUrl(shortUrl);
		repository.delete(link);
	}
	
	public void deleteAll()
	{
		repository.deleteAll();
	}

}
