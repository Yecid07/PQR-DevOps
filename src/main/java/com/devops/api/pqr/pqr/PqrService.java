package com.devops.api.pqr.pqr;

import com.devops.api.pqr.document.entity.Document;
import com.devops.api.pqr.document.DocumentRepository;
import com.devops.api.pqr.pqr.dto.CreatePqrDto;
import com.devops.api.pqr.pqr.entity.Pqr;
import com.devops.api.pqr.reviewer.entity.Reviewer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Iterator;
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

    public void delete(String id) {
        Pqr reviewer = pqrRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id not found"));
        pqrRepository.delete(reviewer);
    }
    public Iterable<Pqr> getAll(){
        return pqrRepository.findAll();
    }
}