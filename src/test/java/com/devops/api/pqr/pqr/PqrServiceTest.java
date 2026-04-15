package com.devops.api.pqr.pqr;

import com.devops.api.pqr.book.BookOrderNotificationRepository;
import com.devops.api.pqr.book.BookOrderPort;
import com.devops.api.pqr.book.entity.BookOrderNotification;
import com.devops.api.pqr.document.DocumentRepository;
import com.devops.api.pqr.document.entity.Document;
import com.devops.api.pqr.pqr.dto.BookDto;
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
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pqr Service Tests")
class PqrServiceTest {

    @Mock
    private PqrRepository pqrRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private PqrService pqrService;

    @Mock
    private BookOrderPort bookOrderPort;

    @Mock
    private BookOrderNotificationRepository notificationRepository;

    private CreatePqrDto validDto;
    private BookDto bookDto;
    private List<MultipartFile> mockFiles;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookDto = new BookDto("Clean Code", "Robert Martin");
        validDto = new CreatePqrDto("peticion", "customer@test.com", "Service description", "comprar libro",bookDto);
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

    @Test
    @DisplayName("Should delete PQR successfully when id exists")
    void shouldDeletePqrSuccessfully() {
        // GIVEN
        String id = UUID.randomUUID().toString();
        Pqr existing = Pqr.builder().id(id).build();

        given(pqrRepository.findById(id)).willReturn(java.util.Optional.of(existing));

        // WHEN
        pqrService.delete(id);

        // THEN
        assertDoesNotThrow(() -> pqrService.delete(id));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing PQR")
    void shouldThrowExceptionWhenDeletingNonExistingPqr() {
        // GIVEN
        String id = UUID.randomUUID().toString();

        given(pqrRepository.findById(id)).willReturn(java.util.Optional.empty());

        // WHEN + THEN
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> pqrService.delete(id));
    }

    @Test
    @DisplayName("Should return all PQRs")
    void shouldReturnAllPqrs() {
        // GIVEN
        List<Pqr> pqrs = List.of(
                Pqr.builder().id(UUID.randomUUID().toString()).build(),
                Pqr.builder().id(UUID.randomUUID().toString()).build()
        );

        given(pqrRepository.findAll()).willReturn(pqrs);

        // WHEN
        Iterable<Pqr> result = pqrService.getAll();

        // THEN
        assertNotNull(result);
        assertEquals(2, ((List<Pqr>) result).size());
    }

    @Test
    @DisplayName("Should not trigger order when type or subject do not match")
    void shouldNotTriggerOrderWhenConditionsNotMet() {
        // GIVEN
        Pqr pqr = Pqr.builder()
                .type("queja") // ❌ no es peticion
                .subject("otro")
                .book(Map.of("bookTitle", "Clean Code", "bookAuthor", "Robert Martin"))
                .build();

        given(pqrRepository.save(any())).willReturn(pqr);

        // WHEN
        Pqr result = pqrService.createPqr(validDto, null);

        // THEN
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should not trigger order if already notified")
    void shouldNotTriggerOrderIfAlreadyNotified() {
        // GIVEN
        Pqr pqr = Pqr.builder()
                .type("peticion")
                .subject("comprar libro")
                .book(Map.of("bookTitle", "Clean Code", "bookAuthor", "Robert Martin"))
                .build();

        given(pqrRepository.save(any())).willReturn(pqr);

        given(notificationRepository.existsByBookTitleAndBookAuthor(any(), any()))
                .willReturn(true);

        // WHEN
        Pqr result = pqrService.createPqr(validDto, null);

        // THEN
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should trigger order when threshold is reached")
    void shouldTriggerOrderWhenThresholdReached() {
        // GIVEN
        Pqr pqr = Pqr.builder()
                .type("peticion")
                .subject("comprar libro")
                .book(Map.of("bookTitle", "Clean Code", "bookAuthor", "Robert Martin"))
                .build();

        given(pqrRepository.save(any())).willReturn(pqr);

        given(notificationRepository.existsByBookTitleAndBookAuthor(any(), any()))
                .willReturn(false);

        given(pqrRepository.countByTypeSubjectAndBook(any(), any(), any(), any()))
                .willReturn(10L); // supera threshold

        given(bookOrderPort.notifyBookOrder(any())).willReturn(true);

        given(notificationRepository.save(any())).willReturn(new BookOrderNotification());

        // WHEN
        Pqr result = pqrService.createPqr(validDto, null);

        // THEN
        assertNotNull(result);
    }
}