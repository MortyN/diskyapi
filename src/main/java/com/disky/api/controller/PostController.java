package com.disky.api.controller;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.PostControllerException;
import com.disky.api.Exceptions.ScoreCardException;
import com.disky.api.Exceptions.UserLinkException;
import com.disky.api.filter.PostFilter;
import com.disky.api.filter.UserLinkFilter;
import com.disky.api.model.*;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Utility;

import java.lang.invoke.WrongMethodTypeException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PostController {
    public static List<Post> getPost(PostFilter filter) throws PostControllerException {
        int psId = 1;
        Logger log = Logger.getLogger(String.valueOf(PostController.class));
        List<Post> postResults = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        String where = " WHERE 1=1 ";

        if (filter.getUser() != null && filter.getUser().getUserId() != 0L && !filter.isGetFromConnections()) {
            where += " AND posts.USER_ID = ? ";
        }

        if (filter.getType() != null && filter.getType() != 0) {
            where += " AND posts.POST_TYPE = ? ";
        }

        if (filter.getScoreCardId() != null && filter.getScoreCardId().getCardId() != 0l) {
            where += " AND posts.SCORE_CARD_LINK = ? ";
        }

        if(filter.isGetFromConnections() && filter.getUser() != null && filter.getUser().getUserId() != 0L){
            userIds = getUserRelations(filter.getUser());
            where += " AND posts.USER_ID in(" + Utility.listAsQuestionMarks(userIds)+ ") ";
        }

        String sql = " SELECT " + Post.getColumns() + " FROM posts " + where + " ORDER BY POSTED_TS desc; ";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){

            if (filter.getUser() != null && filter.getUser().getUserId() != 0L && !filter.isGetFromConnections()) {
                stmt.setLong(psId++, filter.getUser().getUserId());
            }

            if (filter.getType() != null && filter.getType() != 0) {
                stmt.setInt(psId++, filter.getType());
            }

            if (filter.getScoreCardId() != null && filter.getScoreCardId().getCardId() != 0l) {
                stmt.setLong(psId++, filter.getScoreCardId().getCardId());
            }

            if(filter.isGetFromConnections() && filter.getUser() != null && filter.getUser().getUserId() != 0L){
                for(Long userId : userIds){
                    stmt.setLong(psId++, userId);
                }
            }

            log.info("POST SQL : " + stmt.toString());
            ResultSet res = stmt.executeQuery();

            while (res.next()) {
                User user = UserController.getOne(new User(res.getLong("USER_ID")));
                Post post = new Post(
                        res.getLong("POST_ID"),
                        user,
                        res.getString("TEXT_MESSAGE"),
                        res.getInt("POST_TYPE"),
                        res.getLong("SCORE_CARD_LINK") == 0 ? null : new ScoreCard(res.getLong("SCORE_CARD_LINK")),
                        res.getTimestamp("POSTED_TS"),
                        res.getTimestamp("UPDATED_TS")
                );

                postResults.add(post);
                if(filter.getUser() != null){
                    post.setInteractions(getInteractions(post, filter.getUser()));
                    UserLinkFilter userLinkFilter = new UserLinkFilter();
                    userLinkFilter.setUser(filter.getUser());
                    post.getUser().setUserLinks(UserLinkController.getUserLinks(userLinkFilter));
                }else{
                    post.setInteractions(new Interactions(null));
                }
                if(post.getType() == 2 && post.getScoreCard() != null && post.getScoreCard().getCardId() != 0L){
                    post.setScoreCard(ScoreCardController.getOneScoreCard(post.getScoreCard().getCardId()));
                }
            }
            log.info("Successfully retrieved: " + postResults.size() + " posts.");
            return postResults;
        } catch (SQLException | GetUserException | UserLinkException | ScoreCardException e) {
            throw new PostControllerException(e.getMessage());
        }
    }

    private static Interactions getInteractions(Post post, User loggedInUser) throws PostControllerException {
        List<Interaction> interaction = new ArrayList<>();
        String sql = "SELECT * FROM post_interactions WHERE POST_ID = ? ";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){

            stmt.setLong(1, post.getPostId());
            ResultSet res = stmt.executeQuery();

            while(res.next()){
                interaction.add(new Interaction(
                        new Post(res.getLong("POST_ID")),
                        new User(res.getLong("USER_ID")),
                        1));
            }

            Boolean isLikedByUser = false;
            for(Interaction i : interaction){
                if(i.getUser().getUserId() == loggedInUser.getUserId()){
                    isLikedByUser = true;
                }
            }
            return new Interactions(isLikedByUser, interaction);

        } catch (SQLException e) {
            throw new PostControllerException(e.getMessage());
        }
    }

    public static void delete(Post post) throws PostControllerException {
        if(post.getPostId() == null) throw new PostControllerException("postId is required!");
        Logger log = Logger.getLogger(String.valueOf(PostController.class));
        String sql = "DELETE FROM post_interactions where POST_ID = ? ";
        String sql2 = "DELETE FROM posts WHERE POST_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement stmt2 = conn.prepareStatement(sql2);
        ){
            stmt.setLong(1, post.getPostId());
            stmt2.setLong(1, post.getPostId());
            stmt.executeUpdate();
            stmt2.executeUpdate();

        } catch (SQLException e) {
            throw new PostControllerException(e.getMessage());
        }
    };

    public static void create(Post post) throws PostControllerException {
        validateObject(post);
        int psId = 1;
        Logger log = Logger.getLogger(String.valueOf(PostController.class));

        if(post.getPostId() != null && post.getPostId() != null && !post.getPostId().equals(0l)){
            update(post);
            return;
        }
        if(post.getType() != 2){
            post.setScoreCard(null);
        }
        post.setPostedTs(new Timestamp(System.currentTimeMillis()));
        post.setUpdatedTs(new Timestamp(System.currentTimeMillis()));

        String sql = "INSERT INTO posts (USER_ID,TEXT_MESSAGE, POST_TYPE, SCORE_CARD_LINK, POSTED_TS, UPDATED_TS) values (?,?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){

            stmt.setLong(psId++, post.getUser().getUserId());
            stmt.setString(psId++, post.getMessage());
            stmt.setInt(psId++, post.getType());
            if(post.getScoreCard() == null || post.getScoreCard().getCardId() == null){
                stmt.setNull(psId++, Types.BIGINT);
            }
            else{
                stmt.setLong(psId++, post.getScoreCard().getCardId());
            }
            stmt.setTimestamp(psId++, post.getPostedTs());
            stmt.setTimestamp(psId++, post.getUpdatedTs());
            log.info(stmt.toString());
            log.info("Rows affected: " + stmt.executeUpdate());

            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next()){
                post.setPostId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new PostControllerException(e.getMessage());
        }
    }

    private static void validateObject(Post post) throws PostControllerException {
        if(post.getUser() == null || post.getUser().getUserId() == 0l)
            throw new PostControllerException("User is required!");
        if(post.getMessage() == null || post.getMessage() == "")
            throw new PostControllerException("Message is required!");
        if(post.getType() == 0)
            throw new PostControllerException("postType is required!");
    }

    private static int update(Post post) throws PostControllerException {
        if(post.getPostId() == null || post.getMessage() == null) throw new PostControllerException("PostId and post message is required!");
        Logger log = Logger.getLogger(String.valueOf(PostController.class));
        post.setUpdatedTs(new Timestamp(System.currentTimeMillis()));

        String sql = "UPDATE posts SET TEXT_MESSAGE = ?, UPDATED_TS = ? WHERE POST_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            int psId = 1;

            stmt.setString(psId++, post.getMessage());
            stmt.setTimestamp(psId++, post.getUpdatedTs());
            stmt.setLong(psId++, post.getPostId());

            int rowsAffected = stmt.executeUpdate();
            log.info("Rows affected: " + rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            throw new PostControllerException(e.getMessage());
        }
    }

    private static List<Long> getUserRelations(User user) throws PostControllerException {
        List<Long> userIds = new ArrayList<>();
        Logger log = Logger.getLogger(String.valueOf(WrongMethodTypeException.class));

        String sqlOne = "SELECT user_links.USER_ID_LINK1 AS userId FROM user_links WHERE user_links.USER_ID_LINK2 = ? AND user_links.TYPE = ?";
        String sqlTwo = "SELECT user_links.USER_ID_LINK2 AS userId FROM user_links WHERE user_links.USER_ID_LINK1 = ? AND user_links.TYPE = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(sqlOne);
             PreparedStatement stmt2 = conn.prepareStatement(sqlTwo);
        ){
            stmt1.setLong(1, user.getUserId());
            stmt1.setLong(2, UserLink.USER_LINK_TYPE_ACCEPTED);
            log.info(stmt1.toString());
            ResultSet res1 = stmt1.executeQuery();

            while (res1.next()) {
                userIds.add(res1.getLong("userId"));
            }

            stmt2.setLong(1, user.getUserId());
            stmt2.setLong(2, UserLink.USER_LINK_TYPE_ACCEPTED);
            log.info("SHARK: " + stmt2.toString());
            ResultSet res2 = stmt2.executeQuery();

            while (res2.next()) {
                userIds.add(res2.getLong("userId"));
            }
            userIds.add(user.getUserId());

        } catch (SQLException e) {
            throw new PostControllerException(e.getMessage());
        }
        return userIds;
    }

    public static Interaction interact(Interaction interaction) throws PostControllerException {
        if(alreadyInteracted(interaction)) {
            return deleteInteract(interaction);
        } else {
           return insertInteract(interaction);
        }
    }

    private static Interaction insertInteract(Interaction interaction) {
        Logger log = Logger.getLogger(String.valueOf(PostController.class));
        String sql = "INSERT INTO post_interactions (POST_ID, USER_ID, TYPE) values (?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setLong(1, interaction.getPost().getPostId());
            stmt.setLong(2, interaction.getUser().getUserId());
            stmt.setInt(3, 1);
            stmt.executeUpdate();
            log.info("One interaction inserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return interaction;
    }

    private static Interaction deleteInteract(Interaction interaction) throws PostControllerException {
        Logger log = Logger.getLogger(String.valueOf(PostController.class));
        String sql = "DELETE FROM post_interactions WHERE POST_ID = ? AND USER_ID = ? ;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setLong(1, interaction.getPost().getPostId());
            stmt.setLong(2, interaction.getUser().getUserId());
            stmt.executeUpdate();
            log.info("One interaction deleted");
        } catch (SQLException e) {
            throw new PostControllerException(e.getMessage());
        }
        return null;
    }

    private static boolean alreadyInteracted(Interaction interaction) throws PostControllerException {
        String sql = "SELECT * FROM post_interactions WHERE POST_ID = ? AND USER_ID = ? ";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setLong(1, interaction.getPost().getPostId());
            stmt.setLong(2, interaction.getUser().getUserId());
            ResultSet res1 = stmt.executeQuery();

            return res1.next();

        } catch (SQLException e) {
            throw new PostControllerException(e.getMessage());
        }
    }
}
