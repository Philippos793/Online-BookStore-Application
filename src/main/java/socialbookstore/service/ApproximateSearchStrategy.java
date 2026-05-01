package socialbookstore.service;


import java.util.List;
import org.springframework.stereotype.Service;

import socialbookstore.domainmodel.Book;
import socialbookstore.formsdata.SearchFormData;

@Service
public class ApproximateSearchStrategy extends TemplateSearchStrategy {

    @Override
    protected List<Book> makeInitialListOfBooks(SearchFormData searchFormData) {
        return bookMapper.findByTitleContaining(searchFormData.getTitle());
    }

    @Override
    protected boolean checkIfAuthorsMatch(SearchFormData searchFormData, Book book) {
    	 return book.getBookAuthors().stream()
    	            .anyMatch(author -> searchFormData.getAuthors().contains(author.getName()));
    	    }
}

