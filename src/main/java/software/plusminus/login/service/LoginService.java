package software.plusminus.login.service;

import org.springframework.stereotype.Service;
import software.plusminus.security.Loginer;
import software.plusminus.security.Security;

@Service
public class LoginService {

    private Loginer loginer;

    public Security login(String username, String password) {
        return loginer.login(username, password);
    }
}
