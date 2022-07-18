package software.plusminus.login.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class CookieUtilTest {

    @Mock
    private HttpServletResponse mockHttpServletResponse;
    @Captor
    private ArgumentCaptor<Cookie> captorCookie;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreate() {
        CookieUtil.create(mockHttpServletResponse, "CookieName", "CookieValue",
                "example.com");

        verify(mockHttpServletResponse).addCookie(captorCookie.capture());
        Cookie actual = captorCookie.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("CookieName");
        assertThat(actual.getValue()).isEqualTo("CookieValue");
        //assertThat(actual.getSecure()).isTrue();
        assertThat(actual.isHttpOnly()).isTrue();
        assertThat(actual.getMaxAge()).isEqualTo(60 * 60 * 24 * 360);
        //assertThat(actual.getDomain()).isEqualTo("example.com");
        assertThat(actual.getPath()).isEqualTo("/");
    }
}