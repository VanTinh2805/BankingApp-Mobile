package matcha.banking.be.dao;

import matcha.banking.be.entity.FeedbackEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FeedbackDao extends CrudRepository<FeedbackEntity, Integer> {
    List<FeedbackEntity> findByUserEmailOrderByCreatedDesc(String userEmail);
}
