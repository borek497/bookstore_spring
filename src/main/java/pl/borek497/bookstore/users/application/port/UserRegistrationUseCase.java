package pl.borek497.bookstore.users.application.port;

import pl.borek497.bookstore.commons.Either;
import pl.borek497.bookstore.user.domain.UserEntity;

public interface UserRegistrationUseCase {
    RegisterResponse register(String username, String password);

    class RegisterResponse extends Either<String, UserEntity> {

        public RegisterResponse(boolean success, String left, UserEntity right) {
            super(success, left, right);
        }

        public static RegisterResponse success(UserEntity right) {
            return new RegisterResponse(true, null, right);
        }

        public static RegisterResponse failure(String left) {
            return new RegisterResponse(false, left, null);
        }
    }
}