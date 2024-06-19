package pl.borek497.bookstore.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.borek497.bookstore.user.db.UserEntityRepository;

@AllArgsConstructor
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final AdminConfig config;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (config.getUsername().equalsIgnoreCase(username)) {
            return config.adminUser();
        }
        return userEntityRepository
                .findByUsernameIgnoreCase(username)
                .map(UserEntityDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}