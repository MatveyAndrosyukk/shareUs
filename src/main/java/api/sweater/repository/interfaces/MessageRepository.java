package api.sweater.repository.interfaces;

import api.sweater.model.Message;
import api.sweater.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MessageRepository extends PagingAndSortingRepository<Message, Long> {
    List<Message> findByAuthor(User author);

    Page<Message> findByTag(String tag, Pageable pageable);
}
