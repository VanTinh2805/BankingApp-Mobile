package matcha.banking.be.controller;

import lombok.RequiredArgsConstructor;
import matcha.banking.be.dto.FeedbackRequestDto;
import matcha.banking.be.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @GetMapping("/current")
    public ResponseEntity<Object> getCurrentUserFeedback(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(feedbackService.getCurrentUserFeedback(token.substring(7)));
    }

    @PostMapping
    public ResponseEntity<Object> createFeedback(@RequestHeader("Authorization") String token,
                                                 @RequestBody FeedbackRequestDto requestDto) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            return ResponseEntity.ok(feedbackService.createFeedback(token.substring(7), requestDto));
        } catch (IllegalArgumentException exception) {
            responseBody.put("error", exception.getMessage());
            return ResponseEntity.badRequest().body(responseBody);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteFeedback(@RequestHeader("Authorization") String token,
                                                 @PathVariable Integer id) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            feedbackService.deleteFeedback(token.substring(7), id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            responseBody.put("error", exception.getMessage());
            return ResponseEntity.badRequest().body(responseBody);
        }
    }
}
