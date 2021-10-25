package com.disky.api.filter;
import com.disky.api.model.ArenaRoundHole;
import com.disky.api.model.ScoreCardMember;
import com.disky.api.util.Parse;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ScoreCardResultControllerFilter {
    private ScoreCardMember scoreCardMember;
    private ArenaRoundHole arenaRoundHole;
    private Integer scoreValue;

}
