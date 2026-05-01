package socialbookstore.domainmodel;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jdk.jfr.Category;
@Entity
@Table(name="books")
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="book_id")
	private int bookId;
	
	@Column(name="title")
	private String title;
	
	@Column(name="summary")
	private String summary;
	
	  
	 
	@ManyToMany(fetch = FetchType.LAZY, 
			cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(
                    name = "book_id", referencedColumnName = "book_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "author_id", referencedColumnName = "author_id"
            )
    )
	private List<BookAuthor> bookAuthors;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="category_id")

	private BookCategory BookCategory;

	@ManyToMany
	@JoinTable(
			name = "books_requested",
			joinColumns = @JoinColumn(name = "book_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private List<UserProfile> requestingUsers;
	
	
	 @ManyToOne
	    @JoinColumn(name="username", referencedColumnName="username")
	    private UserProfile userProfile;
	
	

	

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<BookAuthor> getBookAuthors() {
		return bookAuthors;
	}

	public void setBookAuthors(List<BookAuthor> bookAuthors) {
		this.bookAuthors = bookAuthors;
	}

	public BookCategory getBookCategory() {
		return BookCategory;
	}

	public void setBookCategory(BookCategory bookCategory) {
		BookCategory = bookCategory;
	}

	public List<UserProfile> getRequestingUsers() {
		return requestingUsers;
	}

	public void setRequestingUsers(List<UserProfile> requestingUsers) {
		this.requestingUsers = requestingUsers;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	
}
