package matcha.banking.be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Data
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "suggestion")
    private String suggestion;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "created_at")
    private LocalDateTime created;
}
