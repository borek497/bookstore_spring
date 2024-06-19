package pl.borek497.bookstore.security;

import lombok.Data;

@Data
class LoginCommand {
    private String username;
    private String password;
}