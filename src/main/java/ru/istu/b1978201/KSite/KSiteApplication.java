package ru.istu.b1978201.KSite;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Класс запускающий приложение
 * Происходит загрузка бинов, активируются внутренние системы фреймворка spring
 */

@SpringBootApplication
public class KSiteApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(KSiteApplication.class, args);
	}

}
