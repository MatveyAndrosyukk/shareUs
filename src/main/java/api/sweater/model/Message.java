package api.sweater.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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

    @NotBlank(message = "Please, fill the message")
    @Length(max = 255, message = "Message too long, max 255 symbols")
    private String text;

    @Pattern(regexp = "#.+", message = "Enter #text no longer 40 symbols")
    @Length(max = 40, message = "Enter #text no longer 40 symbols")
    private String tag;

    private String filename;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User author;

    public Message(String text, String tag, User user) {
        this.author = user;
        this.text = text;
        this.tag = tag;
    }

    public String getAuthorName(){
        return author != null ? author.getUsername() : "<none>";
    }
}
