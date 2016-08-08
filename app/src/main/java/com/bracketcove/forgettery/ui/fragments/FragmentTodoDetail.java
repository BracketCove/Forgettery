package com.bracketcove.forgettery.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bracketcove.forgettery.R;
import com.bracketcove.forgettery.models.objects.Todo;

/**
 * Nothing special. Just displays detailed contents of a to do object, and allows the user to edit
 * the object if they wish.
 */
public class FragmentTodoDetail extends Fragment {
    private static final String TODO = "TODO";

    private FragmentEditItemCallback callback;
    private TextView content, creationDate, reminderDate;
    private Button editTodo;
    private Todo todo;

    public FragmentTodoDetail() {
    }

    public static FragmentTodoDetail newInstance(Todo todo) {
        FragmentTodoDetail fragment = new FragmentTodoDetail();
        Bundle args = new Bundle();
        args.putParcelable(TODO, todo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.todo = getArguments().getParcelable(TODO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo_detail, container, false);

        content = (TextView) v.findViewById(R.id.lbl_todo_content);
        creationDate = (TextView) v.findViewById(R.id.lbl_todo_creation_date);
        reminderDate = (TextView) v.findViewById(R.id.lbl_todo_reminder_date);
        editTodo = (Button) v.findViewById(R.id.btn_edit_todo);

        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        content.setText(todo.getTodoContent());
        creationDate.setText(todo.getTodoCreationDate());
        reminderDate.setText(todo.getTodoReminderDate());
        editTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onEditButtonClick(todo);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * If you're like me and you were wondering where the f*** our Activity attaches a Listener to
     * our callback, it's in the method below. Some kind of wizardy.
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentEditItemCallback) {
            callback = (FragmentEditItemCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface FragmentEditItemCallback {
        void onEditButtonClick(Todo todo);
    }

}
