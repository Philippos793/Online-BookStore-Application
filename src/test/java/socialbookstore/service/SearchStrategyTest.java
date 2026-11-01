package socialbookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import socialbookstore.domainmodel.Book;
import socialbookstore.domainmodel.BookAuthor;
import socialbookstore.formsdata.SearchFormData;
import socialbookstore.mappers.BookMapper;

@ExtendWith(MockitoExtension.class)
class SearchStrategyTest {

	@Mock
	private BookMapper bookMapper;

	private final Book dune = book("Dune", "Frank Herbert");
	private final Book duneMessiah = book("Dune Messiah", "Frank Herbert");
	private final Book other = book("Dune Fan Guide", "Someone Else");

	@Test
	void exactSearchFiltersByAuthor() {
		when(bookMapper.findByTitle("Dune")).thenReturn(List.of(dune));

		List<Book> result = new ExactSearchStrategy(bookMapper).search(form("Dune", List.of("Frank Herbert")));

		assertEquals(List.of(dune), result);
	}

	@Test
	void approximateSearchFiltersByAuthor() {
		when(bookMapper.findByTitleContaining("Dune")).thenReturn(List.of(dune, duneMessiah, other));

		List<Book> result = new ApproximateSearchStrategy(bookMapper).search(form("Dune", List.of("Frank Herbert")));

		assertEquals(List.of(dune, duneMessiah), result);
	}

	@Test
	void emptyAuthorFilterMatchesAllBooks() {
		when(bookMapper.findByTitleContaining("Dune")).thenReturn(List.of(dune, other));

		List<Book> result = new ApproximateSearchStrategy(bookMapper).search(form("Dune", List.of()));

		assertEquals(List.of(dune, other), result);
	}

	private static SearchFormData form(String title, List<String> authors) {
		SearchFormData data = new SearchFormData();
		data.setTitle(title);
		data.setAuthors(authors);
		return data;
	}

	private static Book book(String title, String authorName) {
		BookAuthor author = new BookAuthor();
		author.setName(authorName);
		Book book = new Book();
		book.setTitle(title);
		book.setBookAuthors(List.of(author));
		return book;
	}
}
