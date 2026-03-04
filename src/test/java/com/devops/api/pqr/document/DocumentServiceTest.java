package com.devops.api.pqr.document;

import com.devops.api.pqr.document.entity.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
                "file",
                "test-file.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );
    }

    @Test
    void shouldSaveDocumentSuccessfully() throws IOException {

        Document expectedDocument = Document.builder()
                .id(UUID.randomUUID().toString())
                .fileName("test-file.pdf")
                .storageUrl("https://storage.provider.com/buckets/pqr-files/test-file.pdf")
                .build();

        when(documentRepository.save(any(Document.class)))
                .thenReturn(expectedDocument);

        Document result = documentService.saveDocument(mockFile);

        assertNotNull(result);
        assertEquals("test-file.pdf", result.getFileName());
        assertEquals(
                "https://storage.provider.com/buckets/pqr-files/test-file.pdf",
                result.getStorageUrl()
        );

    }
}