package academy.prog.julia.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Configuration class that sets up Spring MVC for the application.
 * This includes configuring CORS, view controllers, and providing a prototype-scoped {@link RestTemplate} bean.
 */
@EnableWebMvc
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    /**
     * The allowed origin for Cross-Origin Resource Sharing (CORS) requests,
     * which is injected from the application's properties file.
     */
    @Value("${allowed_cross_origin}")
    private String allowedOrigin;

    /**
     * Configures CORS mappings to allow requests from the specified origin.
     * This is especially important for enabling cross-origin communication between the frontend and backend.
     *
     * @return a {@link WebMvcConfigurer} instance with CORS configuration.
     */
    @Bean
    public WebMvcConfigurer configure() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry reg) {
                reg.addMapping("/**").allowedOrigins(allowedOrigin);
                reg.addMapping("/sse").allowedOrigins(allowedOrigin);
            }
        };
    }

    /**
     * Provides a {@link RestTemplate} bean that is scoped as prototype, meaning a new instance
     * is created each time it is requested. This is useful for scenarios where thread safety is a concern.
     *
     * @return a new instance of {@link RestTemplate}.
     */
    @Bean
    @Scope("prototype")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Adds view controllers for handling specific URLs without the need for a dedicated controller.
     * This code was initially required to display the default login page.
     * In this case, it maps the URL "/login" to the view named "login".
     *
     * @param registry the {@link ViewControllerRegistry} used to register view controllers.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }
}
