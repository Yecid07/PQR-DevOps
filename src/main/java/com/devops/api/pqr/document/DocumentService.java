package com.devops.api.pqr.document;

import com.devops.api.pqr.document.entity.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

    public Document saveDocument(MultipartFile file) throws IOException {
        String mockStorageUrl = "https://storage.provider.com/buckets/pqr-files/" + file.getOriginalFilename();

        Document document = Document.builder()
                .fileName(file.getOriginalFilename())
                .storageUrl(mockStorageUrl)
                .build();

        return documentRepository.save(document);
    }

}