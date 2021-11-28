package com.disky.api.controller;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.UserImageUploadException;
import com.disky.api.Exceptions.UserLinkException;
import com.disky.api.filter.UserFilter;
import com.disky.api.filter.UserLinkFilter;
import com.disky.api.model.User;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Utility;
import com.disky.api.util.S3Util;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UserController {
    private static Map<Long, User> users = new HashMap<>();

    public static void delete(User user) throws  GetUserException {
        String sql = "DELETE FROM users WHERE USER_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setLong(1, user.getUserId());
            stmt.executeUpdate();
            UserLinkController.deleteAll(user);
        } catch (UserLinkException | SQLException e) {
            throw new GetUserException("Unabled to delete user");
        }
    }

    public static void save(User user, MultipartFile file) throws GetUserException {
        Logger log = Logger.getLogger(String.valueOf(UserController.class));
        String fileName = "";
        String fields= "";
        String values ="";

        try{
            if(user.getUserId() != null && user.getUserId() != 0L) {
                update(user,file);
                return;
            }
            if(file != null){
                SecureRandom random = new SecureRandom();
                fileName = new BigInteger(130, random).toString(32);
                S3Util.s3UploadPhoto(file, fileName);
                fields += ", IMG_KEY";
                values += ",?";
            }
        } catch (UserImageUploadException e) {
            throw new GetUserException(e.getMessage(), e);
        }

        String sql = "INSERT INTO users (USERNAME, FIRST_NAME, LAST_NAME, PHONE_NUMBER, PASSWORD, API_KEY" + fields +") values (?,?,?,?,?,?" + values + ")";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            int psId = 1;
            stmt.setString(psId++, user.getUserName());
            stmt.setString(psId++, user.getFirstName());
            stmt.setString(psId++, user.getLastName());
            stmt.setString(psId++, user.getPhoneNumber());
            stmt.setString(psId++, user.getPassword());
            stmt.setString(psId++, user.getApiKey());

            if(file!= null){
                stmt.setString(psId++, fileName);
            }

            log.info("Rows affected: " + stmt.executeUpdate());

        } catch (SQLException  e) {
            throw new GetUserException(e.getMessage());
        }
    }
    private static void update(User user, MultipartFile file) throws GetUserException, UserImageUploadException {
        String fileName = null;
        Logger log = Logger.getLogger(String.valueOf(UserController.class));
        String fields= "";

        if(file != null){
            SecureRandom random = new SecureRandom();
            fileName = new BigInteger(130, random).toString(32);
            S3Util.s3UploadPhoto(file, fileName);
            fields += ", IMG_KEY = ?";

            if(user.getImgKey() != null){
                S3Util.s3DeletePhoto(user.getImgKey());
            }
        }
        String sql = "UPDATE users SET FIRST_NAME = ?, LAST_NAME = ?, PHONE_NUMBER = ?" + fields + " WHERE USER_ID = ?";

       try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
       ) {
           int psId = 1;

           stmt.setString(psId++, user.getFirstName());
           stmt.setString(psId++, user.getLastName());
           stmt.setString(psId++, user.getPhoneNumber());
           if(fileName != null){
               stmt.setString(psId++, fileName);
           }
           stmt.setLong(psId++, user.getUserId());
           log.info(stmt.toString());

           log.info("Rows affected: " + stmt.executeUpdate());

           user.setImgKey(fileName);

           updateCache(user);
       } catch (SQLException  e) {
           log.warning("Failed to update image in DB, rollback upload.");
           S3Util.s3DeletePhoto(fileName);
           throw new GetUserException(e.getMessage());
       }
    }

    private static void updateCache(User user) {
        User cachedUser = users.get(user.getUserId());
        cachedUser.setUserLinks(user.getUserLinks());
        cachedUser.setImgKey(user.getImgKey());
        cachedUser.setUserName(user.getUserName());
        cachedUser.setFirstName(user.getFirstName());
        cachedUser.setLastName(user.getLastName());
        cachedUser.setPhoneNumber(user.getPhoneNumber());
    }

    protected static User getOne(User user) throws GetUserException {
        User cachedUser = users.get(user.getUserId());
        if(cachedUser != null) return cachedUser;

        UserFilter filter = new UserFilter();
        filter.addUserIds(user.getUserId());
        User rawUser = getOne(filter);
        if(rawUser != null){
            rawUser.setPassword("***********");
        }
        users.put(rawUser.getUserId(), rawUser);
        return rawUser;
    }

    public static User getOne(UserFilter filter) throws GetUserException {
        List<User> users = get(filter);

        if(Utility.nullOrEmpty(users))
            return null;
        else if(users.size() > 1)
            throw new GetUserException("Expected one user, got " + users.size());

        return users.get(0);
    }

    public static List<User> get(UserFilter filter) throws GetUserException {
        Logger log = Logger.getLogger(String.valueOf(UserController.class));
        List<User> userResult = new ArrayList<>();
        String where = "WHERE 1=1 ";

        if (!Utility.nullOrEmpty(filter.getUserIds())) {
            where += " AND users.USER_ID in ( " + Utility.listAsQuestionMarks(filter.getUserIds()) + ")";
        }

        if (!Utility.nullOrEmpty(filter.getUserNames())) {
            where += " AND users.USERNAME in ( " + Utility.listAsQuestionMarks(filter.getUserNames()) + ")";
        }

        if (!Utility.nullOrEmpty(filter.getFirstNames())) {
            where += " AND users.FIRST_NAME in ( " + Utility.listAsQuestionMarks(filter.getFirstNames()) + ")";
        }

        if (!Utility.nullOrEmpty(filter.getLastNames())) {
            where += " AND users.LAST_NAME in ( " + Utility.listAsQuestionMarks(filter.getLastNames()) + ")";
        }

        if (!Utility.nullOrEmpty(filter.getPhoneNumbers())) {
            where += " AND users.PHONE_NUMBER in ( " + Utility.listAsQuestionMarks(filter.getPhoneNumbers()) + ")";
        }
        String sql = "SELECT " + User.getColumns() + " FROM users " + where ;

       try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
       ){
           int psId = 1;

           if (!Utility.nullOrEmpty(filter.getUserIds())) {
               for (Long id : filter.getUserIds()) {
                   stmt.setLong(psId++, id);
               }
           }

           if (!Utility.nullOrEmpty(filter.getUserNames())) {
               for (String userName : filter.getUserNames()) {
                   stmt.setString(psId++, userName);
               }
           }

           if (!Utility.nullOrEmpty(filter.getFirstNames())) {
               for (String firstName : filter.getFirstNames()) {
                   stmt.setString(psId++, firstName);
               }
           }

           if (!Utility.nullOrEmpty(filter.getLastNames())) {
               for (String lastName : filter.getLastNames()) {
                   stmt.setString(psId++, lastName);
               }
           }

           if (!Utility.nullOrEmpty(filter.getPhoneNumbers())) {
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
                       res.getString("PASSWORD"),
                       res.getString("API_KEY"),
                       res.getString("IMG_KEY")
               );

               userResult.add(user);

               if (filter.isGetUserLinks()) {
                   UserLinkFilter userLinkFilter = new UserLinkFilter();
                   userLinkFilter.setUser(user);
                   user.setUserLinks(UserLinkController.getUserLinks(userLinkFilter));
               }
           }
           return userResult;
       } catch (SQLException | UserLinkException e) {
           throw new GetUserException("Unable to get user");
       }
    }

    public static List<User> search(String keyword) throws GetUserException {
        Logger log = Logger.getLogger(String.valueOf(UserController.class));
        List<User> userResult = new ArrayList<>();
        boolean whereSet = false;
        String where = " WHERE ";

        if (keyword.startsWith("+") || keyword.matches("[0-9].*")) {
            where += " users.PHONE_NUMBER = ?";
            whereSet = true;
        }

        if (keyword.chars().allMatch(Character::isLetter)) {
            where += whereSet ? " OR users.FIRST_NAME like ? OR users.LAST_NAME like ?" : "users.FIRST_NAME like ? OR users.LAST_NAME like ?";
        }

        String sql = "SELECT " + User.getColumns() + "FROM users " + where;
       try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
       ){

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
                       res.getString("PASSWORD"),
                       res.getString("API_KEY"),
                       res.getString("IMG_KEY")
               );
               userResult.add(user);
           }
           return userResult;

       } catch (SQLException e) {
           throw new GetUserException("Unable to get user");
       }
    }
    public static User authUser(String userName, String password) throws GetUserException {
        User loggedInUser = null;
        String select = "SELECT * FROM users WHERE USERNAME = ? AND PASSWORD = ? ";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(select);
        ){
            stmt.setString(1,userName);
            stmt.setString(2,password);

            ResultSet res = stmt.executeQuery();
            if(res.next()){
                loggedInUser = new User(
                        res.getLong("USER_ID"),
                        res.getString("USERNAME"),
                        res.getString("FIRST_NAME"),
                        res.getString("LAST_NAME"),
                        res.getString("PHONE_NUMBER"),
                        res.getString("PASSWORD"),
                        res.getString("API_KEY"),
                        res.getString("IMG_KEY")
                );
            }else if(loggedInUser == null){
                throw new GetUserException("Wrong combination");
            }
        } catch(SQLException e){
            throw new GetUserException(e.getMessage());
        }

        return loggedInUser;
    };
}
