package uz.isystem.BankService.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class Bank {
    private Integer id;
    private String name;
    private List<Atm> atmList;
    private List<Card> cardList;
    private Double amount;

    public Bank() {
        this.atmList = new LinkedList<>();
        this.cardList = new LinkedList<>();
    }
}
