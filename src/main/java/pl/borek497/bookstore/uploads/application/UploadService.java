package pl.borek497.bookstore.uploads.application;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import pl.borek497.bookstore.uploads.application.ports.UploadUseCase;
import pl.borek497.bookstore.uploads.domain.Upload;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
class UploadService implements UploadUseCase {
    private final Map<String, Upload> storage = new ConcurrentHashMap<>();

    @Override
    public Upload save(SaveUploadCommand saveUploadCommand) {
        String newId = RandomStringUtils.randomAlphabetic(8).toLowerCase();
        Upload upload = new Upload(
                newId,
                saveUploadCommand.getFile(),
                saveUploadCommand.getContentType(),
                saveUploadCommand.getFileName(),
                LocalDateTime.now()
        );
        storage.put(upload.getId(), upload);
        System.out.println("Upload saved: " + upload.getFileName() + " with id: " + newId);
        return upload;
    }

    @Override
    public Optional<Upload> getById(String id) {
        return Optional.ofNullable(storage.get(id));
    }
}