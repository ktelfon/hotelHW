package lv.home.reservation.reservation.http.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequest {

    @NotNull(message = "clientId is mandatory")
    private Long clientId;

    @NotNull(message = "roomId is mandatory")
    private Long roomId;

    @NotBlank(message = "from is mandatory")
    private String from;

    @NotBlank(message = "to is mandatory")
    private String to;

}
