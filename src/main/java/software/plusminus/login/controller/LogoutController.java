package software.plusminus.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import software.plusminus.authentication.service.Authenticator;
import software.plusminus.login.util.CookieUtil;

import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {
    
    private List<Authenticator> authenticators;
    
    @PostMapping(path = "/logout", produces = "text/html")
    public String logout(HttpServletResponse response) {
        authenticators.stream()
                .map(a -> a.tokenPlace().getCookiesKey())
                .filter(Objects::nonNull)
                .forEach(cookieKey -> CookieUtil.clear(response, cookieKey));
        return "redirect:/";
    }
}
