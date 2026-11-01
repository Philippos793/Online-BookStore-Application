package socialbookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.AccessDeniedException;

import socialbookstore.domainmodel.Book;
import socialbookstore.domainmodel.UserProfile;
import socialbookstore.mappers.BookAuthorMapper;
import socialbookstore.mappers.BookCategoryMapper;
import socialbookstore.mappers.BookMapper;
import socialbookstore.mappers.UserProfileMapper;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

	@Mock private UserProfileMapper userProfileMapper;
	@Mock private BookAuthorMapper bookAuthorMapper;
	@Mock private BookCategoryMapper bookCategoryMapper;
	@Mock private BookMapper bookMapper;
	@Mock private ExactSearchStrategy exactSearchStrategy;
	@Mock private ApproximateSearchStrategy approximateSearchStrategy;
	@Mock private AsyncEmailService asyncEmailService;

	@InjectMocks
	private UserProfileServiceImpl service;

	private UserProfile owner;
	private UserProfile alice;
	private UserProfile bob;
	private Book book;

	@BeforeEach
	void setUp() {
		owner = profile("owner");
		alice = profile("alice");
		bob = profile("bob");

		book = new Book();
		book.setBookId(1);
		book.setTitle("Dune");
		book.setUserProfile(owner);
		book.setRequestingUsers(new ArrayList<>(List.of(alice, bob)));
	}

	@Test
	void acceptRequestNotifiesEveryRequesterAndClearsRequests() {
		when(bookMapper.findById(1)).thenReturn(Optional.of(book));

		service.acceptRequest(1, "owner", "alice");

		verify(asyncEmailService, times(2)).sendEmail(any(SimpleMailMessage.class));
		assertTrue(book.getRequestingUsers().isEmpty());
		verify(bookMapper).save(book);
	}

	@Test
	void acceptRequestIsDeniedForNonOwner() {
		when(bookMapper.findById(1)).thenReturn(Optional.of(book));

		assertThrows(AccessDeniedException.class, () -> service.acceptRequest(1, "bob", "alice"));
		verify(asyncEmailService, never()).sendEmail(any());
	}

	@Test
	void acceptRequestRejectsUserWhoDidNotRequest() {
		when(bookMapper.findById(1)).thenReturn(Optional.of(book));

		assertThrows(IllegalArgumentException.class, () -> service.acceptRequest(1, "owner", "mallory"));
	}

	@Test
	void requestingUsersAreVisibleOnlyToOwner() {
		when(bookMapper.findById(1)).thenReturn(Optional.of(book));

		assertEquals(List.of(alice, bob), service.retrieveRequestingUsers(1, "owner"));
		assertThrows(AccessDeniedException.class, () -> service.retrieveRequestingUsers(1, "alice"));
	}

	@Test
	void cannotRequestOwnBook() {
		when(bookMapper.findById(1)).thenReturn(Optional.of(book));
		when(userProfileMapper.findByUsername("owner")).thenReturn(owner);

		assertThrows(IllegalArgumentException.class, () -> service.requestBook(1, "owner"));
	}

	@Test
	void cannotRequestSameBookTwice() {
		when(bookMapper.findById(1)).thenReturn(Optional.of(book));
		when(userProfileMapper.findByUsername("alice")).thenReturn(alice);

		assertThrows(IllegalArgumentException.class, () -> service.requestBook(1, "alice"));
	}

	@Test
	void deleteBookOfferIsDeniedForNonOwner() {
		when(bookMapper.findById(1)).thenReturn(Optional.of(book));

		assertThrows(AccessDeniedException.class, () -> service.deleteBookOffer("alice", 1));
		verify(bookMapper, never()).delete(any());
	}

	private static UserProfile profile(String username) {
		UserProfile profile = new UserProfile();
		profile.setUsername(username);
		profile.setEmail(username + "@example.com");
		return profile;
	}
}
