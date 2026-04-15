package com.devops.api.pqr.pqr;

import com.devops.api.pqr.book.dto.BookOrderResponse;
import com.devops.api.pqr.book.entity.BookOrderResult;
import com.devops.api.pqr.book.mapper.BookOrderResultMapper;
import com.devops.api.pqr.book.repository.BookOrderNotificationRepository;
import com.devops.api.pqr.book.BookOrderPort;
import com.devops.api.pqr.book.entity.BookOrderNotification;
import com.devops.api.pqr.book.repository.BookOrderResultRepository;
import com.devops.api.pqr.document.entity.Document;
import com.devops.api.pqr.document.DocumentRepository;
import com.devops.api.pqr.pqr.dto.CreatePqrDto;
import com.devops.api.pqr.pqr.dto.UpdatePqrDto;
import com.devops.api.pqr.pqr.entity.Pqr;
import com.devops.api.pqr.pqr.mapper.PqrMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PqrService {

    private final PqrMapper pqrMapper;
    private final ObjectMapper objectMapper;
    private final PqrRepository pqrRepository;
    private final DocumentRepository documentRepository;
    private final BookOrderPort bookOrderPort;
    private final BookOrderNotificationRepository notificationRepository;
    private final BookOrderResultRepository bookOrderResultRepository;
    private final BookOrderResultMapper bookOrderResultMapper;

    @Value("${pqr.book.order.threshold:5}")
    private int bookOrderThreshold;

    @Transactional
    public Pqr createPqr(CreatePqrDto dto, List<MultipartFile> files) {
        Map<String, Object> bookMap = objectMapper.convertValue(dto.getBook(), Map.class);

        Pqr pqr = Pqr.builder()
                .type(dto.getType())
                .customerEmail(dto.getCustomerEmail())
                .description(dto.getDescription())
                .subject(dto.getSubject())
                .book(bookMap)
                .build();

        Pqr savedPqr = pqrRepository.save(pqr);

        saveFiles(files, savedPqr);
        triggerOrder(savedPqr);

        return savedPqr;
    }

    private void saveFiles(List<MultipartFile> files, Pqr savedPqr) {
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
    }

    public void delete(String id) {
        Pqr reviewer = pqrRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id not found"));
        pqrRepository.delete(reviewer);
    }

    public Iterable<Pqr> getAll() {
        return pqrRepository.findAll();
    }

    private void triggerOrder(Pqr savedPqr) {
        if (!"peticion".equals(savedPqr.getType()) || !"comprar libro".equals(savedPqr.getSubject())) {
            return;
        }

        String bookTitle  = (String) savedPqr.getBook().get("bookTitle");
        String bookAuthor = (String) savedPqr.getBook().get("bookAuthor");

        boolean alreadyNotified = notificationRepository
                .existsByBookTitleAndBookAuthor(bookTitle, bookAuthor);

        if (alreadyNotified) {
            log.info("Book order already notified for title={} author={}", bookTitle, bookAuthor);
            return;
        }

        long count = pqrRepository.countByTypeSubjectAndBook(
                "peticion", "comprar libro", bookTitle, bookAuthor);

        log.info("PQR count for book title={} author={}: {}/{}", bookTitle, bookAuthor, count, bookOrderThreshold);

        if (count >= bookOrderThreshold) {
            BookOrderResponse response = bookOrderPort.notifyBookOrder(savedPqr);

            if (response != null) {
                notificationRepository.save(BookOrderNotification.builder()
                        .bookTitle(bookTitle)
                        .bookAuthor(bookAuthor)
                        .notifiedAt(LocalDateTime.now())
                        .build());

                saveBookOrderResult(response, bookTitle, bookAuthor);
            }
        }
    }

    @Transactional
    public Pqr updatePqr(String id, UpdatePqrDto dto) {

        Pqr pqr = pqrRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "PQR with id " + id + " not found"));

        pqrMapper.updatePqr(dto, pqr);

        Pqr updatedPqr = pqrRepository.save(pqr);

        triggerOrder(updatedPqr);

        return updatedPqr;
    }

    private void saveBookOrderResult(BookOrderResponse response, String bookTitle, String bookAuthor) {

        BookOrderResult result = bookOrderResultMapper.toEntity(response,bookTitle,bookAuthor);

        bookOrderResultRepository.save(result);

        log.info("BookOrderResult saved for book title={}", bookTitle);
    }

}