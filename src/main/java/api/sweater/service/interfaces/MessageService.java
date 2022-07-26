package api.sweater.service.interfaces;

import api.sweater.model.Message;
import api.sweater.model.User;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    public void findFilteredMessages(String filter, List<Message> messages);

    List<Message> findAll();

    void save(Message message);

    List<Message> findByAuthor(User author);

    void deleteById(Long id);

    Optional<Message> findById(Long id);
}
