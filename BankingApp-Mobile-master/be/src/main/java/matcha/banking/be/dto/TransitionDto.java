package matcha.banking.be.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TransitionDto implements Serializable {
    /** UID */
    @Serial
    private static final long serialVersionUID = 1L;

    private String sender;

    private String receiver;

    private Double amount;

}
