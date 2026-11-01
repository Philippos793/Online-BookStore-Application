package socialbookstore.service;

import java.util.List;

import socialbookstore.domainmodel.Book;
import socialbookstore.formsdata.SearchFormData;
import socialbookstore.mappers.BookMapper;

public abstract class TemplateSearchStrategy implements SearchStrategy {

    protected final BookMapper bookMapper;

    protected TemplateSearchStrategy(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Override
    public List<Book> search(SearchFormData searchFormData) {
        return makeInitialListOfBooks(searchFormData).stream()
                .filter(book -> checkIfAuthorsMatch(searchFormData, book))
                .toList();
    }

    protected abstract List<Book> makeInitialListOfBooks(SearchFormData searchFormData);

    // An empty author filter matches every book
    protected boolean checkIfAuthorsMatch(SearchFormData searchFormData, Book book) {
        List<String> authors = searchFormData.getAuthors();
        if (authors == null || authors.isEmpty()) {
            return true;
        }
        return book.getBookAuthors().stream()
                .anyMatch(author -> authors.contains(author.getName()));
    }
}
