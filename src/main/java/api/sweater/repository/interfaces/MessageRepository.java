package api.sweater.repository.interfaces;

import api.sweater.model.Message;
import api.sweater.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long>{
    List<Message> findByTag(String tag);
    List<Message> findByAuthor(User user);
}
