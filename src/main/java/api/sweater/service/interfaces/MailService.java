package api.sweater.service.interfaces;

public interface MailService {
    void send(String emailTo, String subject, String message);
}
