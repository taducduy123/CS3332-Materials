package com.example.library.repositories.impl;

import com.example.library.models.Account;
import com.example.library.models.Reader;
import com.example.library.repositories.IAccountRepository;
import com.example.library.utils.DbConnect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class AccountRepositoryImpl implements IAccountRepository {
    private final DbConnect dbConnect = DbConnect.getInstance();

    @Override
    public Optional<Account> getAccountAndRoleByUsername(String username) {
        String query = String.format("SELECT * FROM users WHERE username = '%s'", username);
        ResultSet rs = dbConnect.executeQuery(query);
        try {
            if (rs.next()) {
                return Optional.of(Account.builder()
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .role(rs.getString("role"))
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean isExistUsername(String username) {
        String query = String.format("SELECT COUNT(*) FROM users WHERE username = '%s'", username);

        ResultSet rs = dbConnect.executeQuery(query);
        try {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    @Override
    public void save(Account account) {
        String query = String.format("INSERT INTO users (username, password) VALUES ('%s', '%s')", account.getUsername(), account.getPassword());
        String queryUpdate = String.format("UPDATE users SET password = '%s' WHERE username = '%s'", account.getPassword(), account.getUsername());

        ResultSet rs = dbConnect.executeQuery(String.format("SELECT * FROM users WHERE username = '%s'", account.getUsername()));

        try {
            if (rs.next()) {
                dbConnect.executeUpdate(queryUpdate);
            } else {
                dbConnect.executeUpdate(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isBlocked(String username) {
        String query = String.format("""
                select r.isBlock
                from readers r
                         join library.users u on u.userId = r.userId
                where u.username = '%s';
                            
                """, username);

        ResultSet rs = dbConnect.executeQuery(query);
        try {
            if (rs.next()) {
                return rs.getBoolean("isBlock");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public Map<String, String> getAccountInfoByEmail(String email) {
        String query = String.format("""
                select u.username, u.password, u.role, r.readerId, r.readerName, r.readerEmail, r.readerPhoneNumber, r.readerDOB, r.address
                from library.users u
                         join library.readers r on u.userId = r.userId
                where r.readerEmail = '%s';
                """, email);

        ResultSet rs = dbConnect.executeQuery(query);
        try {
            if (rs.next()) {
                return Map.of(
                        "username", rs.getString("username"),
                        "readerName", rs.getString("readerName")

                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public Reader getInformation(String username) {
        String sql = String.format("""
                select r.* from readers r
                join library.users u on u.userId = r.userId
                where u.username = '%s';
                """, username);

        ResultSet rs = dbConnect.executeQuery(sql);

        try {
            if (rs.next()) {
                Reader reader = Reader.builder()
                        .readerId(rs.getString("readerId"))
                        .readerName(rs.getString("readerName"))
                        .readerEmail(rs.getString("readerEmail"))
                        .readerPhone(rs.getString("readerPhoneNumber"))
                        .readerDOB(rs.getDate("readerDOB").toLocalDate())
                        .readerAddress(rs.getString("address"))
                        .build();
                return reader;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public int getUserIdByUsername(String username) {
        String sql = String.format("""
                select userId from users where username = '%s'
                """, username);

        ResultSet rs = dbConnect.executeQuery(sql);

        try {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;

    }


}
