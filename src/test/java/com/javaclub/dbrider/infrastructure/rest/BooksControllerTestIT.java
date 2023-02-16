package com.javaclub.dbrider.infrastructure.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.javaclub.dbrider.IntegrationTest;
import com.javaclub.dbrider.TestContainer;
import com.javaclub.dbrider.domain.Book;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@IntegrationTest
@TestContainer
class BooksControllerTestIT {

    private static final String ROOT_URL = "/client-api/books";

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DataSet(value = "datasets/books/setup/books.yml", cleanBefore = true, cleanAfter = true)
    @ExpectedDataSet(value = "datasets/books/expected/books.yml",
        compareOperation = CompareOperation.EQUALS)
    void getBooks() throws Exception {
        MvcResult response = mockMvc.perform(
            get(ROOT_URL).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        List<Book> books = objectMapper
            .readValue(response.getResponse().getContentAsString(), new TypeReference<List<Book>>() {});
        Assertions.assertEquals(3, books.size());
    }

    @Test
    @DataSet(value = "datasets/books/setup/books.yml", cleanAfter = true)
    @ExpectedDataSet(value = "datasets/books/expected/createNewBook.yml",
        compareOperation = CompareOperation.CONTAINS)
    void createBook() throws Exception {
        Book book = new Book();
        book.setName("Test New");
        book.setAbout("about");
        book.setTitle("TN");
        MvcResult response = mockMvc.perform(
            post(ROOT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book))
        ).andExpect(status().isOk()).andReturn();
    }

    @Test
    @DataSet(value = "datasets/books/setup/books.yml", cleanAfter = true)
    @ExpectedDataSet(value = "datasets/books/expected/createNewBook.yml",
        compareOperation = CompareOperation.EQUALS)
    void createBook2() throws Exception {
        Book book = new Book();
        book.setName("Test New");
        book.setTitle("TN");
        MvcResult response = mockMvc.perform(
            post(ROOT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book))
        ).andExpect(status().isOk()).andReturn();
    }

}