package uz.isystem.BankService.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Atm {
    private Integer id;
    private String number;
    private Integer pinCode;
    private String address;
    private Double amount;
    private Boolean status;
}
