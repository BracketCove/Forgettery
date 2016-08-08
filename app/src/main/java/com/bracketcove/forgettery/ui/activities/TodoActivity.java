package com.bracketcove.forgettery.ui.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bracketcove.forgettery.R;
import com.bracketcove.forgettery.models.objects.Todo;
import com.bracketcove.forgettery.tasks.DeleteFromDatabase;
import com.bracketcove.forgettery.tasks.LoadInitialTodosToDatabase;
import com.bracketcove.forgettery.tasks.ReadFromDatabase;
import com.bracketcove.forgettery.tasks.WriteToDatabase;
import com.bracketcove.forgettery.ui.fragments.FragmentCreateOrUpdateTodo;
import com.bracketcove.forgettery.ui.fragments.FragmentTodoDetail;
import com.bracketcove.forgettery.ui.fragments.FragmentTodoList;

import java.io.File;
import java.util.ArrayList;

/**
 * Our Activity has two primary purposes. Managing the three Fragments which make up the UI of the
 * Application, and making the appropriate calls to our Database Asynctasks when we wish to
 * manipulate the Database.
 */
public class TodoActivity extends AppCompatActivity implements FragmentTodoList.FragmentItemClickCallback,
        FragmentTodoDetail.FragmentEditItemCallback,
        FragmentCreateOrUpdateTodo.FragmentUpdateOrCreateTodoCallback {
    private static final String FRAG_CREATE_OR_UPDATE = "FRAG_CREATE_OR_UPDATE";
    private static final String FRAG_TODO_LIST = "FRAG_TODO_LIST";
    private static final String FRAG_TODO_DETAIL = "FRAG_TODO_DETAIL";

    private ArrayList<Todo> listData;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        manager = getSupportFragmentManager();

        /*
        The Line below is meant to allow you to test running the App from a state of which the
        todos.db file doesn't exist. This is only necessary to demonstrate how to pre-populate a
        Database with JSON data, should you need to.

        To test, comment out everything in onCreate() below the deleteDatabase() method, uncomment
         deleteDatabase(), and run the App once as such. Once that occurs, do the opposite and run
         the App like normal. It should pre-populate the Database with the data from todos.json.

         HueHueHueHueHueHueHueHue
         */

        //getApplicationContext().deleteDatabase("todos.db");

        /*
        The following code checks to see if the database file exists. If it doesn't, it
        pre-populates the Database with some data.
         */

        File database = getApplicationContext().getDatabasePath("todos.db");
        if (!database.exists()) {
            LoadInitialTodosToDatabase loader = new LoadInitialTodosToDatabase(getApplicationContext());
            loader.setOnDatabaseBuilt(new LoadInitialTodosToDatabase.OnDatabaseBuilt() {
                @Override
                public void buildComplete() {
                    loadTodoList();
                }
            });
            loader.execute();
        } else {
            loadTodoList();
        }
    }

    /**
     * Loads our data from todos.db. Once the data has been loaded (i.e. setQueryComplete fires),
     * we are able to load our Fragment which contains the RecyclerView to display the to do data.
     */
    private void loadTodoList() {
        ReadFromDatabase reader = new ReadFromDatabase(getApplicationContext());
        reader.setQueryCompleteListener(new ReadFromDatabase.OnQueryComplete() {
            @Override
            public void setQueryComplete(ArrayList result) {
                listData = result;
                loadListFragment();
            }
        });
        reader.execute();
    }

    /**
     * As the ListFragment will be the first Fragment loaded when the App is loaded from onCreate(),
     * we need a special method to load it first thing.
     */
    private void loadListFragment() {
        Fragment listFrag = FragmentTodoList.newInstance(listData);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        transaction.replace(R.id.cont_todo_fragments, listFrag, FRAG_TODO_LIST);
        transaction.commit();
    }

    /**
     * Callback for FragmentTodoList. Fires when the user Swipes an Item out of the RecyclerView,
     * indicating that the user wishes to delete an item.
     *
     * @param position - Position of Item that was swiped
     */
    @Override
    public void onListItemSwiped(int position) {
        DeleteFromDatabase delete = new DeleteFromDatabase(getApplicationContext(),
                listData.get(position));
        delete.setDeleteCompleteListener(new DeleteFromDatabase.OnDeleteComplete() {
            @Override
            public void setQueryComplete(Long result) {

            }
        });
        delete.execute();
    }

    /**
     * Callback for FragmentTodoList. Fires when the user clicks on an Item in the RecyclerView,
     * indicating that the user wishes to view details of the to do object
     *
     * @param position - The position in listData of the clicked item.
     */
    @Override
    public void onListItemClicked(int position) {
        Fragment detailFrag = FragmentTodoDetail.newInstance(listData.get(position));
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        transaction.replace(R.id.cont_todo_fragments, detailFrag, FRAG_TODO_DETAIL);
        transaction.commit();
    }

    /**
     * Callback to open FragmentCreateOrUpdateTodo.java in order to create a new to do object.
     * Calls newInstance() method which does not supply an existing to do object. See method
     * in FragmentCreateOrUpdateTodo for details
     */
    @Override
    public void onAddTodoButtonClicked() {
        Fragment createFrag = FragmentCreateOrUpdateTodo.newInstance();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        transaction.replace(R.id.cont_todo_fragments, createFrag, FRAG_CREATE_OR_UPDATE);
        transaction.commit();
    }

    /**
     * Callback to open FragmentCreateOrUpdateTodo.java in order to modify an existing to do object.
     * Calls newInstance() method which supplies an existing to do object. See method
     * in FragmentCreateOrUpdateTodo for details
     */
    @Override
    public void onEditButtonClick(Todo todo) {
        Fragment createFrag = FragmentCreateOrUpdateTodo.newInstance(todo);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        transaction.replace(R.id.cont_todo_fragments, createFrag, FRAG_CREATE_OR_UPDATE);
        transaction.commit();
    }

    /**
     * Callback to write incoming to do object to the database. Once that operation is complete,
     * load FragmentTodoList.java into the foreground.
     *
     * @param todo Either a newly created to do, or an existing to do which will be updated.
     */
    @Override
    public void onDoneButtonClick(Todo todo) {
        WriteToDatabase writer = new WriteToDatabase(getApplicationContext(), todo);
        writer.setWriteCompleteListener(new WriteToDatabase.OnWriteComplete() {
            @Override
            public void setWriteComplete(long result) {
                loadTodoList();
            }
        });
        writer.execute();
    }

    /**
     * This could be handled better. We essentially use the TodoListFragment as a dashboard for the
     * App. This leads to somewhat poor UI design, as a back press when editing an existing To do
     * object should return the user to the TodoDetailFragment. I didn't feel like it was worth
     * covering in the tutorials, but be aware of this.
     */
    @Override
    public void onBackPressed() {
        Fragment listFrag = manager.findFragmentByTag(FRAG_TODO_LIST);
        if (listFrag == null) {
            loadListFragment();
        }
    }
}
