package ru.koldaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.koldaev.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class UserService {

    private Connection connection;

    @Autowired
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean userExist(String login) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from usr where login = ?");
        ps.setString(1, login);
        ResultSet rs = ps.executeQuery();

        if (!rs.wasNull()) {
            return false;
        }
        System.out.println(rs.getString("login"));
        return true;
    }

    public boolean createUser
            (
                    String name,
                    String family,
                    String login,
                    String password
            ) throws SQLException {
        int nextId = -1;
        PreparedStatement nextIdString = connection.prepareStatement("SELECT nextval('usr_id_seq');");
        ResultSet resultSetId = nextIdString.executeQuery();

        PreparedStatement ps = connection.prepareStatement("insert into usr values(?, ?, ?, ?, ?)");

        if (resultSetId.next()) {
            nextId = resultSetId.getInt("nextval");
        }
        ps.setInt(1, nextId);
        ps.setString(2, login);
        ps.setString(3, password);
        ps.setString(4, name);
        ps.setString(5, family);
        ps.execute();
        return true;
    }

    public User updateUser
            (
                    User user,
                    String name,
                    String family,
                    String password
            ) throws SQLException {
        user.setName(name);
        user.setFamily(family);
        user.setPassword(password);
        String userLogin = user.getLogin();
        String query = "update usr set name = ?, family = ?, password = ? where login = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, name);
        ps.setString(2, family);
        ps.setString(3, password);
        ps.setString(4, userLogin);
        ps.execute();
        return user;
    }

    //Вернет пользователя, если такой существует и пароль совпадает с введенным
    public User getUserAfterLogin(String login, String password) throws SQLException {
        User user = null;
        PreparedStatement ps = connection.prepareStatement("select * from usr where login = ?");
        ps.setString(1, login);
        ResultSet userFromDb = ps.executeQuery();
        if (userFromDb.next()) {
            if (userFromDb.getString("password").equals(password)) {
                user = new User();
                user.setLogin(login);
                user.setPassword(password);
                user.setName(userFromDb.getString("name"));
                user.setFamily(userFromDb.getString("family"));
            }
        }
        return user;
    }

    public User getUserByLogin(String login) throws SQLException {
        User user = null;
        PreparedStatement ps = connection.prepareStatement("select * from usr where login = ?");
        ps.setString(1, login);
        ResultSet userFromDb = ps.executeQuery();
        if (userFromDb.next()) {
            user = new User();
            user.setLogin(login);
            user.setPassword(userFromDb.getString("password"));
            user.setName(userFromDb.getString("name"));
            user.setFamily(userFromDb.getString("family"));
        }
        return user;
    }
}
