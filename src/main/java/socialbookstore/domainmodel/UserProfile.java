package socialbookstore.domainmodel;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="profiles")
public class UserProfile {
	
	
	
	@Id
	@Column(name="username")
	private String username;
	
	@Column(name="full_name")
	private String fullName;
	
	@Column(name="address")
	private String address;
	
	@Column(name="email")
	private String email;
	
	@Column(name="age")
	private int age;
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	 // Establish a foreign key to User without using the primary key as shared
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // This should be a separate column in UserProfile
    private User user;
	
	
	@ManyToMany(fetch = FetchType.EAGER,
			cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
	@JoinTable(
			name = "user_favourite_authors",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "author_id")
	)
	private List<BookAuthor> favouriteBookAuthors;

	@ManyToMany(fetch = FetchType.EAGER,
			cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
	@JoinTable(
			name = "user_favourite_categories",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "category_id")
	)
	private List<BookCategory> favouriteBookCategories;

	@OneToMany(mappedBy = "userProfile", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Book> bookOffers;



	@ManyToMany(mappedBy = "requestingUsers")
	private List<Book> requestedBooks;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<BookAuthor> getFavouriteBookAuthors() {
		return favouriteBookAuthors;
	}

	public void setFavouriteBookAuthors(List<BookAuthor> favouriteBookAuthors) {
		this.favouriteBookAuthors = favouriteBookAuthors;
	}

	public List<BookCategory> getFavouriteBookCategories() {
		return favouriteBookCategories;
	}

	public void setFavouriteBookCategories(List<BookCategory> favouriteBookCategories) {
		this.favouriteBookCategories = favouriteBookCategories;
	}

	public List<Book> getBookOffers() {
		return bookOffers;
	}

	public void setBookOffers(List<Book> bookOffers) {
		this.bookOffers = bookOffers;
	}

	public List<Book> getRequestedBooks() {
		return requestedBooks;
	}

	public void setRequestedBooks(List<Book> requestedBooks) {
		this.requestedBooks = requestedBooks;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
