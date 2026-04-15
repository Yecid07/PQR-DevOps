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

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PqrController.class)
@DisplayName("Pqr Controller Tests")
class PqrControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PqrService pqrService;

    @MockitoBean
    private DocumentMapper documentMapper;

    private static final String BASE_URL = "/api/v2/pqr";

    private CreatePqrDto validDto;
    private Map<String, Object> bookMap;

    @BeforeEach
    void setUp() {
        bookMap = new HashMap<>();
        bookMap.put("bookTitle", "Clean Code");
        bookMap.put("bookAuthor", "Robert Martin");

        BookDto bookDto = new BookDto("Clean Code", "Robert Martin");
        validDto = new CreatePqrDto(
                "peticion",
                "customer@test.com",
                "Service description",
                "comprar libro",
                bookDto
        );
    }

    @Test
    @DisplayName("POST /pqr - Should create PQR with files successfully")
    void testCreatePqrShouldReturnCreated() throws Exception {

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

        BookDto bookDto = new BookDto("Clean Code", " ");
        CreatePqrDto invalidDto = new CreatePqrDto(" ", " ", "desc", "comprar libro", bookDto);

        MockMultipartFile metadataPart = new MockMultipartFile(
                "pqr",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(invalidDto)
        );

        mockMvc.perform(multipart(BASE_URL)
                        .file(metadataPart))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /pqr/{id} - Should delete successfully")
    void testDeleteShouldReturnOk() throws Exception {

        mockMvc.perform(delete(BASE_URL + "/{id}", "123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pqr deleted correctly"));
    }

    @Test
    @DisplayName("GET /pqr - Should return all PQRs")
    void testGetAllShouldReturnOk() throws Exception {

        List<Pqr> list = List.of(
                Pqr.builder().id("1").type("peticion").build(),
                Pqr.builder().id("2").type("queja").build()
        );

        given(pqrService.getAll()).willReturn(list);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    @DisplayName("POST /pqr/try - Should return same body")
    void testPruebaShouldReturnSameBody() throws Exception {

        Map<String, Object> body = Map.of("key", "value");

        mockMvc.perform(post(BASE_URL + "/try")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("value"));
    }
}