package com.glhf.bomberball.ai;

import com.glhf.bomberball.utils.Action;

import java.util.List;

public class AlphaBetaReturnObj {
    public int score;
    public List<Action> actions;

    AlphaBetaReturnObj(int score, List<Action> actions){
        this.score=score;
        this.actions=actions;
    }
}
