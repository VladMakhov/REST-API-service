package system.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import system.dto.AuthorDto;
import system.dto.BookDto;
import system.exceptions.notFoundExceptions.AuthorNotFoundException;
import system.exceptions.notFoundExceptions.BookNotFoundException;
import system.models.Author;
import system.models.Book;
import system.repositorys.AuthorRepository;
import system.repositorys.BookRepository;
import system.services.interfaces.BookService;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorServiceImpl authorService;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorServiceImpl authorService, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.authorRepository = authorRepository;
    }

    @Override
    public List<BookDto> getAllBooks() {
        List<Book> bookList = bookRepository.findAll();
        return bookList.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public AuthorDto getAuthorByBookId(long id) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new BookNotFoundException("Book with id=" + id + " not found"));
        return authorService.mapToDto(book.getAuthor());
    }

    @Override
    public List<BookDto> getBooksByBook(long id) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new BookNotFoundException("Book with id=" + id + " not found"));
        return authorService.getBooksByAuthorId(book.getAuthor().getId());
    }

    @Override
    public BookDto createBook(long id, BookDto bookDto) {
        Author author = authorRepository.findById(id).orElseThrow(() ->
                new AuthorNotFoundException("Author with id=" + id + " not found"));

        Book book = mapToEntity(author, bookDto);
        Book newBook = bookRepository.save(book);
        return mapToDto(newBook);
    }

    @Override
    public BookDto updateBook(long id, BookDto bookDto) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new AuthorNotFoundException("Author with id=" + id + " not found"));
        Author author = authorRepository.findById(book.getAuthor().getId()).
                orElseThrow(() -> new AuthorNotFoundException("Author with id=" + id + " not found"));
        book.setBookName(bookDto.getBookName());
        book.setYear(bookDto.getYear());
        book.setAuthor(author);
        bookRepository.save(book);
        return mapToDto(book);
    }

    @Override
    public BookDto deleteBook(long id) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new AuthorNotFoundException("Author with id=" + id + " not found"));
        BookDto bookDto = mapToDto(book);
        bookRepository.delete(book);
        return bookDto;
    }

    @Override
    public BookDto getBookById(long id) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new BookNotFoundException("Book with id=" + id + " not found"));
        return mapToDto(book);
    }

    public BookDto mapToDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setBookName(book.getBookName());
        bookDto.setYear(book.getYear());
        bookDto.setAuthor(book.getAuthor().getName() + " " + book.getAuthor().getLastName());
        return bookDto;
    }

    public Book mapToEntity(Author author, BookDto bookDto) {
        Book book = new Book();
        book.setId(bookDto.getId());
        book.setBookName(bookDto.getBookName());
        book.setYear(bookDto.getYear());
        book.setAuthor(author);
        return book;
    }

}
