package vn.gpay.gsmart.core;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
//@EnableAuthorizationServer
//@EnableResourceServer
public class GsmartCoreApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(GsmartCoreApplication.class, args);
	}
	
	@PostConstruct
    public void init(){
      // Setting Spring Boot SetTimeZone
      TimeZone.setDefault(TimeZone.getTimeZone("GMT+0700"));
    }
}
