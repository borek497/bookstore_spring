package pl.borek497.bookstore.user.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.borek497.bookstore.user.domain.UserEntity;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);
}