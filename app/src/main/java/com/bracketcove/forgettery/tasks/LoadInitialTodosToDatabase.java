package com.bracketcove.forgettery.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.bracketcove.forgettery.R;
import com.bracketcove.forgettery.models.database.TodoDatabase;
import com.bracketcove.forgettery.models.objects.ListOfTodos;
import com.bracketcove.forgettery.models.objects.Todo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Grabs the data from todos.json, and deserializes it into our ListOfTodos.java object.
 * Subsequent data is loaded into the database.
 * Created by Ryan on 07/08/2016.
 */
public class LoadInitialTodosToDatabase extends AsyncTask<Void,Void,Void> {
    private Context context;
    private OnDatabaseBuilt onDatabaseBuilt;

    public interface OnDatabaseBuilt {
        void buildComplete();
    }

    public LoadInitialTodosToDatabase (Context context){
        this.context = context;
    }

    public void setOnDatabaseBuilt(OnDatabaseBuilt onDatabaseBuilt) {
        this.onDatabaseBuilt = onDatabaseBuilt;
    }

    @Override
    protected Void doInBackground(Void... params) {
        TodoDatabase database = TodoDatabase.getInstance(context);

        InputStream raw = context.getResources().openRawResource(R.raw.todos);
        Reader reader = new BufferedReader(new InputStreamReader(raw));

        /*
         You might be wondering why I made the ListOfTodos class at all. The simple answer is I
          didn't know how to deserielize directly into a List without dicking around with
          TypeTokens. Feel free to give me shit about this as I'm pretty sure there's a better
          way to handle this situation.
         */
        ListOfTodos listOfTodos = new Gson().fromJson(reader, ListOfTodos.class);
        List<Todo> todoList = listOfTodos.getTodoArrayList();

        for (Todo item: todoList){
            database.insertOrUpdateData(item);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void param){
        onDatabaseBuilt.buildComplete();
    }
}
