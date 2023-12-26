package io.ockr.ecosystem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan
public class OckrApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OckrApiApplication.class, args);
    }
}