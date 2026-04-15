package com.devops.api.pqr.pqr;

import com.devops.api.pqr.book.BookOrderNotificationRepository;
import com.devops.api.pqr.book.BookOrderPort;
import com.devops.api.pqr.document.DocumentRepository;
import com.devops.api.pqr.document.entity.Document;
import com.devops.api.pqr.pqr.dto.BookDto;
import com.devops.api.pqr.pqr.dto.CreatePqrDto;
import com.devops.api.pqr.pqr.entity.Pqr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pqr Service Tests")
class PqrServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PqrRepository pqrRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private BookOrderPort bookOrderPort;

    @Mock
    private BookOrderNotificationRepository notificationRepository;

    @InjectMocks
    private PqrService pqrService;

    private CreatePqrDto validDto;

    @BeforeEach
    void setUp() {

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
    @DisplayName("Should save PQR with documents")
    void shouldCreatePqrWithDocumentsSuccessfully() {

        Pqr saved = Pqr.builder().id("123").type("peticion").subject("comprar libro").book(Map.of("bookTitle","Clean Code","bookAuthor","Robert Martin")).build();

        given(pqrRepository.save(any())).willReturn(saved);
        given(documentRepository.save(any())).willReturn(new Document());

        Pqr result = pqrService.createPqr(validDto,
                List.of(new MockMultipartFile("file", "test.pdf", "application/pdf", "data".getBytes())));

        assertNotNull(result);
        assertEquals("123", result.getId());
    }

    @Test
    @DisplayName("Should delete PQR successfully")
    void shouldDeletePqr() {

        Pqr pqr = Pqr.builder().id("1").build();

        given(pqrRepository.findById("1")).willReturn(Optional.of(pqr));

        assertDoesNotThrow(() -> pqrService.delete("1"));
    }

    @Test
    @DisplayName("Should throw when deleting non-existing PQR")
    void shouldThrowWhenDeleteNotFound() {

        given(pqrRepository.findById("1")).willReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> pqrService.delete("1"));
    }

    @Test
    @DisplayName("Should return all PQRs")
    void shouldGetAll() {

        List<Pqr> list = List.of(Pqr.builder().id("1").build());

        given(pqrRepository.findAll()).willReturn(list);

        Iterable<Pqr> result = pqrService.getAll();

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should trigger order when threshold is reached")
    void shouldTriggerOrder() {

        Pqr saved = Pqr.builder()
                .id("1")
                .type("peticion")
                .subject("comprar libro")
                .book(Map.of("bookTitle","Clean Code","bookAuthor","Robert Martin"))
                .build();

        given(pqrRepository.save(any())).willReturn(saved);
        given(notificationRepository.existsByBookTitleAndBookAuthor(any(), any())).willReturn(false);
        given(pqrRepository.countByTypeSubjectAndBook(any(), any(), any(), any())).willReturn(10L);
        given(bookOrderPort.notifyBookOrder(any())).willReturn(true);

        Pqr result = pqrService.createPqr(validDto, null);

        assertNotNull(result);
    }
}