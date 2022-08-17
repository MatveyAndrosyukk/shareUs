package api.sweater.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sweater_message")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 3000, message = "Message is too long")
    private String text;

    @Pattern(regexp = "#.+", message = "Enter tag correctly")
    @Length(max = 40, message = "Tag is too long")
    private String tag;

    @Lob
    private byte[] image;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User author;

    @ManyToMany
    @JoinTable(
            name = "message_likes",
            joinColumns = { @JoinColumn(name = "message_id")},
            inverseJoinColumns = { @JoinColumn(name = "user_id")}
    )
    @ToString.Exclude
    private Set<User> likes = new HashSet<>();

    public Message(String text, String tag, User user) {
        this.author = user;
        this.text = text;
        this.tag = tag;
    }

    public String getImage() {
        if (this.image == null){
            return null;
        }else {
            byte[] encode = Base64.getEncoder().encode(this.image);
            return new String(encode, StandardCharsets.UTF_8);
        }
    }
}
