package com.bracketcove.forgettery.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bracketcove.forgettery.R;
import com.bracketcove.forgettery.models.objects.Todo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Handles either the creation or updation (that's a word, I swear) of a to do object. Since either
 * operation can use the same layout, and fire the same callback, I don't see any reason to make a
 * new Fragment and divide the operations.
 */
public class FragmentCreateOrUpdateTodo extends Fragment {
    private static final String TODO = "TODO";

    private Todo todo;
    private EditText content, reminderDate;
    private TextView creationDate;
    private Button done;

    private FragmentUpdateOrCreateTodoCallback callback;

    public FragmentCreateOrUpdateTodo() {
    }

    /**
     * In the event that we wish to Edit an existing To do object, use this method to create
     * an instance of the fragment. Otherwise, see Fragment below.
     * @param todo this must be a to do object which already exists
     * @return Fragment Instance. Super useful explanation, amirite?
     */
    public static FragmentCreateOrUpdateTodo newInstance(Todo todo) {
        FragmentCreateOrUpdateTodo fragment = new FragmentCreateOrUpdateTodo();
        Bundle args = new Bundle();
        args.putParcelable(TODO, todo);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * When we wish to create a new to do object to be added to the database, we'll use this method
     * to get an instance of the fragment.
     * @return
     */
    public static FragmentCreateOrUpdateTodo newInstance() {
        FragmentCreateOrUpdateTodo fragment = new FragmentCreateOrUpdateTodo();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.todo = getArguments().getParcelable(TODO);
        } else {
            todo = new Todo("", getDate(), "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View v = inflater.inflate(R.layout.fragment_create_or_update_todo, container, false);
        content = (EditText) v.findViewById(R.id.edt_todo_content);
        reminderDate = (EditText) v.findViewById(R.id.edt_reminder_date);
        creationDate = (TextView) v.findViewById(R.id.lbl_todo_creation_date);
        done = (Button) v.findViewById(R.id.btn_done);

        return v;
    }

    /**
     * If To do object exists (i.e. was passed in as an argument), update text fields with data
     * from existing to do. Otherwise, create a new to do object.
     * @param savedInstanceState
     */
    public void onActivityCreated(Bundle savedInstanceState) {
        content.setText(todo.getTodoContent());
        creationDate.setText(todo.getTodoCreationDate());
        reminderDate.setText(todo.getTodoReminderDate());

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todo.setTodoContent(content.getText().toString());
                todo.setTodoReminderDate(reminderDate.getText().toString());
                callback.onDoneButtonClick(todo);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentUpdateOrCreateTodoCallback) {
            callback = (FragmentUpdateOrCreateTodoCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement callback");
        }
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss");
        String formattedDate = format.format(date);
        return formattedDate;
    }

    public interface FragmentUpdateOrCreateTodoCallback {
        void onDoneButtonClick(Todo todo);
    }
}