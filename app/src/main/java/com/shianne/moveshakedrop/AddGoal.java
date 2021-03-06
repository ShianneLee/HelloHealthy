package com.shianne.moveshakedrop;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class AddGoal extends ActionBarActivity {

    DBAdapter db = new DBAdapter(this);
    private ListView drawerList;
    private ArrayAdapter<String> navAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private String activityTitle;
    private Intent intent;
    EditText goalDescET;
    TextView goalValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.goalLayout);
        goalDescET = (EditText) findViewById(R.id.goalDescEdit);
        goalValid = (TextView) findViewById(R.id.addGoalValidation);
        goalDescET.addTextChangedListener(watch);

        // When the screen is touched outside of the edit field, the keyboard will be hidden
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });

        drawerList = (ListView) findViewById(R.id.navList);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        activityTitle = getTitle().toString();

        // Creates the sliding navigation menu
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    // Hide the soft keyboard
    protected void hideKeyboard(View view){

        InputMethodManager imm = (InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.length() == 0){
                Toast.makeText(getApplicationContext(), "Field must contain value!", Toast.LENGTH_LONG).show();
            }
        }
    };

    // Adds the new goal to the database and displays the Goals list
    public void onClickAddGoal(View view){

        goalDescET = (EditText) findViewById(R.id.goalDescEdit);
        String goalDesc = goalDescET.getText().toString();
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePickerAddGoal);
        String dateCompleted = db.getDateTime(datePicker);

        // If the user doesn't enter a value into the goal description, an error will display and
        // redisplay the activity
        if(goalDesc.equals("")) {
            recreate();
            Toast.makeText(getApplicationContext(), "Field must contain a value, please!", Toast.LENGTH_LONG).show();
        }else {
            try {
                db.openDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Inserts new goal into table
            db.createGoal(goalDesc, dateCompleted);

            // Displays all the goals including the new one in the Goals List
            startActivity(new Intent(this, GoalsList.class));
        }
    }

    // Adds each item to the sliding menu
    private void addDrawerItems(){

        String[] listArr = getResources().getStringArray(R.array.navItems);
        navAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listArr);
        drawerList.setAdapter(navAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerList.setItemChecked(position, true);
                switch(position){
                    case 0:
                        intent = new Intent(AddGoal.this, AddGoal.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(AddGoal.this, GoalsList.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(AddGoal.this, CompletedGoalsList.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(AddGoal.this, AddWeight.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(AddGoal.this, WeightHistory.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(AddGoal.this, SelectItem.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(AddGoal.this, ItemsHistory.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    // Decides what to display when sliding menu is open or closed
    private void setupDrawer(){

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawerOpen,
                R.string.drawerClose){

            public void onDrawerOpened(View drawerView){

                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view){

                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(activityTitle);
                invalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_goal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item) || drawerToggle.onOptionsItemSelected(item);
    }
}
