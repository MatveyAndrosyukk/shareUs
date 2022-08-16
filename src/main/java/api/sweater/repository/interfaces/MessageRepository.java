package api.sweater.repository.interfaces;

import api.sweater.model.Message;
import api.sweater.model.User;
import api.sweater.model.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends PagingAndSortingRepository<Message, Long> {
    @Query("SELECT NEW api.sweater.model.dto.MessageDto(m, COUNT(ml), sum(CASE WHEN ml = :user THEN 1 ELSE 0 END) > 0) " +
            "FROM Message m LEFT JOIN m.likes ml " +
            "GROUP BY m")
    Page<MessageDto> findAll(Pageable pageable, @Param("user") User user);

    @Query("SELECT NEW api.sweater.model.dto.MessageDto(m, COUNT(ml), sum(CASE WHEN ml = :user THEN 1 ELSE 0 END) > 0) " +
            "FROM Message m LEFT JOIN m.likes ml " +
            "WHERE m.author = :author " +
            "GROUP BY m")
    Page<MessageDto> findByAuthor(@Param("author") User author, @Param("user") User user, Pageable pageable);

    @Query("SELECT NEW api.sweater.model.dto.MessageDto(m, COUNT(ml), sum(CASE WHEN ml = :user THEN 1 ELSE 0 END) > 0) " +
            "FROM Message m LEFT JOIN m.likes ml " +
            "WHERE m.tag = :tag " +
            "GROUP BY m")
    Page<MessageDto> findByTag(@Param("tag") String tag, @Param("user") User user, Pageable pageable);


}
