package socialbookstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import socialbookstore.domainmodel.Book;
import socialbookstore.formsdata.SearchFormData;
import socialbookstore.mappers.BookMapper;

@Service
public class ExactSearchStrategy extends TemplateSearchStrategy {

    public ExactSearchStrategy(BookMapper bookMapper) {
        super(bookMapper);
    }

    @Override
    protected List<Book> makeInitialListOfBooks(SearchFormData searchFormData) {
        return bookMapper.findByTitle(searchFormData.getTitle());
    }
}
