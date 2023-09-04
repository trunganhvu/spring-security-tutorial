package anhvt.com.springsecurityclient.event.listener;

import anhvt.com.springsecurityclient.entity.User;
import anhvt.com.springsecurityclient.event.RegistrationCompleteEvent;
import anhvt.com.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements
        ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // Create token the verfication token for the user
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);

        String url =
                event.getApplicationUrl()
                + "/verifyRegistration?token=" + token;

        log.info("Click the link to verify your account: {}", url);
    }
}
