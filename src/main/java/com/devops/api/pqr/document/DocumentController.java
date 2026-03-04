package com.devops.api.pqr.document;

import com.devops.api.pqr.document.dto.DocumentResponseDto;
import com.devops.api.pqr.document.entity.Document;
import com.devops.api.pqr.document.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final DocumentMapper documentMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDto> upload(@RequestPart("file") MultipartFile file) throws IOException {

        Document saved = documentService.saveDocument(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentMapper.toResponseDto(saved));
    }

}