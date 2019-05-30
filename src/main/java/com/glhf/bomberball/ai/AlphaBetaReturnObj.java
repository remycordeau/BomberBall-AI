package com.glhf.bomberball.ai;

import com.glhf.bomberball.utils.Action;

import java.util.List;

public class AlphaBetaReturnObj {
    public int alpha;
    public int beta;
    public List<Action> actions;
    public List<List<Action>> actionsPossibles;

    AlphaBetaReturnObj(int alpha,int beta, List<Action> actions,List<List<Action>> actionsPossibles){
        this.alpha=alpha;
        this.beta=beta;
        this.actions=actions;
        this.actionsPossibles=actionsPossibles;
    }
}
