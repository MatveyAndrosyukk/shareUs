package api.sweater.model.dto;

import api.sweater.model.Message;
import api.sweater.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageDto {
    private Long id;
    private String text;
    private String tag;
    private User author;
    private String image;
    private Long likes;
    private Boolean meLiked;

    public MessageDto(Message message, Long likes, Boolean meLiked) {
            this.id = message.getId();
            this.text = message.getText();
            this.tag = message.getTag();
            this.author = message.getAuthor();
            this.image = message.getImage();
            this.likes = likes;
            this.meLiked = meLiked;
    }
}
