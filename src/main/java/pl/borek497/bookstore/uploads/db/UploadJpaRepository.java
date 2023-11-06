package pl.borek497.bookstore.uploads.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.borek497.bookstore.uploads.domain.Upload;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {
}
