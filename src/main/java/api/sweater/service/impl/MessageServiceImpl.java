package api.sweater.service.impl;

import api.sweater.model.Message;
import api.sweater.model.User;
import api.sweater.model.dto.MessageDto;
import api.sweater.repository.interfaces.MessageRepository;
import api.sweater.service.interfaces.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Page<MessageDto> getMessages(String filter, PageRequest pageRequest, ModelAndView modelAndView, User user) {
        if (filter != null && !filter.isEmpty()) {
            modelAndView.addObject("filter", filter);
            return findByTag(filter, user, pageRequest);
        } else {
            return findAll(pageRequest, user);
        }
    }

    public Page<MessageDto> findAll(Pageable pageable, User user){
        return messageRepository.findAll(pageable, user);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Page<MessageDto> findByAuthor(User author, User user, Pageable pageable) {
        return messageRepository.findByAuthor(author, user, pageable);
    }

    @Override
    public Page<MessageDto> findByTag(String tag, User user, Pageable pageable) {
        return messageRepository.findByTag(tag, user, pageable);
    }

    public void save(Message message) {
        messageRepository.save(message);
    }

    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }


}
