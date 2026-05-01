package socialbookstore.service;


import java.util.ArrayList;
import java.util.List;

import socialbookstore.domainmodel.Book;
import socialbookstore.formsdata.SearchFormData;
import socialbookstore.mappers.BookMapper;

public interface SearchStrategy {
    ArrayList<Book> search(SearchFormData searchFormData, BookMapper bookMapper);
}

