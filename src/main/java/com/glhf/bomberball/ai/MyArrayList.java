package com.glhf.bomberball.ai;

import com.glhf.bomberball.utils.Action;

import java.util.ArrayList;
import java.util.Iterator;

public class MyArrayList<T> extends ArrayList<T> {

    MyArrayList(){
        super();
    }

    public MyArrayList clone(){
        MyArrayList ret = new MyArrayList();
        Iterator<T> it = this.iterator();
        while (it.hasNext()){
            T obj = it.next();
            try {
                MyArrayList obj2 = ((MyArrayList) obj).clone();
            }catch (Exception e){
                T obj2 = obj;
            }
            ret.add(obj);
        }
        return ret;
    }
}
