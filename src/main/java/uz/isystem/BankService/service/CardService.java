package uz.isystem.BankService.service;

import org.springframework.stereotype.Component;
import uz.isystem.BankService.exception.BadRequest;
import uz.isystem.BankService.model.Card;
import uz.isystem.BankService.model.PaymentCardToCard;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Component
public class CardService {
    private String URL = "jdbc:postgresql://localhost:5432/isystem_db";
    private String username = "postgres";
    private String password = "root";

    Connection connection;


    public CardService() {
        try {
            connection = DriverManager.getConnection(URL, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Card getCard(Integer id) {
        return findCard(id);
    }

    public Card findCard(Integer id) {
        try {
            Statement statement = connection.createStatement();
            String Query = "Select * from card where id = " + id;
            ResultSet resultSet = statement.executeQuery(Query);
            Card card = new Card();
            while (resultSet.next()) {
                convertCard(resultSet, card);
            }
            if (card.getId() == null) {
                throw new BadRequest("Card not found");
            }
            return card;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String createCard(Card card) {
        checkCard(card);
        try {
            Statement statement = connection.createStatement();
            card.setStatus(false);
            card.setAmount(0.0);
            card.setDate(LocalDate.now().plusYears(5));
            String Query = "INSERT INTO card (name, number, pincode, date, amount, status) values " +
                    "('" + card.getName() + "', " +
                    "'" + card.getNumber() + "', " + card.getPinCode() + ", " +
                    "'" + card.getDate() + "', " + card.getAmount() + ", " + card.getStatus() + ")";
            boolean result = statement.execute(Query);
            System.out.println(result);
            return "Card added";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkCard(Card card) {
        if (card.getNumber().length() != 16) {
            throw new BadRequest("Card number error");
        }
        if (String.valueOf(card.getPinCode()).length() != 4) {
            throw new BadRequest("Card pinCode error");
        }
        if (card.getName().length() > 25) {
            card.setName(card.getName().substring(0, 25));
        }
        try {
            Statement statement = connection.createStatement();
            String Query = "Select * from card where number = '" + card.getNumber() + "'";
            ResultSet set = statement.executeQuery(Query);
            if (set.next()) {
                throw new BadRequest("card is present");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String updateCard(Integer id, Card card) {
        checkCard(card);
        findCard(id);
        try {
            Statement statement = connection.createStatement();
            String Query = "UPDATE card SET " +
                    "name = '" + card.getName() + "', " +
                    "pincode = " + card.getPinCode() + "," +
                    "number = '" + card.getNumber() + "' " +
                    "where id = " + id;
            int i = statement.executeUpdate(Query);
            System.out.println(i);
            return "card updated";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteCard(Integer id) {
        try {
            Statement statement = connection.createStatement();
            String Query = "DELETE from card where id = " + id;
            int i = statement.executeUpdate(Query);
            if (i == 0) {
                throw new BadRequest("Card not found");
            }
            return "Card deleted";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Card> getAll() {
        try {
            Statement statement = connection.createStatement();
            String Query = "Select * from card";
            ResultSet resultSet = statement.executeQuery(Query);
            List<Card> cardList = new LinkedList<>();
            while (resultSet.next()) {
                Card card = new Card();
                convertCard(resultSet, card);
                cardList.add(card);
            }
            if (cardList.isEmpty()) {
                throw new BadRequest("Cards not found");
            }
            return cardList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void convertCard(ResultSet resultSet, Card card) throws SQLException {
        card.setId(resultSet.getInt("id"));
        card.setName(resultSet.getString("name"));
        card.setNumber(resultSet.getString("number"));
        card.setPinCode(resultSet.getInt("pincode"));
        card.setDate(LocalDate.parse(resultSet.getString("date")));
        card.setAmount(resultSet.getDouble("amount"));
        card.setStatus(resultSet.getBoolean("status"));
    }

    public String payment(PaymentCardToCard payment) {
        Card fromCard = findCard(payment.getFromId());
        if (!fromCard.getStatus()) {
            throw new BadRequest("Card is not active");
        }
        if (!fromCard.getPinCode().equals(payment.getFromPassword())) {
            throw new BadRequest("Password error");
        }
        Card toCard = findCard(payment.getToId());
        if (!toCard.getStatus()) {
            throw new BadRequest("Card is not active");
        }
        if (fromCard.getAmount() < payment.getAmount()) {
            throw new BadRequest("First card amount invalid");
        }
        fromCard.setAmount(fromCard.getAmount() - payment.getAmount());
        toCard.setAmount(toCard.getAmount() + payment.getAmount());
        return "Successful payment";
    }

    public Card getByNumber(String number) {
        try {
            Statement statement = connection.createStatement();
            String Query = "Select * from card where number = '" + number + "'";
            ResultSet resultSet = statement.executeQuery(Query);
            Card card = new Card();
            while (resultSet.next()) {
                convertCard(resultSet, card);
            }
            if (card.getId() == null) {
                throw new BadRequest("Card not found");
            }
            return card;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
