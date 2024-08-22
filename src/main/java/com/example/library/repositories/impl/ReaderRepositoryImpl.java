package com.example.library.repositories.impl;

import com.example.library.models.Reader;
import com.example.library.repositories.IReaderRepository;
import com.example.library.utils.DbConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ReaderRepositoryImpl implements IReaderRepository {
    private final DbConnect dbConnect = DbConnect.getInstance();

    @Override
    public ObservableList<Reader> getAllReaders() {
        ObservableList<Reader> readers = FXCollections.observableArrayList();

        String sql = """
                select r.*, u.username from readers r
                join users u on r.userId = u.userId
                where u.role = 'reader';
                """;

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            while (rs.next()) {
                Reader reader = Reader.builder()
                        .readerId(rs.getString("readerId"))
                        .readerName(rs.getString("readerName"))
                        .readerEmail(rs.getString("readerEmail"))
                        .readerPhone(rs.getString("readerPhoneNumber"))
                        .readerDOB(rs.getDate("readerDOB").toLocalDate())
                        .readerAddress(rs.getString("address"))
                        .isBlocked(rs.getBoolean("isBlock"))
                        .username(rs.getString("username"))
                        .build();
                readers.add(reader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return readers;
    }

    @Override
    public void save(Reader reader) {
        String sqlCheck = String.format("SELECT COUNT(readerId) FROM readers WHERE readerId = '%s';", reader.getReaderId());
        ResultSet rs = dbConnect.executeQuery(sqlCheck);


        boolean exists = false;
        try {
            exists = rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!exists) {
            String sqlInsert = String.format("""
                            INSERT INTO readers (readerId, readerName, readerEmail, readerPhoneNumber, readerDob, address, userId)
                            VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %d);
                            """, reader.getReaderId(),
                    reader.getReaderName(),
                    reader.getReaderEmail(),
                    reader.getReaderPhone(),
                    reader.getReaderDOB(),
                    reader.getReaderAddress(),
                    reader.getUserId()
            );

            dbConnect.executeUpdate(sqlInsert);
        } else {
            String sqlUpdate = String.format("""
                            UPDATE readers
                            SET readerName = '%s',
                                readerEmail = '%s',
                                readerPhoneNumber = '%s',
                                readerDOB = '%s',
                                address = '%s',
                                isBlock = %d
                            WHERE readerId = '%s';
                            """, reader.getReaderName(),
                    reader.getReaderEmail(),
                    reader.getReaderPhone(),
                    reader.getReaderDOB(),
                    reader.getReaderAddress(),
                    reader.isBlocked() ? 1 : 0,
                    reader.getReaderId()

            );

            dbConnect.executeUpdate(sqlUpdate);
        }
    }


    @Override
    public void delete(Reader reader) {
        String sql = String.format("""
                delete from readers where readerId = '%s';
                """, reader.getReaderId());

        dbConnect.executeUpdate(sql);

    }

    @Override
    public ObservableList<String> getAllReaderId() {
        ObservableList<String> result = FXCollections.observableArrayList();
        String sql = """
                select readerId from readers;
                """;

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            while (rs.next()) {
                result.add(rs.getString("readerId"));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    @Override
    public String getReaderNameById(String readerId) {
        String sql = String.format("""
                select readerName from readers where readerId = '%s';
                """, readerId);

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getString("readerName");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String getReaderIdByName(String readerName) {
        String sql = String.format(
                """
                        select readerId from readers where readerName = '%s';
                         """, readerName
        );

        ResultSet rs = dbConnect.executeQuery(sql);

        try {
            if (rs.next()) {
                return rs.getString("readerId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getTotalReader() {
        String sql = """
                select count(*) as total from readers;
                """;

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String getReaderId() {
        String sql = "select count(*) from readers";

        int id = 0;

        ResultSet rs = dbConnect.executeQuery(sql);

        try {
            if (rs.next()) {
                id = rs.getInt(1) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format("R%03d", id);
    }

    @Override
    public Optional<Reader> getReaderById(String id) {
        String sql = String.format("""
                select * from readers where readerId = '%s';
                """, id);

        ResultSet rs = dbConnect.executeQuery(sql);

        try {
            if (rs.next()) {
                return Optional.ofNullable(Reader.builder()
                        .readerId(rs.getString("readerId"))
                        .readerName(rs.getString("readerName"))
                        .readerEmail(rs.getString("readerEmail"))
                        .readerPhone(rs.getString("readerPhoneNumber"))
                        .readerDOB(rs.getDate("readerDOB").toLocalDate())
                        .readerAddress(rs.getString("address"))
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean isExistReaderPhoneNumber(String readerPhoneNumber) {
        String sql = String.format("""
                select count(*) from readers where readerPhoneNumber = '%s';
                """, readerPhoneNumber);

        ResultSet rs = dbConnect.executeQuery(sql);

        if (rs != null) {
            try {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean isExistReaderEmail(String readerEmail) {
        String sql = String.format("""
                select count(*) from readers where readerEmail = '%s';
                """, readerEmail);

        ResultSet rs = dbConnect.executeQuery(sql);

        if (rs != null) {
            try {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Reader getReaderByUsername(String username) {
        String sql = String.format("""
                select r.*
                from readers r
                         join library.users u on u.userId = r.userId
                                
                where u.username = '%s';
                                
                """, username);

        ResultSet rs = dbConnect.executeQuery(sql);

        try {
            if (rs.next()) {
                return Reader.builder()
                        .readerId(rs.getString("readerId"))
                        .readerName(rs.getString("readerName"))
                        .readerEmail(rs.getString("readerEmail"))
                        .readerPhone(rs.getString("readerPhoneNumber"))
                        .readerDOB(rs.getDate("readerDOB").toLocalDate())
                        .readerAddress(rs.getString("address"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public boolean existsByEmailAndNotId(String email, String id) {
        String sql = String.format("""
                SELECT COUNT(*) FROM readers WHERE readerEmail = '%s' AND readerId <> '%s';
                """, email, id);

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public boolean existsByPhoneAndNotId(String phone, String id) {
        String sql = String.format("""
                SELECT COUNT(*) FROM readers WHERE readerPhoneNumber = '%s' AND readerId <> '%s';
                """, phone, id);

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;

    }
}

