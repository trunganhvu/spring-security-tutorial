package anhvt.com.springsecurityclient.controller;

import anhvt.com.springsecurityclient.entity.User;
import anhvt.com.springsecurityclient.entity.VerificationToken;
import anhvt.com.springsecurityclient.event.RegistrationCompleteEvent;
import anhvt.com.springsecurityclient.model.PasswordModel;
import anhvt.com.springsecurityclient.model.UserModel;
import anhvt.com.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel,
                               final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        ));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User Verifies successfully";
        }
        return "Bad user";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerifyToken(@RequestParam("token") String oldToken,
                                   HttpServletRequest request) {
        VerificationToken verificationToken =
                userService.resendVerifyToken(oldToken);

        User user = verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
        return "Verification link sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel,
                                HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokeMail(user, applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam String token, @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetToken(token);

        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);

        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password Reset Successfull";
        } else {
            return "Invalid token";
        }
    }

    @PostMapping("/changepassword")
    public String changePassword(@RequestBody PasswordModel passwordModel) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if (!userService.checkValidOldPassword(user, passwordModel.getOldPassword())) {
            return "Invalid old password";
        }
        userService.changePassword(user, passwordModel.getNewPassword());
        return "Change password successfully";
    }

    private String passwordResetTokeMail(User user, String applicationUrl, String token) {
        String url =
                applicationUrl
                        + "/savePassword?token=" + token;

        log.info("Click the link to reset password: {}", url);
        return url;
    }

    private void resendVerificationTokenMail(User user,
                                             String applicationUrl,
                                             VerificationToken verificationToken) {
        String url =
                applicationUrl
                        + "/verifyRegistration?token=" + verificationToken.getToken();

        log.info("Click the link to verify your account: {}", url);
    }


    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();

    }
}
