package uz.isystem.BankService.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCardToCard {
    private Integer fromId;
    private Integer toId;
    private Double amount;
    private Integer fromPassword;
}
