package dev.reliefgrid.config;
import org.springframework.beans.factory.annotation.Value; import org.springframework.context.annotation.Configuration; import org.springframework.web.servlet.config.annotation.*; import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration public class AppConfig implements WebMvcConfigurer { @Value("${app.cors-origin}") String origin; public void addCorsMappings(CorsRegistry r){r.addMapping("/api/**").allowedOrigins(origin).allowedMethods("GET","POST","PATCH","OPTIONS").allowedHeaders("*");} }
