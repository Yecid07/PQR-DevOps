package com.devops.api.pqr.document;

import com.devops.api.pqr.document.dto.DocumentResponseDto;
import com.devops.api.pqr.document.entity.Document;
import com.devops.api.pqr.document.mapper.DocumentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
@DisplayName("Document Controller Tests")
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private DocumentMapper documentMapper;

    private static final String BASE_URL = "/api/v1/document";

    private static final String DOCUMENT_ID =
            UUID.randomUUID().toString();
    @Test
    @DisplayName("POST /document - Should upload document successfully")
    void testUploadShouldReturnCreatedWhenFileIsValid() throws Exception {

        // GIVEN
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "dummy content".getBytes()
        );

        Document savedDocument = createDocumentResponse();
        DocumentResponseDto responseDto = createDocumentResponseDto();

        given(documentService.saveDocument(any()))
                .willReturn(savedDocument);

        given(documentMapper.toResponseDto(any(Document.class)))
                .willReturn(responseDto);

        // WHEN
        // THEN
        mockMvc.perform(multipart(BASE_URL)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.fileName").value(responseDto.getFileName()))
                .andExpect(jsonPath("$.storageUrl").value(responseDto.getStorageUrl()));
    }

    @Test
    @DisplayName("POST /document - Should return 400 when service throws exception")
    void testUploadShouldReturnBadRequestWhenServiceFails() throws Exception {

        // GIVEN
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "dummy content".getBytes()
        );

        willThrow(new RuntimeException("Storage error"))
                .given(documentService)
                .saveDocument(any());

        // WHEN
        // THEN
        mockMvc.perform(multipart(BASE_URL)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    private Document createDocumentResponse() {
        return Document.builder()
                .id(DOCUMENT_ID)
                .fileName("test-file.pdf")
                .storageUrl("https://storage.provider.com/buckets/pqr-files/test-file.pdf")
                .build();
    }

    private DocumentResponseDto createDocumentResponseDto() {
        return new DocumentResponseDto(
                DOCUMENT_ID,
                "test-file.pdf",
                "https://storage.provider.com/buckets/pqr-files/test-file.pdf"
        );
    }
}