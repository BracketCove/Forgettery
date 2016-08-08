package com.bracketcove.forgettery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bracketcove.forgettery.R;
import com.bracketcove.forgettery.models.objects.Todo;

import java.util.ArrayList;
import java.util.List;

/**
 * Pretty standard RecyclerView Adapter, as far as I'm concerned.
 * Created by Ryan on 17/07/2016.
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.CustomViewHolder> {
    private LayoutInflater inflater;
    private List<Todo> listData;

    private ItemClickCallback itemClickCallback;

    public TodoAdapter (List<Todo> listData, Context c){
        inflater = LayoutInflater.from(c);
        this.listData = listData;
    }

    public interface ItemClickCallback {
        void onItemClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    @Override
    public TodoAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_todo, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Todo todo = listData.get(position);
        holder.todoContent.setText(todo.getTodoContent());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void setListData(ArrayList<Todo> todoList) {
        this.listData.clear();
        this.listData.addAll(todoList);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View container;
        TextView todoContent;

        public CustomViewHolder(View itemView) {
            super(itemView);

            todoContent = (TextView) itemView.findViewById(R.id.lbl_todo_content);
            container = itemView.findViewById(R.id.cont_todo_root);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickCallback.onItemClick(getAdapterPosition());
        }
    }
}
