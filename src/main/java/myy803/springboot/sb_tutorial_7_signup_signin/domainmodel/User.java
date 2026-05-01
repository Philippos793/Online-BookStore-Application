package myy803.springboot.sb_tutorial_7_signup_signin.domainmodel;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User implements UserDetails{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int user_id;
	
	@Column(name="username", unique=true)
	private String username;
	
	@Column(name="password")
	private String password;
	
    @Enumerated(EnumType.STRING)
    @Column(name="role")
	private Role role= Role.USER;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;
  
    //Αποφασίστηκε να χρησιμοποιηθεί η στρατηγική @MapsId για να επιτρέψουμε στην UserProfile να χρησιμοποιήσει το user_id του User ως πρωτεύον κλειδί της. 
    //Αυτό βοήθησε να εξασφαλίσουμε ότι κάθε προφίλ χρήστη συνδέεται απευθείας και μοναδικά με έναν χρήστη,
    //διατηρώντας την ακεραιότητα και τη συνέπεια των δεδομένων.
  
   
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		 SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
	     return Collections.singletonList(authority);
	}
	
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public int getId() {
		return user_id;
	}

	public Role getRole() {
		return role;
	}

	public void setId(int id) {
		this.user_id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	  public UserProfile getUserProfile() {
	        return userProfile;
	    }

	    public void setUserProfile(UserProfile userProfile) {
	        this.userProfile = userProfile;
	    }
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	 
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
}
