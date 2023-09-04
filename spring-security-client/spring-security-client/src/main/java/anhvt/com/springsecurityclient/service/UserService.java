package anhvt.com.springsecurityclient.service;

import anhvt.com.springsecurityclient.entity.User;
import anhvt.com.springsecurityclient.entity.VerificationToken;
import anhvt.com.springsecurityclient.model.UserModel;

import java.util.Optional;

public interface UserService {
    public User registerUser(UserModel userModel);

    public void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken resendVerifyToken(String oldToken);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkValidOldPassword(User user, String oldPassword);
}
