package com.example.library.repositories.impl;

import com.example.library.models.Borrow;
import com.example.library.repositories.IBorrowRepository;
import com.example.library.utils.DbConnect;
import com.example.library.utils.UserContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BorrowRepositoryImpl implements IBorrowRepository {
    private final DbConnect dbConnect = DbConnect.getInstance();

    @Override
    public ObservableList<Borrow> getBorrowByReaderId(String readerId) {
        ObservableList<Borrow> result = FXCollections.observableArrayList();
        String sql = String.format(
                """
                        select br.borrowId, b.bookName, r.readerName, br.borrowDate, br.returnDate, br.dueDate, br.status, br.bookId from borrow br
                        join library.books b on b.bookId = br.bookId
                        join library.readers r on r.readerId = br.readerId
                        where r.readerId = '%s';
                        """, readerId
        );

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            while (rs.next()) {
                result.add(Borrow.builder()
                        .borrowId(rs.getString("borrowId"))
                        .bookName(rs.getString("bookName"))
                        .readerName(rs.getString("readerName"))
                        .borrowDate(rs.getDate("borrowDate").toLocalDate())
                        .returnDate(rs.getDate("returnDate").toLocalDate())
                        .dueDate(rs.getString("dueDate"))
                        .status(rs.getString("status"))
                        .bookId(rs.getString("bookId"))
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ObservableList<Borrow> getAllBookBorrowed() {
        ObservableList<Borrow> result = FXCollections.observableArrayList();
        String sql = """
                select br.bookId, br.borrowId, b.bookName, r.readerName, br.borrowDate, br.returnDate, br.dueDate, br.status, r.readerId from borrow br
                        join library.books b on b.bookId = br.bookId
                        join library.readers r on r.readerId = br.readerId
                     
                """;

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            while (rs.next()) {
                result.add(Borrow.builder()
                        .borrowId(rs.getString("borrowId"))
                        .bookName(rs.getString("bookName"))
                        .readerName(rs.getString("readerName"))
                        .borrowDate(rs.getDate("borrowDate").toLocalDate())
                        .returnDate(rs.getDate("returnDate").toLocalDate())
                        .dueDate(rs.getString("dueDate"))
                        .status(rs.getString("status"))
                        .readerId(rs.getString("readerId"))
                        .bookId(rs.getString("bookId"))
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void returnBook(Borrow borrow) {
//        String sql = String.format(
//                """
//                        update borrow
//                        set dueDate = '%s'
//                        where borrowId = '%s';
//                        """, LocalDate.now(), borrowId
//        );

        String sql = String.format(
                """
                        update borrow
                        set dueDate = '%s'
                        where bookId = '%s' and readerId = '%s' and dueDate is null;
                        """, LocalDate.now(), borrow.getBookId(), borrow.getReaderId()
        );

        dbConnect.executeUpdate(sql);
    }

    @Override
    public void save(Borrow borrow) {
        String bookId = borrow.getBookName();
        String sql = String.format(
                """
                        insert into borrow( bookId, readerId, borrowDate, returnDate)
                        values ( '%s', '%s', '%s', '%s');
                        """, bookId, borrow.getReaderName(), LocalDate.now(), borrow.getReturnDate()
        );

        dbConnect.executeUpdate(sql);
    }

    @Override
    public void requestBorrow(String bookId, LocalDate returnDate) {
        String sql = String.format("""
                insert into borrow(bookId, readerId, borrowDate, returnDate, status)
                values('%s','%s','%s','%s','%s');
                """, bookId, UserContext.getInstance().getReaderId(), LocalDate.now(), returnDate, "REQUEST");

        dbConnect.executeUpdate(sql);
    }


    @Override
    public int getTotalBorrow() {
        String sql = """
                select count(*) as total from borrow;
                """;

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int getTotalLate() {
        String sql = """
                select count(*) as total from borrow where returnDate < current_date();
                """;

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int getTotalReturn() {
        String sql = """
                SELECT COUNT(*) AS total_books_returned_on_time
                FROM borrow
                WHERE dueDate IS NOT NULL
                  AND dueDate <= returnDate;
                           
                                """;
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

    @Override
    public int getTotalBorrowByReaderId(String readerId) {
        String sql = String.format(
                """
                        select count(*) as total from borrow where readerId = '%s' and dueDate is null;
                        """, readerId
        );

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public boolean isReaderLate(String readerId) {
        String sql = String.format("""
                select count(*) from borrow
                where dueDate is null  and returnDate < now() and readerId = '%s' and  status is null;
                """, readerId);
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

    @Override
    public void approveRequest(List<String> borrowIds) {
        String sql = """
                update borrow
                set status = null
                where borrowId in (%s);
                """.formatted(String.join(",", borrowIds));
        dbConnect.executeUpdate(sql);
    }

    @Override
    public void deleteRequest(List<String> borrowId) {
        String sql = """
                delete from borrow
                where borrowId in (%s);
                """.formatted(String.join(",", borrowId));
        dbConnect.executeUpdate(sql);
    }


    @Override
    public List<String> getAllEmailByBorrowIds(List<String> borrowIds) {
        String sql = """
                SELECT DISTINCT r.readerEmail
                FROM borrow b
                         JOIN readers r ON b.readerId = r.readerId
                WHERE b.borrowId IN (%s)
                """.formatted(String.join(",", borrowIds));

        ResultSet rs = dbConnect.executeQuery(sql);
        List<String> emails = new ArrayList<>();
        try {
            while (rs.next()) {
                emails.add(rs.getString("readerEmail"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return emails;
    }

    @Override
    public List<String> getAllBookIdByBorrowId(List<String> borrowId) {
        String sql = """
                select bookId from borrow where borrowId in (%s);
                """.formatted(String.join(",", borrowId));
        ResultSet rs = dbConnect.executeQuery(sql);
        List<String> bookIds = new ArrayList<>();
        try {
            while (rs.next()) {
                bookIds.add(rs.getString("bookId"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookIds;
    }

    @Override
    public boolean isAlreadyRequest(String readerId, String bookId) {
        String sql = String.format("""
                select count(*) from borrow
                where bookId = '%s' and readerId = '%s' and dueDate is null;
                """, bookId, readerId);

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

    @Override
    public Map<String, String> getAllEmailWithMessagesByBorrowIds(List<String> borrowIds) {
        String sql = """
                SELECT r.readerEmail, bks.bookName, br.status, br.borrowDate, bks.bookId
                FROM borrow br
                         JOIN readers r ON br.readerId = r.readerId
                         JOIN books bks ON br.bookId = bks.bookId
                WHERE br.borrowId IN (%s)
                """.formatted(borrowIds.stream().map(id -> "'" + id + "'").collect(Collectors.joining(",")));

        ResultSet rs = dbConnect.executeQuery(sql);
        Map<String, Map<String, List<Map<String, String>>>> emailBooksStatusMap = new HashMap<>();

        try {
            while (rs != null && rs.next()) {
                String email = rs.getString("readerEmail");
                String bookName = rs.getString("bookName");
                String status = rs.getString("status");
                String bookId = rs.getString("bookId");
                String requestDate = rs.getString("borrowDate");

                // Create a map for book details
                Map<String, String> bookDetails = new HashMap<>();
                bookDetails.put("bookId", bookId);
                bookDetails.put("bookName", bookName);
                bookDetails.put("requestDate", requestDate);

                // Add the book details to the list associated with the email and status
                emailBooksStatusMap
                        .computeIfAbsent(email, k -> new HashMap<>())
                        .computeIfAbsent(status, k -> new ArrayList<>())
                        .add(bookDetails);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> emailMessages = new HashMap<>();
        for (Map.Entry<String, Map<String, List<Map<String, String>>>> entry : emailBooksStatusMap.entrySet()) {
            String email = entry.getKey();
            Map<String, List<Map<String, String>>> booksByStatus = entry.getValue();

            StringBuilder message = new StringBuilder();
            message.append("<div style='font-family: Arial, sans-serif; padding: 10px;'>");

            // Append approved books table if they exist
            if (booksByStatus.containsKey("REQUEST")) {
                List<Map<String, String>> approvedBooks = booksByStatus.get("REQUEST");
                message.append("<h2 style='color: #4CAF50;'>Your request has been approved!</h2>");
                message.append("<p>Please come to the library to get the following books:</p>");
                message.append("<table style='border-collapse: collapse; width: 100%;'>");
                message.append("<tr style='background-color: #f2f2f2;'><th style='border: 1px solid #ddd; padding: 8px;'>Book ID</th><th style='border: 1px solid #ddd; padding: 8px;'>Book Name</th><th style='border: 1px solid #ddd; padding: 8px;'>Request Date</th></tr>");

                for (Map<String, String> book : approvedBooks) {
                    message.append("<tr>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(book.get("bookId")).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(book.get("bookName")).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(book.get("requestDate")).append("</td>")
                            .append("</tr>");
                }

                message.append("</table>");
            }

            // Append declined books table if they exist
            if (booksByStatus.containsKey("DECLINE")) {
                List<Map<String, String>> declinedBooks = booksByStatus.get("DECLINE");
                message.append("<h2 style='color: #FF0000;'>Your request has been declined!</h2>");
                message.append("<p>The following books have been declined:</p>");
                message.append("<table style='border-collapse: collapse; width: 100%;'>");
                message.append("<tr style='background-color: #f2f2f2;'><th style='border: 1px solid #ddd; padding: 8px;'>Book ID</th><th style='border: 1px solid #ddd; padding: 8px;'>Book Name</th><th style='border: 1px solid #ddd; padding: 8px;'>Request Date</th></tr>");

                for (Map<String, String> book : declinedBooks) {
                    message.append("<tr>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(book.get("bookId")).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(book.get("bookName")).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(book.get("requestDate")).append("</td>")
                            .append("</tr>");
                }

                message.append("</table>");
            }

            message.append("<p>Thank you for using our library services!</p>");
            message.append("</div>");

            emailMessages.put(email, message.toString());
        }

        return emailMessages;
    }


    @Override
    public void declineRequest(List<String> borrowIds) {
        String sql = """
                update borrow
                set status = 'DECLINE'
                where borrowId in (%s);
                """.formatted(String.join(",", borrowIds));
        dbConnect.executeUpdate(sql);
    }

    @Override
    public void updateBorrowDate(List<String> borrowId) {
        String sql = String.format("""
                update borrow
                set borrowDate = '%s'
                where borrowId in (%s);
                """, LocalDate.now(), String.join(",", borrowId));

        dbConnect.executeUpdate(sql);
    }
}
