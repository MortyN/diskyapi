package com.disky.api.endpoint;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.PostControllerException;
import com.disky.api.Exceptions.ScoreCardException;
import com.disky.api.controller.PostController;
import com.disky.api.controller.ScoreCardController;
import com.disky.api.filter.PostFilter;
import com.disky.api.filter.ScoreCardFilter;
import com.disky.api.model.Post;
import com.disky.api.model.ScoreCard;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
@RequestMapping("/api/v1/scorecard")
@RestController
@CrossOrigin
public class ScoreCardEndpoint {
    @PostMapping("/create")
    public ScoreCard create(@RequestBody(required = true) ScoreCard scoreCard) throws ScoreCardException {
        ScoreCardController.create(scoreCard);
        return scoreCard;
    }

    @DeleteMapping()
    public static void delete(@RequestParam(required = true)  Long postId) throws PostControllerException {
        PostController.delete(new Post(postId));
    }

    @PostMapping("/get")
    public static List<ScoreCard> get(@RequestBody(required = true) ScoreCardFilter filter) throws ScoreCardException, SQLException, GetUserException {
        return ScoreCardController.getScoreCard(filter);
    }
}
