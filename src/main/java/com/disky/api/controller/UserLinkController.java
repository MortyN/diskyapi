package com.disky.api.controller;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.UserLinkException;
import com.disky.api.filter.UserLinkFilter;
import com.disky.api.model.User;
import com.disky.api.model.UserLink;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserLinkController {

    public static UserLink toggleFriend(User senderUser, User recipientUser) throws UserLinkException {
        Logger log = Logger.getLogger(String.valueOf(UserLinkController.class));
        UserLinkFilter filter = new UserLinkFilter();
        filter.setUser(senderUser);
        List<UserLink> existingConnections = getUserLinks(filter);

        UserLink existingConnection = null;
        if(Utility.nullOrEmpty(existingConnections)){
            log.info("creating pending connection");
            UserLink newConnection = new UserLink(senderUser, recipientUser,UserLink.USER_LINK_TYPE_PENDING, null);
            create(newConnection);
            return newConnection;
        }
        else{
            for(UserLink link : existingConnections){
                if((link.getUserLink1().getUserId().equals(senderUser.getUserId()) && link.getUserLink2().getUserId().equals(recipientUser.getUserId()))
                 || (link.getUserLink2().getUserId().equals(senderUser.getUserId()) && link.getUserLink1().getUserId().equals(recipientUser.getUserId()))){
                    existingConnection = link;
                    break;
                }
            }
            if(existingConnection != null){
                log.info("Deleting existing connection");
                delete(existingConnection);

            }else{
                log.info("creating pending connection");
                UserLink newConnection = new UserLink(senderUser, recipientUser,UserLink.USER_LINK_TYPE_PENDING, null);
                create(newConnection);
                return newConnection;
            }
            return new UserLink(null,null,0,null);
        }
    }
    public static void create(UserLink link) throws UserLinkException {
        Logger log = Logger.getLogger(String.valueOf(UserLinkController.class));
          link.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
          int psId = 1;

          String sql = "INSERT INTO user_links (USER_ID_LINK1, USER_ID_LINK2, TYPE ) values (?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){
          stmt.setLong(psId++, link.getUserLink1().getUserId());
          stmt.setLong(psId++, link.getUserLink2().getUserId());
          stmt.setInt(psId++, link.getType());

          link.setUserLink1(UserController.getOne(link.getUserLink1()));
          link.setUserLink2(UserController.getOne(link.getUserLink2()));
          link.setCreatedTimeStamp(link.getCreatedTimeStamp());

          log.info("Rows affected: " + stmt.executeUpdate());
      } catch (SQLException | GetUserException e) {
          throw new UserLinkException(e.getMessage());
       }
    }

    public static int update(UserLink link) throws UserLinkException {
            if (link.getUserLink1() == null || link.getUserLink1().getUserId() == 0L ||
                    link.getUserLink2() == null || link.getUserLink2().getUserId() == 0L)
                throw new UserLinkException("link1 and link2 must be given!");
            Logger log = Logger.getLogger(String.valueOf(UserLinkController.class));

            int psId = 1;
            String sql = "UPDATE user_links SET TYPE = ? WHERE USER_ID_LINK1 = ? and USER_ID_LINK2 = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){

            stmt.setLong(psId++, link.getType());
            stmt.setLong(psId++, link.getUserLink1().getUserId());
            stmt.setLong(psId++, link.getUserLink2().getUserId());

            log.info("Rows affected: " + stmt.executeUpdate());
            return stmt.executeUpdate();
        } catch (SQLException  e) {
            throw new UserLinkException(e.getMessage());
        }
    }
    public static void delete(UserLink link) throws UserLinkException {
        if (link.getUserLink1() == null || link.getUserLink1().getUserId() == 0L ||
                link.getUserLink2() == null || link.getUserLink2().getUserId() == 0L)
            throw new UserLinkException("link1 and link2 must be given when deleting link!");
        String sql = "DELETE FROM user_links WHERE USER_ID_LINK1 = ? AND USER_ID_LINK2 = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setLong(1, link.getUserLink1().getUserId());
            stmt.setLong(2, link.getUserLink2().getUserId());

            stmt.executeUpdate();
        } catch (SQLException throwables) {
            throw new UserLinkException("Unable to delete userlink");
        }
    }

    protected static void deleteAll(User user) throws UserLinkException {
        if (user == null  || user.getUserId() == 0L) {
            throw new UserLinkException("User is required");
        }
        String sql = "DELETE FROM user_links WHERE USER_ID_LINK1 = ? OR USER_ID_LINK2 = ?";

        try ( Connection conn = DatabaseConnection.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql);)
            {

            stmt.setLong(1, user.getUserId());
            stmt.setLong(2, user.getUserId());

            stmt.executeUpdate();
        }catch (SQLException throwables) {
            throw new UserLinkException("Unable to delete userlink");
        }
    }

    public static List<UserLink> getUserLinks(UserLinkFilter filter) throws UserLinkException {
        Logger log = Logger.getLogger(String.valueOf(UserLinkController.class));
        int psId = 1;
        if(filter.getUser() == null) throw new UserLinkException("User is required");

        List<UserLink> userLinkResult = new ArrayList<>();

        String where = "WHERE (user_links.USER_ID_LINK1 = ? or user_links.USER_ID_LINK2 = ?) ";
        String join = " INNER JOIN users user_links1 ON user_links1.USER_ID = user_links.USER_ID_LINK1 " +
                " INNER JOIN users user_links2 ON user_links2.USER_ID = user_links.USER_ID_LINK2 ";

        if (filter.getType() != null &&filter.getType() != 0) {
            where += " AND user_links.TYPE = ?";
        }

        if (filter.getFromTs() != null) {
            where += " AND user_links.CREATED_TS >= ? ";
        }

        if (filter.getToTs() != null) {
            where += " AND user_links.CREATED_TS <= ? ";
        }

        String sql = "SELECT " + UserLink.getColumns() + " FROM user_links " + join + where;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);

        ){
            stmt.setLong(psId++, filter.getUser().getUserId());
            stmt.setLong(psId++, filter.getUser().getUserId());

            if (filter.getType() != null && filter.getType() != 0) {
                stmt.setInt(psId++, filter.getType());
            }

            if (filter.getFromTs() != null) {
                stmt.setDate(psId++, new Date(filter.getFromTs().getTime()));
            }

            if (filter.getToTs() != null) {
                stmt.setDate(psId++, new Date(filter.getToTs().getTime()));
            }

            log.info("Executing SQL: " + stmt.toString());

            try(ResultSet res = stmt.executeQuery();) {
                while (res.next()) {
                    UserLink link = new UserLink(
                            new User(
                                    res.getLong("USER_ID_LINK1"),
                                    res.getString("USER_LINKS1_USERNAME"),
                                    res.getString("USER_LINKS1_FIRST_NAME"),
                                    res.getString("USER_LINKS1_LAST_NAME"),
                                    res.getString("USER_LINKS1_PHONE_NUMBER"),
                                    "************",
                                    null,
                                    res.getString("USER_LINKS1_IMG_KEY")
                            ),
                            new User(
                                    res.getLong("USER_ID_LINK2"),
                                    res.getString("USER_LINKS2_USERNAME"),
                                    res.getString("USER_LINKS2_FIRST_NAME"),
                                    res.getString("USER_LINKS2_LAST_NAME"),
                                    res.getString("USER_LINKS2_PHONE_NUMBER"),
                                    "************",
                                    null,
                                    res.getString("USER_LINKS2_IMG_KEY")
                            ),
                            res.getInt("TYPE"),
                            res.getTimestamp("CREATED_TS")
                    );
                    userLinkResult.add(link);
                }
            }


            log.info("Successfully retireved: " + userLinkResult.size() + " users.");
            return userLinkResult;
        } catch (SQLException  e) {
            throw new UserLinkException(e.getMessage());
        }
    }
}