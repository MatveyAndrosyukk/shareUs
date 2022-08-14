package api.sweater.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sweater_user")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User implements UserDetails{

    @Serial
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username should not be empty")
    private String username;

    @NotBlank(message = "Password should not be empty")
    private String password;

    @Email(message = "Email is not correct")
    @NotBlank(message = "Email should not be empty")
    private String email;

    private String activationCode;

    private boolean active;

    private String imageFilename;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "sweater_user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Collection<Role> roles;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<Message> messages;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
            name = "sweater_user_subscriptions",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "subscriber_id")
    )
    private Set<User> subscribers = new HashSet<>();

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
            name = "sweater_user_subscriptions",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    private Set<User> subscriptions = new HashSet<>();

    public User(String username, String password, String email, String activationCode, boolean active, Collection<Role> roles, String imageFilename) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.activationCode = activationCode;
        this.active = active;
        this.roles = roles;
        this.imageFilename = imageFilename;
    }

    public boolean isAdmin(){
        return roles.contains(new Role("ADMIN"));
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
        return isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public boolean isSubscriber(User user){
        return this.subscribers.contains(user);
    }

    public Long countSubscribers(){
        return (long) subscribers.size();
    }

    public Long countSubscriptions(){
        return (long) subscriptions.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
