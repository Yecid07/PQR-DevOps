package com.devops.api.pqr.pqr;

import com.devops.api.pqr.document.mapper.DocumentMapper;
import com.devops.api.pqr.pqr.dto.BookDto;
import com.devops.api.pqr.pqr.dto.CreatePqrDto;
import com.devops.api.pqr.pqr.entity.Pqr;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PqrController.class)
@DisplayName("Pqr Controller Tests")
public class PqrControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PqrService pqrService;

    @MockitoBean
    private DocumentMapper documentMapper;

    private static final String BASE_URL = "/api/v1/pqr";


    private CreatePqrDto validDto;
    private BookDto bookDto;
    private Map<String, Object> bookMap;
    private List<MultipartFile> mockFiles;

    @BeforeEach
    void setUp() {
        bookMap = new HashMap<>();
        bookMap.put("bookTitle","Clean Code");
        bookMap.put("bookAuthor","Robert Martin");
        bookDto = new BookDto("Clean Code", "Robert Martin");
        validDto = new CreatePqrDto("peticion", "customer@test.com", "Service description", "comprar libro",bookDto);

    }


    @Test
    @DisplayName("POST /pqr - Should create PQR with files successfully")
    void testCreatePqrShouldReturnCreated() throws Exception {
        // GIVEN

        MockMultipartFile metadataPart = new MockMultipartFile(
                "pqr",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validDto)
        );

        MockMultipartFile filePart = new MockMultipartFile(
                "files",
                "evidence.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "dummy content".getBytes()
        );

        Pqr savedPqr = Pqr.builder()
                .id(UUID.randomUUID().toString())
                .type(validDto.getType())
                .subject(validDto.getSubject())
                .book(bookMap)
                .customerEmail(validDto.getCustomerEmail())
                .description(validDto.getDescription())
                .build();

        given(pqrService.createPqr(any(CreatePqrDto.class), anyList()))
                .willReturn(savedPqr);

        // WHEN
        // THEN
        mockMvc.perform(multipart(BASE_URL)
                        .file(metadataPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value(savedPqr.getType()));
    }

    @Test
    @DisplayName("POST /pqr - Should return 400 when metadata is invalid")
    void testCreatePqrShouldReturnBadRequestWhenInvalidDto() throws Exception {
        // GIVEN
        bookDto = new BookDto("Clean Code", " ");
        CreatePqrDto invalidDto = new CreatePqrDto(" ", " ", "Service description", "comprar libro",bookDto);;

        MockMultipartFile metadataPart = new MockMultipartFile(
                "metadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(invalidDto)
        );

        // WHEN
        // THEN
        mockMvc.perform(multipart(BASE_URL)
                        .file(metadataPart))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /pqr/{id} - Should delete PQR successfully")
    void testDeletePqrShouldReturnOk() throws Exception {
        // GIVEN
        String pqrId = UUID.randomUUID().toString();

        // no necesitas given() porque delete es void

        // WHEN
        // THEN
        mockMvc.perform(delete(BASE_URL + "/" + pqrId))
                .andExpect(status().isOk())
                .andExpect(content().string("Pqr deleted correctly"));
    }

    @Test
    @DisplayName("GET /pqr - Should return all PQRs")
    void testGetAllPqrShouldReturnList() throws Exception {
        // GIVEN
        Pqr pqr1 = Pqr.builder()
                .id(UUID.randomUUID().toString())
                .type("peticion")
                .subject("comprar libro")
                .book(bookMap)
                .customerEmail("test1@test.com")
                .description("desc1")
                .build();

        Pqr pqr2 = Pqr.builder()
                .id(UUID.randomUUID().toString())
                .type("queja")
                .subject("otro")
                .book(bookMap)
                .customerEmail("test2@test.com")
                .description("desc2")
                .build();

        given(pqrService.getAll()).willReturn(List.of(pqr1, pqr2));

        // WHEN
        // THEN
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("POST /pqr/try - Should return same body")
    void testPruebaShouldReturnSameBody() throws Exception {
        // GIVEN
        Map<String, Object> body = new HashMap<>();
        body.put("message", "hello");
        body.put("value", 123);

        // WHEN
        // THEN
        mockMvc.perform(post(BASE_URL + "/try")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("hello"))
                .andExpect(jsonPath("$.value").value(123));
    }
}