package com.nttdata.urlshortener.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.urlshortener.model.Link;


@Repository
public interface ILinkRepository extends MongoRepository<Link, String>
{
	public Link findByShortUrl(String shortUrl);
	public Link findByLongUrl(String shortUrl);
}
