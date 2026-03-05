package com.devops.api.pqr.pqr;

import com.devops.api.pqr.pqr.dto.CreatePqrDto;
import com.devops.api.pqr.pqr.entity.Pqr;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pqr")
@RequiredArgsConstructor
public class PqrController {
    private final PqrService pqrService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Pqr> create(
            @RequestPart("metadata") @Valid CreatePqrDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        return ResponseEntity.status(HttpStatus.CREATED).body(pqrService.createPqr(dto, files));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        this.pqrService.delete(id);
        return ResponseEntity.ok("Pqr deleted correctly");
    }

    @GetMapping()
    public ResponseEntity<Iterable<Pqr>> getAll() {
        return ResponseEntity.ok().body(pqrService.getAll());
    }
}