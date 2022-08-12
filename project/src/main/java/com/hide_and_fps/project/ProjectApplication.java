package com.hide_and_fps.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;


import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import com.hide_and_fps.project.entity.Customer;
import com.hide_and_fps.project.repository.testRepository;

import java.time.Duration;
import java.util.Arrays;
/*
 * 이 클래스의 패키지가 최상위 루트로 지정된다.
 */

@ServletComponentScan

//매핑할 패키지 경로를 지정한다. 해당 클래스 외의 패키지 경로를 매핑하고 싶을 때(Autowired bean이 안잡힐 때)
@ComponentScan(basePackages = {
								"com.hide_and_fps.project.*"
							  })
@SpringBootApplication
public class ProjectApplication {

	private static final Logger log = LoggerFactory.getLogger(ProjectApplication.class);

    public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
		/*
		//System.setProperty("spring.profiles.active", "local");
        SpringApplication application = new SpringApplication(ProjectApplication.class);
        application.setWebApplicationType(WebApplicationType.REACTIVE); 
        // starter-web은 SERVLET으로 자동으로 선택한다. 이것을 강제로 REACTIVE로 바꾼다.
        application.run(args);
        */
	}
	
    /**
     * Spring Data R2dbc 는 연결될 때 데이터베이스에서 SQL 스크립트를 실행할 수 있도록 ConnectionFactoryInitializer를 제공합니다 . Spring Boot 애플리케이션에서는 자동으로 구성됩니다. 응용 프로그램이 시작되면 스키마를 스캔합니다.
     **/
    /*
    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));

        return initializer;
    }
    */
    /**
     * CommandLineRunner는 Bean이 SpringApplication에 포함될 때 실행되어야 함을 나타내는 데 사용되는 인터페이스입니다. 스프링 부트 응용 프로그램에는 CommandLineRunner를 구현하는 여러 개의 빈이 있을 수 있습니다. 이것들은 @Order와 함께 주문할 수 있습니다.
     **/
    /*
    @Bean
    public CommandLineRunner demo(testRepository repository) {

        return (args) -> {
            // save a few customers
            repository.saveAll(Arrays.asList(new Customer("Jack", "Bauer"),
                new Customer("Chloe", "O'Brian"),
                new Customer("Kim", "Bauer"),
                new Customer("David", "Palmer"),
                new Customer("Michelle", "Dessler")))
                .blockLast(Duration.ofSeconds(10));

            // fetch all customers
            log.info("Customers found with findAll():");
            log.info("-------------------------------");
            repository.findAll().doOnNext(customer -> {
                log.info("customer All >>>" + customer.toString());
            }).blockLast(Duration.ofSeconds(10));

            log.info("");

            // fetch an individual customer by ID
			repository.findById(1L).doOnNext(customer -> {
				log.info("Customer found with findById(1L):");
				log.info("--------------------------------");
				log.info("customer ById 1L?>>>" + customer.toString());
				log.info("");
			}).block(Duration.ofSeconds(10));


            // fetch customers by last name
            log.info("Customer found with findByLastName('Bauer'):");
            log.info("--------------------------------------------");
            repository.findByLastName("Bauer").doOnNext(bauer -> {
                log.info("bauer >>> " + bauer.toString());
            }).blockLast(Duration.ofSeconds(10));;
            log.info("");
        };
    }
    */
}