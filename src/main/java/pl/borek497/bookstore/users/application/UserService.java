package pl.borek497.bookstore.users.application;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.borek497.bookstore.user.db.UserEntityRepository;
import pl.borek497.bookstore.user.domain.UserEntity;
import pl.borek497.bookstore.users.application.port.UserRegistrationUseCase;

@Service
@AllArgsConstructor
public class UserService implements UserRegistrationUseCase {
    private final UserEntityRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public RegisterResponse register(String username, String password) {
        if (repository.findByUsernameIgnoreCase(username).isPresent()) {
            return RegisterResponse.failure("Account already exists");
        }
        UserEntity entity = new UserEntity(username, passwordEncoder.encode(password));
        return RegisterResponse.success(repository.save(entity));
    }
}