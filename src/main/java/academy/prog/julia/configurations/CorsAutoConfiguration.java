package academy.prog.julia.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.Filter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuration class for setting up Cross-Origin Resource Sharing (CORS) policies.
 * This class reads the CORS properties from a configuration file and applies them globally to the application.
 */
@Configuration
@EnableConfigurationProperties(CorsConfigurationProperties.class)
public class CorsAutoConfiguration {

    private final CorsConfigurationProperties properties;

    /**
     * Constructor that injects the CORS properties.
     *
     * @param properties the CORS configuration properties loaded from the application configuration file.
     */
    @Autowired
    public CorsAutoConfiguration(CorsConfigurationProperties properties) {
        this.properties = properties;
    }

    /**
     * Bean that registers the CORS filter with the highest precedence in the servlet filter chain.
     * This filter applies the CORS configuration to all incoming HTTP requests.
     *
     * @return a FilterRegistrationBean configured with the CORS filter.
     */
    @Bean
    public FilterRegistrationBean<? extends Filter> corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowedOrigins = getAllowedOrigins();
        configuration.setAllowedOrigins(allowedOrigins);

        List<String> allowedMethods = getAllowedMethods();
        configuration.setAllowCredentials(properties.getAllowCredentials());
        configuration.setAllowedMethods(allowedMethods);

        List<String> allowedHeaders = getAllowedHeaders();
        configuration.setAllowedHeaders(allowedHeaders);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    /**
     * Retrieves the list of allowed origins from the configuration properties.
     *
     * @return a list of allowed origins, or a wildcard ("*") if none are specified.
     */
    private List<String> getAllowedOrigins() {
        List<String> allowedOrigins = properties.getAllowedOrigins();
        if (!allowedOrigins.isEmpty()) {
            return allowedOrigins;
        }
        return Collections.singletonList("*");
    }

    /**
     * Retrieves the list of allowed headers from the configuration properties.
     *
     * @return a list of allowed headers, or a default list if none are specified.
     */
    private List<String> getAllowedHeaders() {
        List<String> allowedHeaders = properties.getAllowedHeaders();
        if (!allowedHeaders.isEmpty()) {
            return allowedHeaders;
        }
        return Arrays.asList(
                HttpHeaders.ORIGIN,
                HttpHeaders.REFERER,
                HttpHeaders.USER_AGENT,
                HttpHeaders.CACHE_CONTROL,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.AUTHORIZATION,
                "X-Requested-With",
                "X-Forwarded-For",
                "x-ijt");
    }

    /**
     * Retrieves the list of allowed HTTP methods from the configuration properties.
     *
     * @return a list of allowed methods, or all available methods if none are specified.
     */
    private List<String> getAllowedMethods() {
        List<String> allowedMethods = properties.getAllowedMethods();
        if (!allowedMethods.isEmpty()) {
            return allowedMethods;
        }
        return Arrays.stream(HttpMethod.values())
                .map(HttpMethod::name)
                .collect(Collectors.toList());
    }

}


