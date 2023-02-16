package com.javaclub.dbrider.infrastructure.rest;

import com.javaclub.dbrider.domain.Book;
import com.javaclub.dbrider.infrastructure.persistence.repository.JpaBookRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BooksController {

    private final JpaBookRepository jpaBookRepository;

    public BooksController(JpaBookRepository jpaBookRepository) {
        this.jpaBookRepository = jpaBookRepository;
    }

    @GetMapping(value = "/client-api/books", produces = {"application/json"})
    public ResponseEntity<List<Book>> getBooks() {
        List<Book> all = jpaBookRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping(value = "/client-api/books/{id}", produces = {"application/json"})
    public ResponseEntity<Book> getBook(@PathVariable long id) {
        return ResponseEntity.of(jpaBookRepository.findById(id));
    }

    @PostMapping(value = "/client-api/books", produces = {"application/json"})
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        return ResponseEntity.ok(jpaBookRepository.save(book));
    }

}
