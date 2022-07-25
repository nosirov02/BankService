package uz.isystem.BankService.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class  Card {
    private Integer id;
    private String number;
    private Integer pinCode;
    private String name;
    private LocalDate date;
    private Double amount;
    private Boolean status;
}
