package com.glhf.bomberball.ai;

import com.glhf.bomberball.utils.Action;

import java.util.List;

public class AlphaBetaReturnObj {
    public int score;
    public List<Action> actions;
    public List<List<Action>> actionsPossibles;
    public int badness;

    AlphaBetaReturnObj(int score, List<Action> actions,List<List<Action>> actionsPossibles,int badness){
        this.score=score;
        this.actions=actions;
        this.actionsPossibles=actionsPossibles;
        this.badness=badness;
    }
}
