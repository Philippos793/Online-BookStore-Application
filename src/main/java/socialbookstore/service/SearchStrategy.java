package socialbookstore.service;

import java.util.List;

import socialbookstore.domainmodel.Book;
import socialbookstore.formsdata.SearchFormData;

public interface SearchStrategy {
    List<Book> search(SearchFormData searchFormData);
}
