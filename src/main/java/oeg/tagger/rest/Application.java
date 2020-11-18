package oeg.tagger.rest;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.web.support.SpringBootServletInitializer;

 //ONLY IF WAR IS DESIRED
import org.springframework.context.ApplicationContext;
import org.fusesource.jansi.AnsiConsole;

/**
 * Main application
 * @author vroddon
 */
@SpringBootApplication
//public class Application extends SpringBootServletInitializer {  //ONLY IF WAR IS DESIRED
public class Application {
    
    public static void main(String[] args) {
        System.out.println("Welcome!");
        AnsiConsole.systemInstall();        
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        System.out.println("Let's inspect the beans provided by Spring Boot:");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }
    /* ONLY IF WAR IS DESIRED*/
	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}    
        
        
    
}
