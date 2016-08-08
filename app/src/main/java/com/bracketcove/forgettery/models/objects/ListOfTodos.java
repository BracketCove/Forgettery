package com.bracketcove.forgettery.models.objects;

import java.util.ArrayList;

/**
 * Convenience class for deserializing objects from JSON data. Probably not the best way of
 * solving the issue, so feel free to scream at me.
 * Created by Ryan on 07/08/2016.
 */
public class ListOfTodos {
    private ArrayList<Todo> todoArrayList;

    public ArrayList<Todo> getTodoArrayList(){
        return todoArrayList;
    }
}
