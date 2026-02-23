import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

public record Transaction(  
    @Id Long id,
    Long payerId,
    Long payeeId,
    BigDecimal amount,
    LocalDateTime createdAt){
}