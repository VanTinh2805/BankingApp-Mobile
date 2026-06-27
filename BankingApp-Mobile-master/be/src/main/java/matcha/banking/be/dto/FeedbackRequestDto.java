package matcha.banking.be.dto;

import lombok.Data;

@Data
public class FeedbackRequestDto {
    private String suggestion;
    private Integer rating;
}
