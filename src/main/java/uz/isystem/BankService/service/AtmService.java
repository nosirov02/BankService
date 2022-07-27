package uz.isystem.BankService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.isystem.BankService.exception.BadRequest;
import uz.isystem.BankService.model.Atm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

@Service
public class AtmService {
    @Autowired
    private JdbcConnection jdbcConnection;

    public Atm getAtm(Integer id) {
        return findAtm(id);
    }

    public Atm findAtm(Integer id) {
        try {
            Statement statement = jdbcConnection.getStatement();
            String Query = "SELECT * from atm where id = " + id;
            ResultSet resultSet = statement.executeQuery(Query);
            Atm atm = new Atm();
            while (resultSet.next()) {
                atm.setId(resultSet.getInt("id"));
                atm.setNumber(resultSet.getString("number"));
                atm.setPinCode(resultSet.getString("pincode"));
                atm.setAmount(resultSet.getDouble("amount"));
                atm.setAddress(resultSet.getString("address"));
                atm.setStatus(resultSet.getBoolean("status"));
                return atm;
            }
            throw new BadRequest("Atm not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String createAtm(Atm atm) {
        checkAtm(atm);
        atm.setAmount(50000.0);
        atm.setStatus(false);
        try {
            Statement statement = jdbcConnection.getStatement();
            String Query = "INSERT INTO atm (number, pincode, address, amount, status) values (" +
                    "'" + atm.getNumber() + "'," +
                    "'" + atm.getPinCode() + "'," +
                    "'" + atm.getAddress() + "'," +
                    "" + atm.getAmount() + "," +
                    "" + atm.getStatus() + ")";
            int i = statement.executeUpdate(Query);
            if (i == 0) {
                throw new BadRequest("Atm not added");
            }
            return "Atm added";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkAtm(Atm atm) {
        if (atm.getNumber().length() != 25) {
            throw new BadRequest("Atm number error");
        }
        if (String.valueOf(atm.getPinCode()).length() != 6) {
            throw new BadRequest("Atm pinCode error");
        }
        try {
            Statement statement = jdbcConnection.getStatement();
            String Query = "Select * from atm where number = '" + atm.getNumber() + "'";
            ResultSet resultSet = statement.executeQuery(Query);
            if (resultSet.next()) {
                throw new BadRequest("Atm already exist");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public String updateAtm(Integer id, Atm atm) {
        checkAtm(atm);
        findAtm(id);
        try {
            Statement statement = jdbcConnection.getStatement();
            String Query = "UPDATE atm set " +
                    "number = '" + atm.getNumber() + "'," +
                    "pincode = '" + atm.getPinCode() + "'," +
                    "address = '" + atm.getAddress() + "'" +
                    "where id = " + id;
            int i = statement.executeUpdate(Query);
            if (i == 0){
                throw new BadRequest("Atm not updated");
            }
            return "atm updated";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteAtm(Integer id) {
        findAtm(id);
        try {
            Statement statement = jdbcConnection.getStatement();
            String Query = "DELETE from atm where id = " + id;
            int i = statement.executeUpdate(Query);
            if (i == 0){
                throw new BadRequest("Atm not deleted");
            }
            return "atm deleted";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Atm> getAll() {
        try {
            Statement statement = jdbcConnection.getStatement();
            String Query = "SELECT * from atm" ;
            ResultSet resultSet = statement.executeQuery(Query);
            List<Atm> atmList = new LinkedList<>();
            while (resultSet.next()) {
                Atm atm = new Atm();
                atm.setId(resultSet.getInt("id"));
                atm.setNumber(resultSet.getString("number"));
                atm.setPinCode(resultSet.getString("pincode"));
                atm.setAmount(resultSet.getDouble("amount"));
                atm.setAddress(resultSet.getString("address"));
                atm.setStatus(resultSet.getBoolean("status"));
                atmList.add(atm);
            }
            return atmList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
