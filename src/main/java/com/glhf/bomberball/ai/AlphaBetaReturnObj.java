package com.glhf.bomberball.ai;

import com.glhf.bomberball.utils.Action;

import java.util.List;

public class AlphaBetaReturnObj {
    public double score;
    public List<Action> actions;
    public String message;

    AlphaBetaReturnObj(double score, List<Action> actions,String message){
        this.score=score;
        this.actions=actions;
        this.message=message;
    }
}
