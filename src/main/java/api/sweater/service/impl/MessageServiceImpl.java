package api.sweater.service.impl;

import api.sweater.model.Message;
import api.sweater.model.User;
import api.sweater.repository.interfaces.MessageRepository;
import api.sweater.service.interfaces.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Page<Message> findAllPageable(Pageable pageable){
        return messageRepository.findAll(pageable);
    }

    public void save(Message message) {
        messageRepository.save(message);
    }

    public List<Message> findByAuthor(User author) {
        return messageRepository.findByAuthor(author);
    }

    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public Page<Message> findByTag(String tag, Pageable pageable) {
        return messageRepository.findByTag(tag, pageable);
    }
}
