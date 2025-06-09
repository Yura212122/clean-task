package academy.prog.julia.configurations;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * A filter that clears all cookies from the client's request during the first run of the application.
 * This filter is executed for every HTTP request and ensures that cookies are removed if no cookies
 * are present in the initial request, effectively simulating a "first run" scenario.
 */
@Component
public class CookieClearFilter implements Filter {

    /**
     * Initializes the filter. This method is called by the servlet container during filter initialization.
     *
     * @param filterConfig  the configuration object used by a servlet container to pass information to a filter
     *                      during initialization.
     * @throws ServletException if an error occurs during initialization.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /**
     * Cleans up any resources used by the filter. This method is called by the servlet container before the
     * filter is taken out of service.
     */
    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    /**
     * Performs the actual cookie clearing operation if the request is determined to be the first run.
     *
     * @param request           the ServletRequest object that contains the client's request.
     * @param response          the ServletResponse object that contains the filter's response.
     * @param chain             the FilterChain for invoking the next filter or the resource.
     * @throws IOException      if an I/O error occurs during the process.
     * @throws ServletException if a servlet error occurs during the process.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            if (isFirstRun(httpRequest)) {
                clearCookies(httpRequest, httpResponse);
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Checks if the current request is the first run by verifying the absence of cookies.
     *
     * @param request   the HttpServletRequest object that contains the client's request.
     * @return true     if there are no cookies in the request, indicating a first run; false otherwise.
     */
    public boolean isFirstRun(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return cookies == null || cookies.length == 0;
    }

    /**
     * Clears all cookies from the client's request by setting their max age to 0, effectively deleting them.
     *
     * @param request  the HttpServletRequest object that contains the client's request.
     * @param response the HttpServletResponse object to which the cookies will be added with a max age of 0.
     */
    public void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}
