package pl.borek497.bookstore.users.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.borek497.bookstore.users.application.port.UserRegistrationUseCase;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
class UsersController {

    private final UserRegistrationUseCase userRegistrationUseCase;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        return userRegistrationUseCase.register(command.getUsername(), command.getPassword())
                .handle(
                        entity -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    @Data
    static class RegisterCommand {

        @Email
        String username;

        @Size(min = 3, max = 50)
        String password;
    }
}