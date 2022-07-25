package uz.isystem.BankService.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.isystem.BankService.exception.BadRequest;
import uz.isystem.BankService.model.Atm;
import uz.isystem.BankService.model.Bank;
import uz.isystem.BankService.model.Card;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class BankService {
    public final List<Bank> bankList;
    @Autowired
    public CardService cardService;
    @Autowired
    private AtmService atmService;
    private static int id;

    public BankService() {
        this.bankList = new LinkedList<>();
    }

    public Bank createBank(Bank bank) {
        bank.setId(++id);
        bank.setAmount(100000.0);
        bankList.add(bank);
        return bank;
    }

    public Bank getBank(Integer id) {
        return findBank(id);
    }

    public String addCard(Integer cardId, Integer bankId) {
        Bank bank = findBank(bankId);
        Card card = cardService.getCard(cardId);
        if (card.getStatus()) {
            throw new BadRequest("Card already active");
        }
        card.setStatus(true);
        bankruptBank(bank);
        bank.setAmount(bank.getAmount() - 5000);
        card.setAmount(5000.0);
        bank.getCardList().add(card);
        return "card added to bank";
    }


    public String addAtm(Integer atmId, Integer bankId) {
        Bank bank = findBank(bankId);
        Atm atm = atmService.getAtm(atmId);
        bank.getAtmList().add(atm);
        return "atm added to bank";
    }

    public Bank findBank(Integer id) {
        for (Bank bank : bankList) {
            if (bank.getId().equals(id)) {
                return bank;
            }
        }
        throw new BadRequest("Bank not found");
    }

    public Bank updateBank(Integer id, Bank bank) {
        return null;
    }

    public String deleteBank(Integer id) {
        return null;
    }

    public String paymentBankToCard(Integer bankId, Integer cardId, Double amount) {
        Bank bank = findBank(bankId);
        bankruptBank(bank);
        Card card = cardService.findCard(cardId);
        if (!card.getStatus()) {
            throw new BadRequest("Card is not active");
        }
        bank.setAmount(bank.getAmount() - amount);
        card.setAmount(card.getAmount() + amount);
        return "Successful payment";
    }

    public String startAtm(Integer atmId) {
        Atm atm = null;
        Bank bank = null;
        for (Bank b : bankList) {
            for (Atm a : b.getAtmList()) {
                if (a.getId().equals(atmId)) {
                    atm = a;
                    bank = b;
                }
            }
        }
        if (atm == null || bank == null) {
            throw new BadRequest("Atm not found");
        }
        if (atm.getStatus()) {
            throw new BadRequest("Atm is active");
        }

        bank.setAmount(bank.getAmount() - 50000);
        atm.setAmount(50000.0);
        atm.setStatus(true);
        return "Atm is activated";
    }

    public void bankruptBank(Bank bank) {
        if (bank.getAmount() < 5000) {
            throw new BadRequest("Bank is bankrupt");
        }
    }

    public String finishAtm(Integer atmId) {
        Atm atm = null;
        Bank bank = null;
        for (Bank b : bankList) {
            for (Atm a : b.getAtmList()) {
                if (a.getId().equals(atmId)) {
                    atm = a;
                    bank = b;
                }
            }
        }
        if (atm == null || bank == null) {
            throw new BadRequest("Atm not found");
        }
        if (!atm.getStatus()) {
            throw new BadRequest("Atm is not active");
        }
        bank.setAmount(bank.getAmount() + atm.getAmount());
        atm.setAmount(0.0);
        atm.setStatus(false);
        return "atm is inactivated";
    }

    public String paymentAtmToCard(Integer atmId, Integer cardId, Double amount) {
        Atm atm = atmService.findAtm(atmId);
        if (!atm.getStatus()) {
            throw new BadRequest("Atm offline");
        }
        Card card = cardService.findCard(cardId);
        if (!card.getStatus()) {
            throw new BadRequest("Card offline");
        }
        if (atm.getAmount() < amount) {
            throw new BadRequest("Atm amount is not available");
        }
        atm.setAmount(atm.getAmount() - amount);
        card.setAmount(card.getAmount() + amount);
        if (atm.getAmount() < 10000.0){
            finishAtm(atmId);
            startAtm(atmId);
        }
        return "Successful payment";

    }

   /* @Scheduled(cron = "48 18 * * * ?")
    public void test(){
        log.info("This is schedule");
    }*/

}
