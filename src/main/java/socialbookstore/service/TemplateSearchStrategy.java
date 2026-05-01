package socialbookstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import socialbookstore.domainmodel.Book;
import socialbookstore.formsdata.SearchFormData;
import socialbookstore.mappers.BookMapper;

public abstract class TemplateSearchStrategy implements SearchStrategy {

    @Autowired
    protected BookMapper bookMapper;

    @Override
    public ArrayList<Book> search(SearchFormData searchFormData, BookMapper bookMapper) {
        List<Book> initialList = makeInitialListOfBooks(searchFormData);
        ArrayList<Book> filteredBooks = new ArrayList<>();
        for (Book book : initialList) {

            if (checkIfAuthorsMatch(searchFormData, book)) {

                filteredBooks.add(book);
            }
        }
        return filteredBooks;
    }

    protected abstract List<Book> makeInitialListOfBooks(SearchFormData searchFormData);

    protected abstract boolean checkIfAuthorsMatch(SearchFormData searchFormData, Book book);
}
