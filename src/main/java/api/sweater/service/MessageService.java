package api.sweater.service;

import api.sweater.model.Message;
import api.sweater.model.User;
import api.sweater.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void findFilteredMessages(String filter, List<Message> messages) {
        Iterable<Message> allMessages = messageRepository.findAll();
        String[] tags = filter.split(" ");
        for (Message message : allMessages) {
            String[] messageTags = message.getTag().split(" ");
            for (String tag :
                    tags) {
                for (String messageTag : messageTags) {
                    if (messageTag.equals(tag)) {
                        messages.add(message);
                    }
                }
            }
        }
    }

    public List<Message> findAll(){
        return (List<Message>) messageRepository.findAll();
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
}
