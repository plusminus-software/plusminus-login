package software.plusminus.login.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.security.Loginer;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {
    
    @Mock
    private Loginer loginer;
    @InjectMocks
    private LoginService service;
    
    @Test
    public void serviceCallsLoginer() {
        String username = "testUsername";
        String password = "testPassword";
        
        service.login(username, password);
        
        verify(loginer).login(username, password);
    }
}