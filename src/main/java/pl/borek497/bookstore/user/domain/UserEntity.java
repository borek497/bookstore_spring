package pl.borek497.bookstore.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.borek497.bookstore.jpa.BaseEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class UserEntity extends BaseEntity {
    private String username;
    private String password;

    @CollectionTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name ="role")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = Set.of("ROLE_USER");
    }
}