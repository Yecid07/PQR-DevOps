package com.devops.api.pqr.book;

import com.devops.api.pqr.pqr.entity.Pqr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookOrderAdapter implements BookOrderPort {

    private final RestClient restClient;

    @Value("${services.book.order.url}")
    private String bookOrderServiceUrl;

    @Value("${pqr.book.order.threshold:5}")
    private int bookOrderThreshold;

    @Override
    public boolean notifyBookOrder(Pqr pqr) {
        try {
            restClient.post()
                    .uri(bookOrderServiceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildPayload(pqr))
                    .retrieve()
                    .toBodilessEntity();

            log.info("Book order notified for PQR id={}", pqr.getId());
            return true;
        } catch (Exception e) {
            log.error("Failed to notify book order service for PQR id={}: {}", pqr.getId(), e.getMessage());
            return false;
        }
    }

    private Map<String, Object> buildPayload(Pqr pqr) {
        Map<String, Object> pqrPayload = Map.of(
                "id",          pqr.getId(),
                "asunto",      pqr.getSubject(),
                "responsable", pqr.getCustomerEmail(),
                "conteo",      bookOrderThreshold
        );

        return Map.of(
                "titulo_libro", pqr.getBook().get("bookTitle"),
                "autor",        pqr.getBook().get("bookAuthor"),
                "pqr",          pqrPayload
        );
    }
}