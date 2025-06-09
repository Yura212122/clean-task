package academy.prog.julia.configurations;

import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

/**
 * Configuration class for setting up Spring Security with custom security filters, authentication management, and
 * session handling (without JWT).
 * This class defines the security filter chain, authentication manager, session ID resolver, and other essential beans.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@EnableCORS
public class WebSecurityConfiguration {

    private final EncodersConfiguration passwordEncoder;

    private final CookieClearFilter cookieClearFilter;

    /**
     * Constructor that injects dependencies required for configuring security, including password encoder and
     * cookie clearing filter.
     *
     * @param passwordEncoder   {@link EncodersConfiguration} for encoding passwords.
     * @param cookieClearFilter {@link CookieClearFilter} for clearing cookies.
     */
    public WebSecurityConfiguration(EncodersConfiguration passwordEncoder, CookieClearFilter cookieClearFilter) {
        this.passwordEncoder = passwordEncoder;
        this.cookieClearFilter = cookieClearFilter;
    }

    /**
     * Configures the security filter chain, including CORS settings, permitted URLs, and session management.
     *
     * @param http {@link HttpSecurity} to configure security settings.
     * @return Configured {@link SecurityFilterChain}.
     * @throws Exception in case of configuration issues.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new CookieClearFilter(), ChannelProcessingFilter.class)
                .addFilterBefore(cookieClearFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/api/invite",
                                "/api/register",
                                "/api/formLogin",
                                "/api/logout",
                                "/api/user/**",
                                "/api/courses/**",
                                "/api/user-courses/**",
                                "/api/lessons/**",
                                "/api/tasks/**",
                                "/api/tests/**",
                                "/api/teacher/**",
                                "/api/certificate/**",
                                "/api/certificate/user/**",
                                "/api/allEmployeesList",
                                "/api/studentslist",
                                "/sse/**",
                                "/css/**",
                                "/js/**",
                                "/api/admin/**"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/v3/api-docs/**",
                                "/proxy/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .securityContext((securityContext) -> securityContext
                        .requireExplicitSave(true)
                )
                .csrf().disable()
                .headers().frameOptions().disable();

        return http.build();
    }

    /**
     * Configures the authentication manager with a custom {@link DaoAuthenticationProvider} that uses a provided {@link UserDetailsService} and password encoder.
     *
     * @param userDetailsService Service to load user-specific data.
     * @return Configured {@link AuthenticationManager}.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService
    ) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder.userPasswordEncoder());

        return new ProviderManager(authenticationProvider);
    }

    /**
     * Configures a custom {@link HttpSessionIdResolver} that uses cookies to manage session IDs.
     * The cookies are configured to clear old session IDs and set new ones as needed.
     *
     * @return Configured {@link HttpSessionIdResolver}.
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        CookieHttpSessionIdResolver sessionIdResolver = new CookieHttpSessionIdResolver();
        sessionIdResolver.setCookieSerializer(new CookieSerializer() {
            @Override
            public void writeCookieValue(CookieValue cookieValue) {
                HttpServletResponse response = cookieValue.getResponse();
                HttpServletRequest request = cookieValue.getRequest();

                clearCookie(request, response);

                String sessionId = cookieValue.getCookieValue();
                // Get session object and save it in cookies
                Cookie cookie = new Cookie("JSESSIONID", sessionId);

                cookie.setMaxAge(-1); // Session cookie lasts until the browser is closed
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            /**
             * Retrieves session ID values from cookies present in the request.
             * Specifically looks for cookies with the name "JSESSIONID" and collects their values.
             * If multiple session cookies are present, their values are added to the list.
             *
             * @param request the HttpServletRequest from which to retrieve cookies
             * @return a list of session ID values, used for managing session state
             */
            @Override
            public List<String> readCookieValues(HttpServletRequest request) {
                Cookie[] cookies = request.getCookies();
                List<String> sessionIds = new ArrayList<>();

                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("JSESSIONID".equals(cookie.getName())) {
                            sessionIds.add(cookie.getValue());
                        }
                    }
                }

                return sessionIds;
            }
        });

        return sessionIdResolver;
    }

    /**
     * Utility method to clear the JSESSIONID cookie from the client's browser.
     *
     * @param request  {@link HttpServletRequest} containing client request data.
     * @param response {@link HttpServletResponse} containing server response data.
     */
    public void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    cookie.setMaxAge(0); //clear cookie
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break; //stop on first found cookie
                }
            }
        }
    }

    /**
     * Configures a {@link FilterRegistrationBean} for the custom {@link CookieClearFilter}.
     * The filter is disabled by default and can be enabled as needed.
     *
     * @return Configured {@link FilterRegistrationBean}.
     */
    @Bean
    public FilterRegistrationBean<CookieClearFilter> cookieClearFilterRegistrationBean() {
        FilterRegistrationBean<CookieClearFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(cookieClearFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    /**
     * Configures a {@link MappingJackson2HttpMessageConverter} bean to handle JSON conversion in HTTP messages.
     *
     * @return Configured {@link MappingJackson2HttpMessageConverter}.
     */
    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

}