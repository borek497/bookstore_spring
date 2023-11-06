package pl.borek497.bookstore.uploads.application;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.uploads.application.ports.UploadUseCase;
import pl.borek497.bookstore.uploads.db.UploadJpaRepository;
import pl.borek497.bookstore.uploads.domain.Upload;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
class UploadService implements UploadUseCase {
    private final UploadJpaRepository uploadJpaRepository;

    @Override
    public Upload save(SaveUploadCommand saveUploadCommand) {

        Upload upload = new Upload(
                saveUploadCommand.getFileName(),
                saveUploadCommand.getContentType(),
                saveUploadCommand.getFile()
        );

        uploadJpaRepository.save(upload);
        System.out.println("Upload saved: " + upload.getFileName() + " with id: " + upload.getId());
        return upload;
    }

    @Override
    public Optional<Upload> getById(Long id) {
        return uploadJpaRepository.findById(id);
    }

    @Override
    public void removeById(Long id) {
        uploadJpaRepository.deleteById(id);
    }
}