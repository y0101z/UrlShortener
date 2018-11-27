package com.nttdata.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.nttdata"})
@EnableScheduling
public class UrlShortenerApplication
{	
	public static void main(String[] args)
	{
		SpringApplication.run(UrlShortenerApplication.class, args);
	}
}
