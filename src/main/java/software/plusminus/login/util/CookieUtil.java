package software.plusminus.login.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@UtilityClass
public class CookieUtil {

    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 360;

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public void create(HttpServletResponse httpServletResponse,
                       String name,
                       String value,
                       String domain) {

        Cookie cookie = new Cookie(name, value);
        if (!domain.equals("localhost") && !domain.equals("127.0.0.1")) {
            cookie.setSecure(true);
        }
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        //cookie.setDomain(domain);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }

    public void clear(HttpServletResponse httpServletResponse,
                      String name) {

        Cookie cookie = new Cookie(name, null);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
        //TODO check clear
    }

    public String getValue(HttpServletRequest httpServletRequest,
                           String name) {

        Cookie cookie = WebUtils.getCookie(httpServletRequest, name);
        return cookie != null ? cookie.getValue() : null;
    }
}
