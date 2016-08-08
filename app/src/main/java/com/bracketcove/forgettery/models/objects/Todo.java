package com.bracketcove.forgettery.models.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This Class is intended to represent data which we can expect to be associated with a To Do object
 * Created by Ryan on 17/07/2016.
 */
public class Todo implements Parcelable {
    private String todoContent;
    private String todoCreationDate;
    private String todoReminderDate;

    /**
     * @param todoReminderDate - Date specified for App to remind user
     * @param todoCreationDate - Date which the to do was create
     * @param todoContent      - Description of what the to do is supposed to remind the user of
     */
    public Todo(String todoContent, String todoCreationDate, String todoReminderDate) {
        this.todoContent = todoContent;
        this.todoCreationDate = todoCreationDate;
        this.todoReminderDate = todoReminderDate;
    }

    protected Todo(Parcel in) {
        todoContent = in.readString();
        todoCreationDate = in.readString();
        todoReminderDate = in.readString();
    }

    public static final Creator<Todo> CREATOR = new Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

    public String getTodoContent() {
        return todoContent;
    }

    public void setTodoContent(String todoContent) {
        this.todoContent = todoContent;
    }

    public String getTodoCreationDate() {
        return todoCreationDate;
    }

    public void setTodoCreationDate(String todoCreationData) {
        this.todoCreationDate = todoCreationData;
    }

    public String getTodoReminderDate() {
        return todoReminderDate;
    }

    public void setTodoReminderDate(String todoReminderDate) {
        this.todoReminderDate = todoReminderDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(todoContent);
        parcel.writeString(todoCreationDate);
        parcel.writeString(todoReminderDate);
    }
}
