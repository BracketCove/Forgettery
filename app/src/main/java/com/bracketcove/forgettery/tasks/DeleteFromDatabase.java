package com.bracketcove.forgettery.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.bracketcove.forgettery.models.database.TodoDatabase;
import com.bracketcove.forgettery.models.objects.Todo;

/**
 * This class is responsible for making our Database delete stuff from a background thread.
 * Once the operation is complete, we fire a callback to the activity.
 * Created by Ryan on 06/08/2016.
 */
public class DeleteFromDatabase extends AsyncTask<Void, Void, Long> {
    private Todo todo;
    private Context context;

    private OnDeleteComplete onDeleteComplete;

    public interface OnDeleteComplete {
        void setQueryComplete(Long result);
    }

    public DeleteFromDatabase(Context context, Todo todo) {
        this.todo = todo;
        this.context = context;
    }

    public void setDeleteCompleteListener(OnDeleteComplete onDeleteComplete) {
        this.onDeleteComplete = onDeleteComplete;
    }

    @Override
    protected Long doInBackground(Void... params) {
        TodoDatabase database = TodoDatabase.getInstance(context);
        return database.deleteTodo(todo);
    }

    //Value returned from doInBackground is passed to onPostExecute
    @Override
    protected void onPostExecute(Long result) {
        onDeleteComplete.setQueryComplete(result);
    }
}
