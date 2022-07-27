package uz.isystem.BankService.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JdbcConnection jdbcConnection;

    public Card getCard(Integer id) {
        return findCard(id);
    }

    public Card findCard(Integer id) {
        try {
            PreparedStatement preparedStatement = jdbcConnection
                    .getConnection().prepareStatement("SELECT * from card where id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Card card = new Card();
            if (resultSet.next()) {
                convertCard(resultSet, card);
                return card;
            }
            throw new BadRequest("Card not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String createCard(Card card) {
        checkCard(card);
        card.setStatus(false);
        card.setAmount(0.0);
        card.setDate(LocalDate.now().plusYears(5));
        try {
            PreparedStatement statement = jdbcConnection.getConnection()
                    .prepareStatement("INSERT INTO card (name, number, pincode, date, amount, status) values (?,?,?,?,?,?)");
            statement.setString(1, card.getName());
            statement.setString(2, card.getNumber());
            statement.setInt(3, card.getPinCode());
            statement.setString(4, String.valueOf(card.getDate()));
            statement.setDouble(5, card.getAmount());
            statement.setBoolean(6, card.getStatus());
            int i = statement.executeUpdate();
            if (i == 0) {
                throw new BadRequest("Card not created");
            }
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
            PreparedStatement statement = jdbcConnection.getConnection()
                    .prepareStatement("Select * from card where number = ?");
            statement.setString(1, card.getNumber());
            ResultSet set = statement.executeQuery();
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
            PreparedStatement statement = jdbcConnection.getConnection()
                    .prepareStatement("UPDATE card SET name = ?, pincode = ?, number = ? where id = ?");
            statement.setString(1, card.getName());
            statement.setInt(2, card.getPinCode());
            statement.setString(3, card.getNumber());
            statement.setInt(4, card.getId());
            int i = statement.executeUpdate();
            if (i == 0) {
                throw new BadRequest("Card not updated");
            }
            return "card updated";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteCard(Integer id) {
        findCard(id);
        try {
            PreparedStatement statement = jdbcConnection.getConnection()
                    .prepareStatement("DELETE FROM card  where id = ?");
            statement.setInt(1, id);
            int i = statement.executeUpdate();
            if (i == 0) {
                throw new BadRequest("Card not deleted");
            }
            return "card deleted";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Card> getAll() {
        try {
            PreparedStatement statement = jdbcConnection.getConnection().prepareStatement( "Select * from card");
            ResultSet resultSet = statement.executeQuery();
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
            PreparedStatement statement = jdbcConnection.getConnection()
                    .prepareStatement("Select * from card where number = ?");
            statement.setString(1, number);
            ResultSet resultSet = statement.executeQuery();
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
