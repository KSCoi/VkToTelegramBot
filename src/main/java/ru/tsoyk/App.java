package ru.tsoyk;

//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import ru.tsoyk.config.BotConfiguration;
import ru.tsoyk.config.TelegramBotInitializer;
import ru.tsoyk.config.VkConfig;

// Аннотация, которая объединяет в себя @Configuration, @EnableAutoConfiguration, @ComponentScan
    @SpringBootApplication()
    //@AutoConfigurationPackage
    public class App extends SpringBootServletInitializer {
        public static void main(String[] args) {
            new SpringApplicationBuilder(App.class)
                   .run(args);

            //SpringApplication.run(App.class,args);
        }
    }

