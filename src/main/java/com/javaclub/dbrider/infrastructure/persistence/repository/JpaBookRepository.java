package com.javaclub.dbrider.infrastructure.persistence.repository;

import com.javaclub.dbrider.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookRepository extends JpaRepository<Book, Long> {

}
