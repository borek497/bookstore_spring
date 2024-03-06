package pl.borek497.bookstore.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.borek497.bookstore.order.domain.Recipient;

import java.util.Optional;

public interface RecipientJpaRepository extends JpaRepository<Recipient, Long> {
    Optional<Recipient> findByEmailIgnoreCase(String email);
}
