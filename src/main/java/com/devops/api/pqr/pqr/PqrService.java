package com.devops.api.pqr.pqr;

import com.devops.api.pqr.document.entity.Document;
import com.devops.api.pqr.document.DocumentRepository;
import com.devops.api.pqr.pqr.dto.CreatePqrDto;
import com.devops.api.pqr.pqr.entity.Pqr;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PqrService {
    private final PqrRepository pqrRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public Pqr createPqr(CreatePqrDto dto, List<MultipartFile> files) {
        Pqr pqr = Pqr.builder()
                .type(dto.getType())
                .customerEmail(dto.getCustomerEmail())
                .description(dto.getDescription())
                .build();

        Pqr savedPqr = pqrRepository.save(pqr);

        if (files != null && !files.isEmpty()) {
            files.forEach(file -> {
                String mockUrl = "https://storage.provider.com/pqrs/" + savedPqr.getId() + "/" + file.getOriginalFilename();

                Document doc = Document.builder()
                        .fileName(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .storageUrl(mockUrl)
                        .pqr(savedPqr)
                        .build();

                documentRepository.save(doc);
            });
        }

        return savedPqr;
    }
}