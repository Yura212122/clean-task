package academy.prog.julia.integration.configurations;

import academy.prog.julia.configurations.CookieClearFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CookieClearFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private CookieClearFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new CookieClearFilter();
    }


    @Test
    void testDoFilter_FirstRun_NoCookies() throws IOException, ServletException {
        when(request.getCookies()).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response, never()).addCookie(any(Cookie.class));
        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilter_FirstRun_WithCookies() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        Cookie[] cookies = {new Cookie("cookie1", "value1"), new Cookie("cookie2", "value2")};

        when(request.getCookies()).thenReturn(cookies);
        CookieClearFilter filter = new CookieClearFilter();

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void testIsFirstRun_NoCookies() {
        when(request.getCookies()).thenReturn(null);
        assertTrue(filter.isFirstRun(request));
    }

    @Test
    void testIsFirstRun_WithCookies() {
        Cookie cookie = new Cookie("cookie1", "value1");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        assertTrue(!filter.isFirstRun(request));
    }

    @Test
    void testClearCookies_WithCookies() {
        Cookie cookie1 = new Cookie("cookie1", "value1");
        Cookie cookie2 = new Cookie("cookie2", "value2");
        Cookie[] cookies = {cookie1, cookie2};

        when(request.getCookies()).thenReturn(cookies);

        filter.clearCookies(request, response);

        for (Cookie cookie : cookies) {
            verify(response).addCookie(argThat(c -> c.getName().equals(cookie.getName()) && c.getMaxAge() == 0));
        }
    }

    @Test
    void testInit() throws ServletException {
        FilterConfig filterConfig = mock(FilterConfig.class);
        filter.init(filterConfig);
    }

    @Test
    void testDestroy() {
        filter.destroy();
    }

}
