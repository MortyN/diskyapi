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
                }
            }
            delete(existingConnection);
            log.info("Deleting existing connection");
            return null;
        }
    }
    public static void create(UserLink link) throws UserLinkException {
        Logger log = Logger.getLogger(String.valueOf(UserLinkController.class));
      try {
          link.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
          Connection conn = DatabaseConnection.getConnection();
          int psId = 1;

          String sql = "INSERT INTO user_links (USER_ID_LINK1, USER_ID_LINK2, TYPE ) values (?,?,?)";

          PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
        try {
            if (link.getUserLink1() == null || link.getUserLink1().getUserId() == 0L ||
                    link.getUserLink2() == null || link.getUserLink2().getUserId() == 0L)
                throw new UserLinkException("link1 and link2 must be given!");
            Logger log = Logger.getLogger(String.valueOf(UserLinkController.class));
            Connection conn = DatabaseConnection.getConnection();

            int psId = 1;
            String sql = "UPDATE user_links SET TYPE = ? WHERE USER_ID_LINK1 = ? and USER_ID_LINK2 = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(psId++, link.getType());
            stmt.setLong(psId++, link.getUserLink1().getUserId());
            stmt.setLong(psId++, link.getUserLink2().getUserId());

            log.info("Rows affected: " + stmt.executeUpdate());
            return stmt.executeUpdate();
        } catch (SQLException | UserLinkException e) {
            throw new UserLinkException(e.getMessage());
        }
    }
    public static void delete(UserLink link) throws UserLinkException {
        if (link.getUserLink1() == null || link.getUserLink1().getUserId() == 0L ||
                link.getUserLink2() == null || link.getUserLink2().getUserId() == 0L)
            throw new UserLinkException("link1 and link2 must be given when deleting link!");
        try {
            Logger log = Logger.getLogger(String.valueOf(UserLinkController.class));
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM user_links WHERE USER_ID_LINK1 = ? AND USER_ID_LINK2 = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setLong(1, link.getUserLink1().getUserId());
            stmt.setLong(2, link.getUserLink2().getUserId());

            stmt.executeUpdate();
        } catch (SQLException throwables) {
            throw new UserLinkException("Unable to delete userlink");
        }
    }

    protected static void deleteAll(User user) throws UserLinkException {
        Logger log = Logger.getLogger(String.valueOf(UserLinkController.class));
        if (user == null  || user.getUserId() == 0L) {
            throw new UserLinkException("User is required");
        }
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM user_links WHERE USER_ID_LINK1 = ? OR USER_ID_LINK2 = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

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
        try {
            List<UserLink> userLinkResult = new ArrayList<>();

            Connection conn = DatabaseConnection.getConnection();

            String where = "WHERE user_links.USER_ID_LINK1 = ? or user_links.USER_ID_LINK2 = ?";

            if (filter.getType() != null &&filter.getType() != 0) {
                where += " AND user_links.TYPE = ?";
            }

            if (filter.getFromTs() != null) {
                where += " AND user_links.CREATED_TS >= ? ";
            }

            if (filter.getToTs() != null) {
                where += " AND user_links.CREATED_TS <= ? ";
            }

            String sql = "SELECT " + UserLink.getColumns() + " FROM user_links " + where;
            PreparedStatement stmt = conn.prepareStatement(sql);

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

            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                User user_id_link1 = new User(res.getLong("USER_ID_LINK1")),
                        user_id_link2 = new User(res.getLong("USER_ID_LINK2"));
                if (Utility.nullOrEmpty(filter.getUser().getUserLinks())) {
                    user_id_link1 = UserController.getOne(new User(res.getLong("USER_ID_LINK1")));
                    user_id_link2 = UserController.getOne(new User(res.getLong("USER_ID_LINK2")));
                }
                UserLink link = new UserLink(
                        user_id_link1,
                        user_id_link2,
                        res.getInt("TYPE"),
                        res.getTimestamp("CREATED_TS")
                );
                userLinkResult.add(link);
            }

            log.info("Successfully retireved: " + userLinkResult.size() + " users.");
            return userLinkResult;
        } catch (SQLException | GetUserException e) {
            throw new UserLinkException(e.getMessage());
        }
    }
}