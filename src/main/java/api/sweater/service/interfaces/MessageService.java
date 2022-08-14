package api.sweater.service.interfaces;

import api.sweater.model.Message;
import api.sweater.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    Page<Message> findAllPageable(Pageable pageable);

    void save(Message message);

    List<Message> findByAuthor(User author);

    void deleteById(Long id);

    Optional<Message> findById(Long id);

    Page<Message> findByTag(String tag, Pageable pageable);
}
