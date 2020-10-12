package ru.istu.b1978201.KSite;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.istu.b1978201.KSite.uploadingfiles.StorageProperties;
import ru.istu.b1978201.KSite.uploadingfiles.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class KSiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(KSiteApplication.class, args);
	}

}
