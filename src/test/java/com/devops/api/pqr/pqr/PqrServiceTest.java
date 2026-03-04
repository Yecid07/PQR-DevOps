package com.devops.api.pqr.pqr;

import com.devops.api.pqr.document.DocumentRepository;
import com.devops.api.pqr.document.entity.Document;
import com.devops.api.pqr.pqr.dto.CreatePqrDto;
import com.devops.api.pqr.pqr.entity.Pqr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pqr Service Tests")
class PqrServiceTest {

    @Mock
    private PqrRepository pqrRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private PqrService pqrService;

    private CreatePqrDto validDto;
    private List<MultipartFile> mockFiles;

    @BeforeEach
    void setUp() {
        validDto = new CreatePqrDto("queja", "customer@test.com", "Service description");
        mockFiles = List.of(
                new MockMultipartFile("files", "doc1.pdf", "application/pdf", "data".getBytes())
        );
    }

    @Test
    @DisplayName("Should save PQR and associated documents")
    void shouldCreatePqrWithDocumentsSuccessfully() {
        // GIVEN
        String pqrId = UUID.randomUUID().toString();
        Pqr pqrToSave = Pqr.builder()
                .id(pqrId)
                .type(validDto.getType())
                .customerEmail(validDto.getCustomerEmail())
                .description(validDto.getDescription())
                .build();

        given(pqrRepository.save(any(Pqr.class))).willReturn(pqrToSave);
        given(documentRepository.save(any(Document.class))).willReturn(new Document());

        // WHEN
        Pqr result = pqrService.createPqr(validDto, mockFiles);

        // THEN
        assertNotNull(result);
        assertEquals(pqrId, result.getId());
    }

    @Test
    @DisplayName("Should save PQR even if file list is null")
    void shouldCreatePqrWhenFilesAreNull() {
        // GIVEN
        Pqr pqrToSave = Pqr.builder()
                .id(UUID.randomUUID().toString())
                .build();

        given(pqrRepository.save(any(Pqr.class))).willReturn(pqrToSave);

        // WHEN
        Pqr result = pqrService.createPqr(validDto, null);

        // THEN
        assertNotNull(result);
    }

    private org.mockito.verification.VerificationMode shouldHaveNoInteractions() {
        return never();
    }
}