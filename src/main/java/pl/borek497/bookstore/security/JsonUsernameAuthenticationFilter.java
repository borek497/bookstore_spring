package pl.borek497.bookstore.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

class JsonUsernameAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Stworzyliśmy sobie taki filtr (JsonUserNameAuthenticationFilter), który w momencie gdzy przyjdzie request będzie
     * próbował przetworzyć ten request, sparsować json, który jest w środku na klasę LoginCommand, wyciągnąć z tej klasy
     * nazwę usera i password i zautentykować wobec kont użytkowników, które znajdują się w systemie, u nas w klasie SecurityConfig
     */
    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        LoginCommand loginCommand = mapper.readValue(request.getReader(), LoginCommand.class);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginCommand.getUsername(), loginCommand.getPassword());
        return this.getAuthenticationManager().authenticate(token);
    }
}