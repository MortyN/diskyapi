package com.disky.api.controller;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.UserLinkException;
import com.disky.api.filter.UserFilter;
import com.disky.api.filter.UserLinkFilter;
import com.disky.api.model.User;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Parse;
import jdk.jshell.spi.ExecutionControl;
import org.slf4j.event.Level;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserController {
//TODO: Fix transaction and internal transaction logic

    public static void delete(User user) throws  GetUserException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            Logger log = Logger.getLogger(String.valueOf(UserController.class));

            String sql = "DELETE FROM users WHERE USER_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setLong(1, user.getUserId());
            stmt.executeUpdate();

            UserLinkController.deleteAll(user);
        } catch (UserLinkException | SQLException e) {
            throw new GetUserException("Unabled to delete user");
        }
        //TODO: Slett fra alle andre tabeller også
    }

    public static void save(User user) throws GetUserException {
        Logger log = Logger.getLogger(String.valueOf(UserController.class));
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;

            if(user.getUserId() != 0L) {
                update(user);
                return;
            }

            String sql = "INSERT INTO users (USERNAME, FIRST_NAME, LAST_NAME, PHONE_NUMBER, PASSWORD) values (?,?,?,?,?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(psId++, user.getUserName());
            stmt.setString(psId++, user.getFirstName());
            stmt.setString(psId++, user.getLastName());
            stmt.setString(psId++, user.getPhoneNumber());
            stmt.setString(psId++, user.getPassword());

            log.info("Rows affected: " + stmt.executeUpdate());

        } catch (SQLException e) {
            throw new GetUserException(e.getMessage());
        }

    }

    private static void update(User user) throws GetUserException {
        Logger log = Logger.getLogger(String.valueOf(UserController.class));
        Connection conn = DatabaseConnection.getConnection();
       try {
           int psId = 1;

           String sql = "UPDATE users SET USERNAME = ?, FIRST_NAME = ?, LAST_NAME = ?, PHONE_NUMBER = ?, PASSWORD = ? WHERE USER_ID = ?";

           PreparedStatement stmt = conn.prepareStatement(sql);
           stmt.setString(psId++, user.getUserName());
           stmt.setString(psId++, user.getFirstName());
           stmt.setString(psId++, user.getLastName());
           stmt.setString(psId++, user.getPhoneNumber());
           stmt.setString(psId++, user.getPassword());
           stmt.setLong(psId++, user.getUserId());

           log.info("Rows affected: " + stmt.executeUpdate());
       } catch (SQLException throwables) {
           throw new GetUserException("Unable to update user");
       }
    }
    public static User getOne(User user) throws GetUserException {
        UserFilter filter = new UserFilter();
        filter.addUserIds(user.getUserId());
        return getOne(filter);
    }
    public static User getOne(UserFilter filter) throws GetUserException {
        List<User> users = get(filter);

        if(Parse.nullOrEmpty(users))
            return null;
        else if(users.size() > 1)
            throw new GetUserException("Expected one user, got " + users.size());

        return users.get(0);
    }

    public static List<User> get(UserFilter filter) throws GetUserException {
        Logger log = Logger.getLogger(String.valueOf(UserController.class));
        List<User> userResult = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();

       try {
           String where = "WHERE 1=1 ";

           if (!Parse.nullOrEmpty(filter.getUserIds())) {
               where += " AND users.USER_ID in ( " + Parse.listAsQuestionMarks(filter.getUserIds()) + ")";
           }

           if (!Parse.nullOrEmpty(filter.getUserNames())) {
               where += " AND users.USERNAME in ( " + Parse.listAsQuestionMarks(filter.getUserNames()) + ")";
           }

           if (!Parse.nullOrEmpty(filter.getFirstNames())) {
               where += " AND users.FIRST_NAME in ( " + Parse.listAsQuestionMarks(filter.getFirstNames()) + ")";
           }

           if (!Parse.nullOrEmpty(filter.getLastNames())) {
               where += " AND users.LAST_NAME in ( " + Parse.listAsQuestionMarks(filter.getLastNames()) + ")";
           }

           if (!Parse.nullOrEmpty(filter.getPhoneNumbers())) {
               where += " AND users.PHONE_NUMBER in ( " + Parse.listAsQuestionMarks(filter.getPhoneNumbers()) + ")";
           }
           String sql = "SELECT " + User.getColumns() + " FROM users " + where;
           PreparedStatement stmt = conn.prepareStatement(sql);
           int psId = 1;

           if (!Parse.nullOrEmpty(filter.getUserIds())) {
               for (Long id : filter.getUserIds()) {
                   stmt.setLong(psId++, id);
               }
           }

           if (!Parse.nullOrEmpty(filter.getUserNames())) {
               for (String userName : filter.getUserNames()) {
                   stmt.setString(psId++, userName);
               }
           }

           if (!Parse.nullOrEmpty(filter.getFirstNames())) {
               for (String firstName : filter.getFirstNames()) {
                   stmt.setString(psId++, firstName);
               }
           }

           if (!Parse.nullOrEmpty(filter.getLastNames())) {
               for (String lastName : filter.getLastNames()) {
                   stmt.setString(psId++, lastName);
               }
           }

           if (!Parse.nullOrEmpty(filter.getPhoneNumbers())) {
               for (String number : filter.getPhoneNumbers()) {
                   stmt.setString(psId++, number);
               }
           }
           log.info(stmt.toString());

           ResultSet res = stmt.executeQuery();
           while (res.next()) {
               User user = new User(
                       res.getLong("USER_ID"),
                       res.getString("USERNAME"),
                       res.getString("FIRST_NAME"),
                       res.getString("LAST_NAME"),
                       res.getString("PHONE_NUMBER"),
                       res.getString("PASSWORD")
               );

               userResult.add(user);

               if (filter.isGetUserLinks()) {
                   UserLinkFilter userLinkFilter = new UserLinkFilter();
                   userLinkFilter.setUser(user);
                   user.setUserLinks(UserLinkController.getUserLinks(userLinkFilter));
               }
           }
           log.info("Successfully retrieved: " + userResult.size() + " users.");
           return userResult;
       } catch (SQLException | UserLinkException e) {
           throw new GetUserException("Unable to get user");
       }
    }

    public static List<User> search(String keyword) throws GetUserException {
        Logger log = Logger.getLogger(String.valueOf(UserController.class));
        List<User> userResult = new ArrayList<>();
        boolean whereSet = false;
        Connection conn = DatabaseConnection.getConnection();

       try {
           String where = " WHERE ";


           if (keyword.startsWith("+") || keyword.matches("[0-9].*")) {
               where += " users.PHONE_NUMBER = ?";
               whereSet = true;
           }

           if (keyword.chars().allMatch(Character::isLetter)) {
               where += whereSet ? " OR users.FIRST_NAME like ? OR users.LAST_NAME like ?" : "users.FIRST_NAME like ? OR users.LAST_NAME like ?";
               whereSet = true;
           }

           String sql = "SELECT " + User.getColumns() + "FROM users " + where;
           PreparedStatement stmt = conn.prepareStatement(sql);
           int psId = 1;

           if (keyword.startsWith("+") || keyword.matches("[0-9].*")) {
               stmt.setString(psId++, keyword);
           }
           if (keyword.chars().allMatch(Character::isLetter)) {
               stmt.setString(psId++, keyword + "%");
               stmt.setString(psId++, keyword + "%");
           }

           log.info(stmt.toString());

           ResultSet res = stmt.executeQuery();
           while (res.next()) {
               User user = new User(
                       res.getLong("USER_ID"),
                       res.getString("USERNAME"),
                       res.getString("FIRST_NAME"),
                       res.getString("LAST_NAME"),
                       res.getString("PHONE_NUMBER"),
                       res.getString("PASSWORD")
               );

               userResult.add(user);
           }
           log.info("Successfully retireved: " + userResult.size() + " users.");
           return userResult;

       } catch (SQLException e) {
           throw new GetUserException("Unable to get user");
       }
    }

}
