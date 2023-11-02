package pl.borek497.bookstore.uploads.application.ports;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.borek497.bookstore.uploads.domain.Upload;

public interface UploadUseCase {

    Upload save(SaveUploadCommand saveUploadCommand);

    @AllArgsConstructor
    @Data
    class SaveUploadCommand {
        String fileName;
        byte[] file;
        String contentType;
    }
}