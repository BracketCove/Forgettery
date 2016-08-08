package com.bracketcove.forgettery.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bracketcove.forgettery.R;
import com.bracketcove.forgettery.adapters.TodoAdapter;
import com.bracketcove.forgettery.models.objects.Todo;

import java.util.ArrayList;

/**
 *This Fragment displays a series of to do objects in a RecyclerView. An ArrayList container such
 * objects is passed in as an argument, and then loaded into the Fragment's RecyclerView.
 * When an item is clicked in the RecyclerView,
 */
public class FragmentTodoList extends Fragment {
    private static final String LIST_DATA = "LIST_DATA";

    private ArrayList<Todo> listData;
    private FragmentItemClickCallback callback;
    private RecyclerView todoList;
    private Button addTodo;


    public FragmentTodoList() {
    }

    public static FragmentTodoList newInstance(ArrayList<Todo> listData) {
        FragmentTodoList fragment = new FragmentTodoList();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_DATA, listData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.listData = getArguments().getParcelableArrayList(LIST_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo_list, container, false);
        addTodo = (Button) v.findViewById(R.id.btn_add_todo);
        todoList = (RecyclerView)v.findViewById(R.id.lst_todos);
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        TodoAdapter adapter = new TodoAdapter(listData, getActivity());
        todoList.setAdapter(adapter);
        adapter.setItemClickCallback(new TodoAdapter.ItemClickCallback() {
            @Override
            public void onItemClick(int p) {
                callback.onListItemClicked(p);
            }
        });
        todoList.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper((createHelperCallback()));
        itemTouchHelper.attachToRecyclerView(todoList);

        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null){
                    callback.onAddTodoButtonClicked();
                }
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentItemClickCallback) {
            callback = (FragmentItemClickCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            //not used, as the first parameter above is 0
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                callback.onListItemSwiped(viewHolder.getAdapterPosition());
            }
        };
        return simpleItemTouchCallback;
    }

    public interface FragmentItemClickCallback {
        // TODO: Update argument type and name
        void onListItemSwiped(int position);
        void onListItemClicked(int position);
        void onAddTodoButtonClicked();
    }

}
