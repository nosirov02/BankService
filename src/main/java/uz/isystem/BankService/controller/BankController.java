package uz.isystem.BankService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.isystem.BankService.model.Bank;
import uz.isystem.BankService.service.BankService;

@RestController
@RequestMapping("/bank")
public class BankController {
    @Autowired
    private BankService bankService;

    @PostMapping
    public ResponseEntity<?> createBank(@RequestBody Bank bank) {
        Bank result = bankService.createBank(bank);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBank(@PathVariable("id") Integer id) {
        Bank result = bankService.getBank(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBank(@PathVariable("id") Integer id,
                                        @RequestBody Bank bank) {
        Bank result = bankService.updateBank(id, bank);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable("id") Integer id){
        String result = bankService.deleteBank(id);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/add-card")
    public ResponseEntity<?> addCard(@RequestParam("card") Integer cardId, @RequestParam("bank") Integer bankId) {
        String result = bankService.addCard(cardId, bankId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/add-atm")
    public ResponseEntity<?> addAtm(@RequestParam("atm") Integer atmId, @RequestParam("bank") Integer bankId) {
        String result = bankService.addAtm(atmId, bankId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/card/payment")
    public ResponseEntity<?> paymentToCard(@RequestParam("bankId") Integer bankId,
                                     @RequestParam("cardId") Integer cardId,
                                     @RequestParam("amount") Double amount){
        String result = bankService.paymentBankToCard(bankId, cardId, amount);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/terminal/card/payment")
    public ResponseEntity<?> paymentTerminalToCard(@RequestParam("atmId") Integer atmId,
                                     @RequestParam("cardId") Integer cardId,
                                     @RequestParam("amount") Double amount){
        String result = bankService.paymentAtmToCard(atmId, cardId, amount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/start-atm/{id}")
    public ResponseEntity<?> startAtm(@PathVariable("id") Integer atmId){
        String result = bankService.startAtm(atmId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/finish-atm/{id}")
    public ResponseEntity<?> finishAtm(@PathVariable("id") Integer atmId){
        String result = bankService.finishAtm(atmId);
        return ResponseEntity.ok(result);
    }
}
