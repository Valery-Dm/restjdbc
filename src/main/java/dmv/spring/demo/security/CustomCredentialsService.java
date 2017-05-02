package dmv.spring.demo.security;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import dmv.spring.demo.model.entity.User;

/**
 * {@link CredentialsService} implementation
 * @author dmv
 */
@Service
public class CustomCredentialsService implements CredentialsService {

	/* For password auto generation */
	@Autowired
	private SecureRandom random;

	/* For password hashing */
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public boolean isProtected(User user) {
		return user != null && isProtected(user.getId());
	}

	@Override
	public boolean isProtected(Long userId) {
		return userId != null && userId == adminId;
	}

	@Override
	public String getHashedPassword(User user) {
		Assert.notNull(user, "user can't be null");
		String password = user.getPassword();
		if (password == null || password.length() == 0) {
			password = generatePassword();
			user.setPassword(password);
		}
		return getHashedPassword(password);
	}

	private String getHashedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return new BigInteger(130, random).toString(32);
	}
}
