package anhvt.com.springsecurityclient.repository;

import anhvt.com.springsecurityclient.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    public PasswordResetToken findByToken(String token);
}
