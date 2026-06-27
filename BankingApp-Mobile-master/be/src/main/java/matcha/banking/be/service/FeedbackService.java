package matcha.banking.be.service;

import lombok.RequiredArgsConstructor;
import matcha.banking.be.dao.FeedbackDao;
import matcha.banking.be.dto.FeedbackRequestDto;
import matcha.banking.be.entity.FeedbackEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackDao feedbackDao;
    private final UserService userService;

    public List<FeedbackEntity> getCurrentUserFeedback(String token) {
        String email = userService.getEmailfromToken(token);
        return feedbackDao.findByUserEmailOrderByCreatedDesc(email);
    }

    public FeedbackEntity createFeedback(String token, FeedbackRequestDto requestDto) {
        if (requestDto.getSuggestion() == null || requestDto.getSuggestion().trim().isEmpty()) {
            throw new IllegalArgumentException("Suggestion is required");
        }

        String email = userService.getEmailfromToken(token);
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setUserEmail(email);
        feedback.setSuggestion(requestDto.getSuggestion().trim());
        feedback.setRating(normalizeRating(requestDto.getRating()));
        feedback.setCreated(LocalDateTime.now());
        return feedbackDao.save(feedback);
    }

    public void deleteFeedback(String token, Integer id) {
        String email = userService.getEmailfromToken(token);
        FeedbackEntity feedback = feedbackDao.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Feedback not found")
        );
        if (!email.equals(feedback.getUserEmail())) {
            throw new IllegalArgumentException("Feedback does not belong to current user");
        }
        feedbackDao.delete(feedback);
    }

    private Integer normalizeRating(Integer rating) {
        if (rating == null) {
            return 0;
        }
        if (rating < 0) {
            return 0;
        }
        if (rating > 5) {
            return 5;
        }
        return rating;
    }
}
