package com.bracketcove.forgettery.models.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bracketcove.forgettery.models.objects.Todo;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Ryan on 27/07/2016.
 */
public class TodoDatabase {
    /**
     * These variables describe what our Database will look like, what the table will be called,
     * and what each column will be called. See the nested SQLiteOpenHelper class below to see them
     * in action. These variables may also be placed in a seperate "contract" class, as described in
     * the Android Developer guide "Saving Data in SQL Databases"
     *
     * @see <a href="https://developer.android.com/training/basics/data-storage/databases.html#DbHelper"</a>
     */
    private static final String TABLE_NAME = "todos";
    private static final String COLUMN_ENTRY_ID = "entry_id";
    private static final String COLUMN_TODO_CONTENT = "content";
    private static final String COLUMN_CREATION_DATE = "creation";
    private static final String COLUMN_REMINDER_DATE = "reminder";
    private static final String COLUMN_TODO_DATA = "data";

    /**
     * - DATABASE_VERSION is to be incremented up by 1 (we'll, I think it just needs to be a larger
     * number...but I don't see the point in testing that theory), each time we change the schema,
     * or structure of the Database.
     * - DATABASE_NAME is simply the file name of our Database. We can use the file name in our
     * Java code to check if a Databse exists or not. This will come in handy if you want to
     * "pre-load" data into your database.
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "todos.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private TodoDatabaseHelper helper;
    private static TodoDatabase database;

    public static TodoDatabase getInstance(Context c) {
        if (database == null) {
            database = new TodoDatabase(c);
        }
        return database;
    }


    private TodoDatabase(Context c) {
        helper = new TodoDatabaseHelper(c);
    }

    /**
     * Method which grabs all of the to do data in our database. This gets into some interesting
     * water, as you may be wondering what we can do once the number of to dos we return gets
     * large enough to cause some issues. I plan on revisiting this issue at some point.
     *
     * @return
     */
    public ArrayList getAllData() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Todo> result = new ArrayList<>();
        Gson gson = new Gson();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                Todo todo = gson.fromJson(
                        c.getString(
                                c.getColumnIndex(COLUMN_TODO_DATA)
                        ),
                        Todo.class);
                result.add(todo);
            }
            while (c.moveToNext());
        }

        c.close();
        db.close();
        return result;
    }

    /**
     * Why wouldn't we make two seperate methods to handle inserting or updating the data?
     * There's probably pros to that approach as well (for example, if we are certain that the new
     * to do is going to be added and not updated, we are wasting time parsing the database first),
     * but we need to consider that this approach allows us to have fewer methods and classes in
     * the rest of the program. I'm happy to hear opinions on this :)
     * @param todo either an existing to do which the user wishes to update, or a new to do which
     *             should be added to the database.
     * @return the result of our database update or insert operation
     */
    public long insertOrUpdateData(Todo todo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        ContentValues val = new ContentValues();

        Gson gson = new Gson();

        if (c.moveToFirst()) {
            do {
                //In English: If the current Cursor's item Creation Date matches the date of the
                // to do object we are trying to write, update that entry instead of creating a new
                //one.
                if (c.getString(c.getColumnIndex(COLUMN_CREATION_DATE))
                        .equals(todo.getTodoCreationDate())) {
                    val.put(COLUMN_TODO_CONTENT, todo.getTodoContent());
                    val.put(COLUMN_REMINDER_DATE, todo.getTodoReminderDate());
                    val.put(COLUMN_CREATION_DATE, todo.getTodoCreationDate());
                    val.put(COLUMN_TODO_DATA, gson.toJson(todo, Todo.class));
                    //selection and selectionArgs simple tell our db which rows we
                    //want to update. Notice how selectionArgs is an array. We could supply
                    //multiple column ids if we wish to.
                    String selection = COLUMN_ENTRY_ID + " LIKE ?";
                    String[] selectionArgs = {
                            String.valueOf(c.getString(c.getColumnIndex(COLUMN_ENTRY_ID)))
                    };
                    long id = db.update(TABLE_NAME, val, selection, selectionArgs);
                    c.close();
                    db.close();
                    return id;
                }
            }
            while (c.moveToNext());
        }

        //no match was found, therefore we should create a new entry.
        val.put(COLUMN_TODO_CONTENT, todo.getTodoContent());
        val.put(COLUMN_REMINDER_DATE, todo.getTodoReminderDate());
        val.put(COLUMN_CREATION_DATE, todo.getTodoCreationDate());
        val.put(COLUMN_TODO_DATA, gson.toJson(todo, Todo.class));

        long id = db.insert(TABLE_NAME, null, val);
        c.close();
        db.close();
        return id;
    }


    /**
     * pretty self-explanatory.
     * @param todo
     * @return
     */
    public long deleteTodo(Todo todo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(COLUMN_CREATION_DATE))
                        .equals(todo.getTodoCreationDate())
                        ) {
                    String selection = COLUMN_ENTRY_ID + " LIKE ?";
                    String[] selectionArgs = {
                            String.valueOf(c.getString(c.getColumnIndex(COLUMN_ENTRY_ID)))
                    };

                    long id = db.delete(TABLE_NAME, selection, selectionArgs);
                    c.close();
                    db.close();
                    return id;
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return -1;
    }

        /*------------------------------Database Helper------------------------*/

    private static class TodoDatabaseHelper extends SQLiteOpenHelper {
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TODO_CONTENT + TEXT_TYPE + COMMA_SEP +
                COLUMN_CREATION_DATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_REMINDER_DATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_TODO_DATA + TEXT_TYPE +
                " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Context context;

        /**
         * the null value passed is a CursorFactory object. CursorFactory is used when we wish
         * to pass in a custom sub-class of Cursor. When would we need to do that? I have no
         * ****ing idea...
         *
         * @param context - Self Explanatory
         */
        public TodoDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

}
