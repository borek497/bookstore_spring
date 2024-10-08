package pl.borek497.bookstore.uploads.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.borek497.bookstore.uploads.application.ports.UploadUseCase;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/uploads")
@AllArgsConstructor
class UploadsController {

    private final UploadUseCase uploadUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<UploadResponse> getUpload(@PathVariable Long id) {
        return uploadUseCase.getById(id)
                .map(uploadFile -> {
                    UploadResponse response =  new UploadResponse(
                        uploadFile.getId(),
                        uploadFile.getContentType(),
                        uploadFile.getFileName(),
                        uploadFile.getCreatedAt()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getUploadFile(@PathVariable Long id) {
        return uploadUseCase.getById(id)
                .map(uploadFile -> {
                    String contentDisposition = "attachment; filename=\"" + uploadFile.getContentType() + "\"";
                    byte[] bytes = uploadFile.getFile();
                    Resource resource = new ByteArrayResource(bytes);
                    return ResponseEntity
                            .ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                            .contentType(MediaType.parseMediaType(uploadFile.getContentType()))
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Value
    @AllArgsConstructor
    static class UploadResponse {
        Long id;
        String contentType;
        String fileName;
        LocalDateTime createdAt;
    }
}