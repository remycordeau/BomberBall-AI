package com.glhf.bomberball.ai;

import com.glhf.bomberball.utils.Action;

public class checkGameResultReturnObject {
	MyArrayList<Action> actions;
	double alpha;
	double beta;
	String message;
	checkGameResultReturnObject(MyArrayList<Action> actions,double alpha,double beta,String message){
		this.actions=actions;
		this.alpha=alpha;
		this.beta=beta;
		this.message=message;
	}
}
