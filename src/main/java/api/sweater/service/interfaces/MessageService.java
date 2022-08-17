package api.sweater.service.interfaces;

import api.sweater.model.Message;
import api.sweater.model.User;
import api.sweater.model.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    Page<MessageDto> getMessages(String filter, PageRequest pageRequest, ModelAndView modelAndView, User user);

    Page<MessageDto> findAll(Pageable pageable, User user);

    List<Message> findAll();

    Page<MessageDto> findByAuthor(User author, User user, Pageable pageable);

    Page<MessageDto> findByTag(String tag, User user, Pageable pageable);

    void save(Message message);

    void deleteById(Long id);




}
