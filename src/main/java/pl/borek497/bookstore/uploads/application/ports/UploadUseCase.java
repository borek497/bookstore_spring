package pl.borek497.bookstore.uploads.application.ports;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.borek497.bookstore.uploads.domain.Upload;

import java.util.Optional;

public interface UploadUseCase {

    Upload save(SaveUploadCommand saveUploadCommand);
    Optional<Upload> getById(String id);

    void removeById(String id);

    @AllArgsConstructor
    @Data
    class SaveUploadCommand {
        String fileName;
        byte[] file;
        String contentType;
    }
}