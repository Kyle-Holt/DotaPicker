package com.example.kyle.dotapicker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.kyle.dotapicker.R.id.enemy_advantage;
import static com.example.kyle.dotapicker.R.id.enemy_win;
import static com.example.kyle.dotapicker.R.xml.preferences;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    ImageView Enemy1;
    ImageView Enemy2;
    ImageView Enemy3;
    ImageView Enemy4;
    ImageView Enemy5;

    ImageView Ally1;
    ImageView Ally2;
    ImageView Ally3;
    ImageView Ally4;
    ImageView Ally5;

    TextView heroListHeader;
    TextView heroListName;
    TextView heroListWAllies;
    TextView heroListTotal;
    TextView heroListVEnemies;
    TextView heroListColor;


    TextView allyHeader;
    TextView enemyHeader;
    TextView allySynergy;
    TextView allyAdvantage;
    TextView enemySynergy;
    TextView enemyAdvantage;
    TextView allyWin;
    TextView enemyWin;
    StringBuffer urlString;
    StatsCalculator sc;
    DataBaseHelper myDb;
    MenuItem action_switch;
    private BottomNavigationView mBottomBar;
    private ListView mListView1;
    private CustomAdapter adapter;

    Integer hero_id = 0;
    public static Integer Eh_id1 = 0;
    public static Integer Eh_id2 = 0;
    public static Integer Eh_id3 = 0;
    public static Integer Eh_id4 = 0;
    public static Integer Eh_id5 = 0;

    public static Integer Ah_id1 = 0;
    public static Integer Ah_id2 = 0;
    public static Integer Ah_id3 = 0;
    public static Integer Ah_id4 = 0;
    public static Integer Ah_id5 = 0;

    public static String user_id = "0";
    int reload_table_count = 0;
    public static int bayes_constant = 3;
    public int count = 0;
    final public static int hero_pool_size = 115;
    double allied_team_advantage = 0;
    double allied_team_synergy = 0;
    double enemy_team_advantage = 0;
    double enemy_team_synergy = 0;
    int selected_role = 0;
    public int alliedCount = 0;
    public int enemyCount = 0;
    String strRole = "";
    static String against_hero = "heroes?against_hero_id=";
    static String with_hero =  "heroes?with_hero_id=";
    boolean role_selected = false;
    public static boolean setting_changed = false;
    public static boolean has_started = false;
    public static boolean user_stats = true;
    public static boolean short_text = false;
    public static boolean tables_loaded = false;

    double[][] str = new double[hero_pool_size][hero_pool_size];
    double[][] str_win = new double[hero_pool_size][hero_pool_size];
    double[][] str_loss = new double[hero_pool_size][hero_pool_size];
    double[][] ally_str = new double[hero_pool_size][hero_pool_size];
    double[][] ally_str_win = new double[hero_pool_size][hero_pool_size];
    double[][] ally_str_loss = new double[hero_pool_size][hero_pool_size];
    double[][] temp = new double[hero_pool_size][hero_pool_size];
    double[][] Advantage_table = new double[hero_pool_size][hero_pool_size];
    double[][] Synergy_table = new double[hero_pool_size][hero_pool_size];
    double[][] Win_vs_enemies_table = new double[hero_pool_size][hero_pool_size];
    double[][] Win_with_allies_table = new double[hero_pool_size][hero_pool_size];
    int[][] hero_role = new int[hero_pool_size][11];
    double[][] dummy_zero_array = new double[hero_pool_size][hero_pool_size];
    double[] populationWinRates = new double[hero_pool_size];
    Map<Integer, double[]> hero_map = new HashMap<>();

    public static String[] names = new String[hero_pool_size];
    public static String[] subnames = new String[hero_pool_size];
    public static int[] images = new int[hero_pool_size];
    public int[] allied_heroes;
    public int[] enemy_heroes;
    public ImageView[] alliedIcons = new ImageView[5];
    public ImageView[] enemyIcons = new ImageView[5];
    List<Hero> selectedHeroList = new ArrayList<>();
    List<Hero> heroListCopy = new ArrayList<>();
    private List<Hero> heroList = new ArrayList<Hero>();
    private Map<Integer, List<Integer>> heroPopulationRates;
    Map<Integer, Double> populationWR;
    Map<Integer, List<Integer>> chosenHeroStats;
    public static Hero chosenHero;
    public boolean normal = false;
    public boolean high = false;
    public boolean veryHigh = false;

    //Methods necsesary for Android
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FirebaseCrash.report(new Exception("My first Android non-fatal error"));

        mBottomBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(mBottomBar);
        PreferenceManager.setDefaultValues(this, preferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        myDb = new DataBaseHelper(this);
        sc = new StatsCalculator();
        //user_id = String.valueOf(prefs.getString("edittext_preference", "default_value"));
        //user_id = String.valueOf(prefs.getString("edittext_preference", "default_value"));
        loadData();


        hero_role = myDb.roleTableBuilder("Heroes");

        allied_heroes = new int[5];
        enemy_heroes = new int[5];

        define_heroes();
        //setHeroWinRates();
        addHeroImages(images);
        addHeroNames(names);
        addHeroSubNames(subnames);

        Log.d("HEROPOPRATES", String.valueOf("1"));

        Enemy1 = (ImageView) findViewById(R.id.enemy1);
        Enemy1.setOnClickListener(this);
        Enemy2 = (ImageView) findViewById(R.id.enemy2);
        Enemy2.setOnClickListener(this);
        Enemy3 = (ImageView) findViewById(R.id.enemy3);
        Enemy3.setOnClickListener(this);
        Enemy4 = (ImageView) findViewById(R.id.enemy4);
        Enemy4.setOnClickListener(this);
        Enemy5 = (ImageView) findViewById(R.id.enemy5);
        Enemy5.setOnClickListener(this);

        Ally1 = (ImageView) findViewById(R.id.ally1);
        Ally1.setOnClickListener(this);
        Ally2 = (ImageView) findViewById(R.id.ally2);
        Ally2.setOnClickListener(this);
        Ally3 = (ImageView) findViewById(R.id.ally3);
        Ally3.setOnClickListener(this);
        Ally4 = (ImageView) findViewById(R.id.ally4);
        Ally4.setOnClickListener(this);
        Ally5 = (ImageView) findViewById(R.id.ally5);
        Ally5.setOnClickListener(this);

        setUpLongClickListenersAndHeroRemoval();

        action_switch = (MenuItem) findViewById(R.id.action_switch_mode);

        heroListColor = (TextView) findViewById(R.id.list_color);
        heroListName = (TextView) findViewById(R.id.list_name);
        heroListHeader = (TextView) findViewById(R.id.list_hero);
        heroListTotal = (TextView) findViewById(R.id.list_total);
        heroListWAllies = (TextView) findViewById(R.id.list_with_allies);
        heroListVEnemies = (TextView) findViewById(R.id.list_with_enemies);

        allySynergy = (TextView) findViewById(R.id.allied_synergy);
        allyAdvantage = (TextView) findViewById(R.id.allied_advantage);
        enemySynergy = (TextView) findViewById(R.id.enemy_synergy);
        enemyAdvantage = (TextView) findViewById(enemy_advantage);
        allyWin = (TextView) findViewById(R.id.allied_win);
        enemyWin = (TextView) findViewById(enemy_win);
        allyHeader = (TextView) findViewById(R.id.Ally_text);
        enemyHeader = (TextView) findViewById(R.id.Enemy_text);

        mListView1 = (ListView)findViewById(R.id.listView1);

        if (prefs.getBoolean("pref_short_text", true)) {
            short_text = true;
        }

        setUpTeamAdvantagesAndSynergies();
        initiateHeroJsonRetrieval("0", Ally5, against_hero);

        adapter = new CustomAdapter(this, heroList);

        mListView1.setAdapter(adapter);

        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                loadData();
                Log.d("UserID", user_id);

                Toast.makeText(getApplicationContext(), String.valueOf(heroList.get(position).getName()) + " selected. Now displaying population statistics",
                        Toast. LENGTH_LONG).show();

                //user_id = "0";
                user_stats = false;
                adapter.notifyDataSetChanged();
                Ally5.setImageResource(images[heroList.get(position).getId()]);
                Ah_id5 = heroList.get(position).getId();
                chosenHero = new Hero(names[Ah_id5],images[Ah_id5], Ah_id5, subnames[Ah_id5]);
                allied_heroes[4] = Ah_id5;
                heroList.remove(chosenHero);
                count++;
                reorder(false);
                Ah_id5 = initiateHeroJsonRetrieval(String.valueOf(Ah_id5), Ally5, with_hero);

                //setUpTeamAdvantagesAndSynergies();



                has_started = true;
                //setChosenHeroWinRates(Ah_id5);
                //this may have to be a user setting




            }
        });

        Log.d("USER_ID", user_id);
        Log.d("USER_ID", String.valueOf(hero_id));
        Log.d("Bayes Constant", String.valueOf(bayes_constant));

        mBottomBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull final MenuItem mItem) {
                        switch (mItem.getItemId()) {

                            case R.id.menu_home:
                                String str = reorder(true);
                                mItem.setTitle(str);
                                break;

                            case R.id.add_enemy_button:
                                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                                int firstEmptySpace = getFirstEmptyHeroSpace(enemy_heroes);

                                if(firstEmptySpace != -1) {
                                    startActivityForResult(searchIntent, firstEmptySpace);
                                } else {
                                    //perhaps say if all resrouces are set to 0, clear?
                                    Toast.makeText(getApplicationContext(), "All enemy slots filled. Click an existing hero to change them.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.menu_search:
                                clear();
                                break;

                            case R.id.add_ally_button:
                                searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                                firstEmptySpace = getFirstEmptyHeroSpace(allied_heroes);
                                firstEmptySpace += 5;

                                if(firstEmptySpace != 10) {
                                    startActivityForResult(searchIntent, firstEmptySpace);
                                } else {
                                    Toast.makeText(getApplicationContext(), "All allied slots filled. Click an existing hero to change them.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.role_selector:
                                PopupMenu popup;
                                popup = new PopupMenu(MainActivity.this, mBottomBar.findViewById(R.id.role_selector));
                                popup.getMenuInflater().inflate(R.menu.popup_roles, popup.getMenu());

                                popup.setOnMenuItemClickListener(new MyOnMenuItemClickListener(mItem){
                                    public boolean onMenuItemClick(MenuItem item){
                                        selected_role = Integer.valueOf(item.getTitleCondensed().toString());
                                        strRole = item.getTitle().toString();
                                        mItem.setTitle(strRole);
                                        if(selected_role != 0){
                                            Toast.makeText(getApplicationContext(), "Showing only " + item.getTitle().toString().toLowerCase() + " heroes",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Showing all heroes",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        selectRole(selected_role);
                                        return true;
                                    }
                                });
                                popup.show();

                                break;
                        }
                        return true;
                    }
                });
    }



    private class MyOnMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private MenuItem item;

        public MyOnMenuItemClickListener(MenuItem item) {
            this.item = item;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(setting_changed){
            clear();
        }
        setting_changed = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent;
        switch (item.getItemId()) {
            case R.id.menu_settings:
                myIntent = new Intent(MainActivity.this,
                        PreferencesActivity.class);
                startActivity(myIntent);
                return true;
            case R.id.menu_help:
                myIntent = new Intent(MainActivity.this,
                        HelpActivity.class);
                startActivity(myIntent);
                return true;
            /*case R.id.menu_credits:
                myIntent = new Intent(MainActivity.this,
                        AboutActivity.class);
                startActivity(myIntent);
                return true;
                */
            case R.id.action_switch_mode:
                //if(has_started){
                    user_stats = !user_stats;
                    adapter.notifyDataSetChanged();
                    count++;
                    reorder(false);
                    if(user_stats) {
                        //item.setTitle("User stats");
                        Toast.makeText(getApplicationContext(), "Now displaying user statistics",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //item.setTitle("Pop Stats");
                        Toast.makeText(getApplicationContext(), "Now displaying population statistics",
                                Toast.LENGTH_SHORT).show();
                    }
               // } else {

               // }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals("edittext_preference")){
            if(!sharedPreferences.getString("edittext_preference", "default_value").equals("default value")){
                user_id = sharedPreferences.getString("edittext_preference", "default_value");
            } else {
                user_id = String.valueOf(0);
            }
            clear();
        }
        if(key.equals("skill_setting_preference")){
            tables_loaded = false;
            clear();
        }
        if(key.equals("time_setting_preference")){
            tables_loaded = false;
            clear();
        }
    }

    //Methods for handling the on-click to Json requests
    @Override
    public void onClick(View v) {
        has_started = true;
        Intent searchIntent = new Intent(MainActivity.this,
                SearchActivity.class);

        switch(v.getId()) {

            case R.id.enemy1:
                startActivityForResult(searchIntent, 1);
                break;
            case R.id.enemy2:
                startActivityForResult(searchIntent, 2);
                break;
            case R.id.enemy3:
                startActivityForResult(searchIntent, 3);
                break;
            case R.id.enemy4:
                startActivityForResult(searchIntent, 4);
                break;
            case R.id.enemy5:
                startActivityForResult(searchIntent, 5);
                break;

            case R.id.ally1:
                startActivityForResult(searchIntent, 6);
                break;
            case R.id.ally2:
                startActivityForResult(searchIntent, 7);
                break;
            case R.id.ally3:
                startActivityForResult(searchIntent, 8);
                break;
            case R.id.ally4:
                startActivityForResult(searchIntent, 9);
                break;
            case R.id.ally5:
                if(Ah_id5 == 0){
                    Toast.makeText(getApplicationContext(), "This is your hero. Please select from the list.", Toast.LENGTH_SHORT).show();
                } else if((Ah_id1 + Ah_id2 + Ah_id3 + Ah_id4 == 0)&&(Eh_id1 + Eh_id2 + Eh_id3 + Eh_id4 + Eh_id5 == 0)) {
                    Toast.makeText(getApplicationContext(), "Hero unselected. Now displaying your statistics.", Toast.LENGTH_SHORT).show();
                    user_stats = true;
                    //action_switch.setTitle("USER STATS");
                    Ally5.setImageResource(0);
                    Ah_id5 = 0;
                    allied_heroes[4] = 0;
                    setUpTeamAdvantagesAndSynergies();
                    chosenHero = null;
                    heroList.clear();
                    adapter.notifyDataSetChanged();
                    //has_started = true;
                    //setChosenHeroWinRates(Ah_id5);
                    //this may have to be a user setting
                    loadData();
                    initiateHeroJsonRetrieval("0", Ally5, against_hero);
                } else {
                    Toast.makeText(getApplicationContext(), "Hero unselected. Now displaying your statistics.", Toast.LENGTH_SHORT).show();
                    user_stats = true;
                    //action_switch.setTitle("USER STATS");
                    boolean allyFilled = false;
                    int heroSpace = getFirstFilledHeroSpace(enemy_heroes);
                    if(heroSpace == -1) {
                        heroSpace = getFirstFilledHeroSpace(allied_heroes);
                        allyFilled = true;
                    }
                    Log.d("ALLYFILLED", String.valueOf(allyFilled));
                    Log.d("Herospace", String.valueOf(heroSpace));
                    Ally5.setImageResource(0);
                    Ah_id5 = 0;
                    allied_heroes[4] = 0;
                    setUpTeamAdvantagesAndSynergies();
                    chosenHero = null;
                    heroList.clear();
                    adapter.notifyDataSetChanged();
                    define_heroes();
                    loadData();
                    if(!allyFilled){
                        initiateHeroJsonRetrieval(String.valueOf(enemy_heroes[heroSpace]), enemyIcons[heroSpace], against_hero);
                    } else {
                        initiateHeroJsonRetrieval(String.valueOf(allied_heroes[heroSpace]), alliedIcons[heroSpace], with_hero);
                    }

                }

                break;

            default:

                break;
        }
    }

    public void deleteHeroIconReturnToList() {
        boolean allyFilled = false;
        int heroSpace = getFirstFilledHeroSpace(enemy_heroes);
        if(heroSpace == -1) {
            heroSpace = getFirstFilledHeroSpace(allied_heroes);
            allyFilled = true;
            if(heroSpace == -1) {
                heroSpace = 0;
            }
        }
        setUpTeamAdvantagesAndSynergies();
        heroList.clear();
        adapter.notifyDataSetChanged();
        define_heroes();
        loadData();
        if(!allyFilled){
            initiateHeroJsonRetrieval(String.valueOf(enemy_heroes[heroSpace]), enemyIcons[heroSpace], against_hero);
        } else {
            initiateHeroJsonRetrieval(String.valueOf(allied_heroes[heroSpace]), alliedIcons[heroSpace], with_hero);
        }
    }

    public void setUpLongClickListenersAndHeroRemoval() {
        Enemy1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Eh_id1 != 0) {
                    Eh_id1 = 0;
                    Enemy1.setImageResource(0);
                    enemy_heroes[0] = 0;
                    deleteHeroIconReturnToList();
                }
                return true;
            }
        });
        Enemy2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Eh_id2 != 0) {
                    Eh_id2 = 0;
                    Enemy2.setImageResource(0);
                    enemy_heroes[1] = 0;
                    deleteHeroIconReturnToList();
                }
                return true;
            }
        });
        Enemy3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Eh_id3 != 0) {
                    Toast.makeText(getApplicationContext(), "Showing all heroes",
                            Toast.LENGTH_SHORT).show();
                    Eh_id3 = 0;
                    Enemy3.setImageResource(0);
                    enemy_heroes[2] = 0;
                    deleteHeroIconReturnToList();
                }
                return true;
            }
        });
        Enemy4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Eh_id4 != 0) {
                    Toast.makeText(getApplicationContext(), "Showing all heroes",
                            Toast.LENGTH_SHORT).show();
                    Eh_id4 = 0;
                    Enemy4.setImageResource(0);
                    enemy_heroes[3] = 0;
                    deleteHeroIconReturnToList();
                }
                return true;
            }
        });
        Enemy5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Eh_id5 != 0) {
                    Toast.makeText(getApplicationContext(), "Showing all heroes",
                            Toast.LENGTH_SHORT).show();
                    Eh_id5 = 0;
                    Enemy5.setImageResource(0);
                    enemy_heroes[4] = 0;
                    deleteHeroIconReturnToList();
                }
                return true;
            }
        });
        Ally1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Ah_id1 != 0) {
                    Ah_id1 = 0;
                    Ally1.setImageResource(0);
                    allied_heroes[0] = 0;
                    deleteHeroIconReturnToList();
                }
                return true;
            }
        });
        Ally2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Ah_id2 != 0) {
                    Ah_id2 = 0;
                    Ally2.setImageResource(0);
                    allied_heroes[1] = 0;
                    deleteHeroIconReturnToList();
                }
                return true;
            }
        });
        Ally3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Ah_id3 != 0) {
                    Ah_id3 = 0;
                    Ally3.setImageResource(0);
                    allied_heroes[2] = 0;
                    deleteHeroIconReturnToList();
                }
                return true;
            }
        });
        Ally4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Ah_id4 != 0) {
                    Ah_id4 = 0;
                    Ally4.setImageResource(0);
                    allied_heroes[3] = 0;
                    deleteHeroIconReturnToList();
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Eh_id1 = initiateHeroJsonRetrieval(result, Enemy1, against_hero);
            }
        }else if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Eh_id2 = initiateHeroJsonRetrieval(result, Enemy2, against_hero);
            }
        }else if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Eh_id3 = initiateHeroJsonRetrieval(result, Enemy3, against_hero);
            }
        } else if (requestCode == 4) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Eh_id4 = initiateHeroJsonRetrieval(result, Enemy4, against_hero);
            }
        }else if (requestCode == 5) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Eh_id5 = initiateHeroJsonRetrieval(result, Enemy5, against_hero);
            }
        }else if (requestCode == 6) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Ah_id1 = initiateHeroJsonRetrieval(result, Ally1, with_hero);
            }
        }else if (requestCode == 7) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Ah_id2 = initiateHeroJsonRetrieval(result, Ally2, with_hero);
            }
        }else if (requestCode == 8) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Ah_id3 = initiateHeroJsonRetrieval(result, Ally3, with_hero);
            }
        }else if (requestCode == 9) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                Ah_id4 = initiateHeroJsonRetrieval(result, Ally4, with_hero);
            }
        }else if(resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Json request failed. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public int initiateHeroJsonRetrieval(String result, ImageView image, String hero_team) {
        int box_position = Integer.valueOf(result);
        hero_id = Integer.valueOf(result);
        urlString = new StringBuffer("https://api.opendota.com/api/players");

        if((!user_id.equals(""))&&(!user_id.equals("default_value"))){
            urlString.append("/" + user_id);
        } else {
            urlString.append("/0");
        }
        if(hero_id == 0){
            urlString.append("/heroes?");
            new JsonTask().execute(urlString.toString());
        } else {
            image.setImageResource(images[box_position]);
            urlString.append("/" + hero_team);
            urlString.append(hero_id.toString());
            new JsonTask().execute(urlString.toString());

        }
        Log.d("URL", String.valueOf(urlString));
        return box_position;
    }

    public class JsonTask extends AsyncTask<String, String, Map<Integer,List<Integer>>> {

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Map<Integer,List<Integer>> doInBackground(String... params) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }

            JSONArray jsonArray = new JSONArray((buffer.toString()));

            //JSONObject jsonObject = new JSONObject((buffer.toString()));

            Map<Integer,List<Integer>> in = new HashMap<Integer, List<Integer>>();
            in = parseArray(jsonArray);
            //parse(jsonObject, in);

            return in;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Map<Integer,List<Integer>> result) {
        super.onPostExecute(result);
        if (result != null) {

            Log.d("USERID", user_id);

            temp = sc.getHeroMap(result, hero_id);

            double[][] temp2 = new double[hero_pool_size][hero_pool_size];
            double[][] temp3 = new double[hero_pool_size][hero_pool_size];

            temp2 = sc.getHeroWinMap(result, hero_id);

            temp3 = sc.getHeroLossMap(result, hero_id);


            if (urlString.toString().contains(with_hero)) {
                for (int i = 1; i < hero_pool_size; i++) {
                    ally_str[i][hero_id] = temp[i][hero_id];
                    ally_str_win[i][hero_id] = temp2[i][hero_id];
                    ally_str_loss[i][hero_id] = temp3[i][hero_id];

                }
            } else if (urlString.toString().contains(against_hero)) {
                for (int i = 1; i < hero_pool_size; i++) {
                    str[i][hero_id] = temp[i][hero_id];
                    str_win[i][hero_id] = temp2[i][hero_id];
                    str_loss[i][hero_id] = temp3[i][hero_id];

                }
            } else{
                for (int i = 1; i < hero_pool_size; i++) {
                    str[i][hero_id] = temp[i][hero_id];
                    str_win[i][hero_id] = temp2[i][hero_id];
                    str_loss[i][hero_id] = temp3[i][hero_id];
                }
            }

            heroList.clear();
            hero_map = totalHeroRating(str, ally_str, ally_str_win, ally_str_loss, str_win, str_loss);

            allied_heroes = new int[5];
            enemy_heroes = new int[5];

            define_heroes();
            //populationWR = getHeroWinRates(heroPopulationRates);

            for (int i = 1; i < hero_pool_size; i++) {
                if (i != 24) {
                    if(hero_id != 0){

                        heroList.add(new Hero(names[i], images[i], i, hero_map.get(i)[0] + hero_map.get(i)[1], hero_map.get(i)[0], hero_map.get(i)[1],
                                totalAdvantage(i, Advantage_table, enemy_heroes), totalAdvantage(i, Synergy_table, allied_heroes),
                                getBayesWR(i, Win_with_allies_table, allied_heroes, ally_str_win, ally_str_loss), getBayesWR(i, Win_vs_enemies_table, enemy_heroes, str_win, str_loss),
                                getBayesWR(i, Win_with_allies_table, allied_heroes, dummy_zero_array, dummy_zero_array), getBayesWR(i, Win_vs_enemies_table, enemy_heroes, dummy_zero_array, dummy_zero_array)));
                    } else {
                        heroList.add(new Hero(names[i], images[i], i, (20*populationWinRates[getCorrectId(i)]/100 + hero_map.get(i)[2])/(20 +  hero_map.get(i)[3]+ hero_map.get(i)[2]),
                                (20*populationWinRates[getCorrectId(i)]/100 + hero_map.get(i)[2])/(20 + hero_map.get(i)[3]+ hero_map.get(i)[2]),
                                populationWinRates[getCorrectId(i)]/100, populationWinRates[getCorrectId(i)]/100));
                    }

                }
            }

            //errorCheckHeroTablesLoaded(heroList);


            List<Hero> dupeHeroList = new ArrayList<>();

            updateSelectedHeroesList(Eh_id1, dupeHeroList);
            updateSelectedHeroesList(Eh_id2, dupeHeroList);
            updateSelectedHeroesList(Eh_id3, dupeHeroList);
            updateSelectedHeroesList(Eh_id4, dupeHeroList);
            updateSelectedHeroesList(Eh_id5, dupeHeroList);

            updateSelectedHeroesList(Ah_id1, dupeHeroList);
            updateSelectedHeroesList(Ah_id2, dupeHeroList);
            updateSelectedHeroesList(Ah_id3, dupeHeroList);
            updateSelectedHeroesList(Ah_id4, dupeHeroList);

            heroList.removeAll(dupeHeroList);
            heroList.remove(chosenHero);

            heroListCopy.clear();

            for (int i = 0; i < heroList.size(); i++) {
                heroListCopy.add(i, heroList.get(i));
            }

            if (selected_role != 0) {
                selectRole(selected_role);
            }

            //this is to keep the current view order
            count++;
            reorder(false);

            errorCheckHeroTablesLoaded(heroList);

            setUpTeamAdvantagesAndSynergies();

            define_heroes();


        } else {
            Toast.makeText(getApplicationContext(), "Json request failed. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }

        }
    }

    private void errorCheckHeroTablesLoaded(List<Hero> heroList) {
        Hero last = heroList.get(heroList.size() - 1);
        if(last.getBayesAlliedWR() != 0) {
            Log.d("IS NOT 0", "ALLIES 0");
        } else {
            Log.d("IS 0", "ALLIES 0");
            Toast.makeText(getApplicationContext(), "Data not loaded correctly, attempting to reload.",
                    Toast.LENGTH_SHORT).show();
            reload_table_count += 1;
            if(reload_table_count < 3) {
                tables_loaded = false;
                clear();
            } else {
            }
        }
    }

    public int getFirstEmptyHeroSpace(int[] hero_array) {
        for(int i = 0; i < hero_array.length; i++) {
            if(hero_array[i] == 0) {
                return i+1;
            }
        }
        return -1;
    }

    public int getFirstFilledHeroSpace(int[] hero_array) {
        for(int i = 0; i < hero_array.length; i++) {
            if(hero_array[i] != 0) {
                return i;
            }
        }
        return -1;
    }

    //Methods for calculating hero and team statistics
    private SpannableStringBuilder[] setUpTeamAdvantagesAndSynergies() {

        CustomAdapter ca = new CustomAdapter();
        SpannableStringBuilder team_synergy;
        SpannableStringBuilder enemy_synergy_value;
        SpannableStringBuilder team_advantage;
        SpannableStringBuilder enemy_advantage;
        SpannableStringBuilder team_win;
        SpannableStringBuilder enemy_win;
        SpannableStringBuilder sb;
        alliedCount = 0;
        enemyCount = 0;
        int enemyHeroCount = 0;
        int heroCount = 0;
        int allySum = 0;
        int enemySum = 0;
        String adv_string;
        String syn_string;



        for(int i = 0; i < allied_heroes.length; i++) {
            if(allied_heroes[i] != 0) {
                alliedCount++;
            }
            if(enemy_heroes[i] != 0) {
                enemyCount++;
            }
        }

        Log.d("AHID5", String.valueOf(alliedCount));
        heroCount = alliedCount + enemyCount - 1;
        enemyHeroCount = alliedCount + enemyCount - 1;

        for(int i = 0; i < alliedCount; i++) {
            allySum += heroCount;
            heroCount -= 1;
        }

        for(int i = 0; i < enemyCount; i++) {
            enemySum += enemyHeroCount;
            enemyHeroCount -= 1;
        }

        //to get adjusted win, this would be a modified allied_heroes array
        //we would then calculate for the chosen hero specifically - probably by using the method with a single hero
        //then running through the remaining methods and adding it in individually
        List<int[]> hero_pairs = getAllHeroCombinationsOnTeam(allied_heroes);
        List<int[]> hero_to_enemy_pairs = getEnemyPairs(allied_heroes, enemy_heroes);
        List<int[]> enemy_pairs = getAllHeroCombinationsOnTeam(enemy_heroes);
        List<int[]> enemy_to_hero_pairs = getEnemyPairs(enemy_heroes, allied_heroes);
        double newSynergy = calculateTeamAttribute(hero_pairs, Synergy_table);
        double newAdvantage = calculateHeroAndEnemyAdvantage(hero_to_enemy_pairs, Advantage_table);
        double enemy_synergy = calculateTeamAttribute(enemy_pairs, Synergy_table);
        double enemy_Advantage = calculateHeroAndEnemyAdvantage(enemy_to_hero_pairs, Advantage_table);
        double newWin = calculateTeamAttribute(hero_pairs, Win_with_allies_table)/allySum;
        double newToEnemyWin = calculateHeroAndEnemyAdvantage(hero_to_enemy_pairs, Win_vs_enemies_table)/allySum;
        double enemy_Win = calculateTeamAttribute(enemy_pairs, Win_with_allies_table)/enemySum;
        double enemyToAllyWin = calculateHeroAndEnemyAdvantage(enemy_to_hero_pairs, Win_vs_enemies_table)/enemySum;
        allied_team_synergy = newSynergy;
        allied_team_advantage = newAdvantage;
        enemy_team_synergy = enemy_synergy;
        enemy_team_advantage = enemy_Advantage;

        double remainder = 100 - (newWin + newToEnemyWin) - (enemy_Win + enemyToAllyWin);
        double percentageAlly = (newWin + newToEnemyWin)/((newWin + newToEnemyWin) + (enemy_Win + enemyToAllyWin));

        if (short_text) {
            adv_string = "Adv: ";
            syn_string = "Syn: ";
            allySynergy.setPadding(5,0,0,0);
            enemySynergy.setPadding(5,0,0,0);
            //allyAdvantage.setGravity(Gravity.CENTER_HORIZONTAL);
            //enemyAdvantage.setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            adv_string = "Advantage: ";
            syn_string = "Synergy: ";
        }
        team_synergy = ca.determineColor(newSynergy, syn_string, "%", 0);
        enemy_synergy_value = ca.determineColor(enemy_synergy, syn_string, "%", 0);
        team_advantage = ca.determineColor(newAdvantage, adv_string, "%", 0);
        enemy_advantage = ca.determineColor(enemy_Advantage, adv_string, "%", 0);

        if(Double.isNaN(newWin + newToEnemyWin + remainder*percentageAlly)){
            sb = ca.determineColor(0, "Win: ", "%", 50);
            team_win = ca.checkColor(0, sb, 0);
        } else {
            sb = ca.determineColor(newWin + newToEnemyWin + remainder*percentageAlly, "Win: ", "%", 50);
            team_win = ca.checkColor(newWin + newToEnemyWin + remainder*percentageAlly, sb, 0);
        }
        if(Double.isNaN(enemy_Win + enemyToAllyWin + remainder*(1-percentageAlly))) {
            sb = ca.determineColor(0, "Win: ", "%", 50);
            enemy_win = ca.checkColor(0, sb, 0);
        } else {
            sb = ca.determineColor(enemy_Win + enemyToAllyWin + remainder*(1-percentageAlly), "Win: ", "%", 50);
            enemy_win = ca.checkColor(enemy_Win + enemyToAllyWin + remainder*(1-percentageAlly), sb, 0);
        }

        Log.d("TRUE", "IN THE METHOD");

        SpannableStringBuilder[] team_stats = new SpannableStringBuilder[6];
        team_stats[0] = team_synergy;
        team_stats[1] = enemy_synergy_value;
        team_stats[2] = team_advantage;
        team_stats[3] = enemy_advantage;
        team_stats[4] = team_win;
        team_stats[5] = enemy_win;

        allySynergy.setText(team_synergy);
        enemySynergy.setText(enemy_synergy_value);
        allyAdvantage.setText(team_advantage);
        enemyAdvantage.setText(enemy_advantage);
        allyWin.setText(team_win);
        enemyWin.setText(enemy_win);

        return team_stats;
    }

    public Map totalHeroRating(double[][] hero_array, double[][] ally_array, double[][] ally_str_win, double[][] ally_str_loss, double[][] str_win, double[][] str_loss){
        Map<Integer, double[]> hero_map = new HashMap<>();

        if(hero_id != 0) {
            for(int i = 1; i < hero_array.length; i++) {
                double friendly = ally_array[i][Ah_id1] + ally_array[i][Ah_id2] + ally_array[i][Ah_id3] +
                        ally_array[i][Ah_id4] + ally_array[i][Ah_id5];
                double ally_win = ally_str_win[i][Ah_id1] + ally_str_win[i][Ah_id2] + ally_str_win[i][Ah_id3] +
                        ally_str_win[i][Ah_id4] + ally_str_win[i][Ah_id5];
                double ally_loss = ally_str_loss[i][Ah_id1] + ally_str_loss[i][Ah_id2] + ally_str_loss[i][Ah_id3] +
                        ally_str_loss[i][Ah_id4] + ally_str_loss[i][Ah_id5];
                double enemy = hero_array[i][Eh_id1] + hero_array[i][Eh_id2] + hero_array[i][Eh_id3] +
                        hero_array[i][Eh_id4] + hero_array[i][Eh_id5];
                double enemy_win = str_win[i][Eh_id1] + str_win[i][Eh_id2] + str_win[i][Eh_id3] +
                        str_win[i][Eh_id4] + str_win[i][Eh_id5];
                double enemy_loss = str_loss[i][Eh_id1] + str_loss[i][Eh_id2] + str_loss[i][Eh_id3] +
                        str_loss[i][Eh_id4] + str_loss[i][Eh_id5];

                double[] totals = new double[7];
                totals[0] = friendly;
                totals[1] = enemy;
                totals[2] = enemy_win;
                totals[3] = enemy_loss;
                totals[4] = ally_win;
                totals[5] = ally_loss;

                hero_map.put(i,totals);
            }
            return hero_map;
        } else {
            for(int i = 1; i < hero_array.length; i++) {
                double enemy = hero_array[i][0];
                double enemy_win = str_win[i][0];
                double enemy_loss = str_loss[i][0];
                double[] totals = new double[7];
                totals[0] = 0;
                totals[1] = enemy;
                totals[2] = enemy_win;
                totals[3] = enemy_loss;
                totals[4] = 0;
                totals[5] = 0;

                hero_map.put(i,totals);
            }
            return hero_map;
        }

    }

    public double totalAdvantage(int i, double[][] doubTable, int[] selected_heroes) {
        double advantage1 = 0;
        if(selected_heroes[0] != 0){
            advantage1 = doubTable[getCorrectId(i)][getCorrectId(selected_heroes[0])];
        }
        double advantage2 = 0;
        if(selected_heroes[1] != 0){
            advantage2 = doubTable[getCorrectId(i)][getCorrectId(selected_heroes[1])];
        }
        double advantage3 = 0;
        if(selected_heroes[2] != 0){
            advantage3 = doubTable[getCorrectId(i)][getCorrectId(selected_heroes[2])];
        }
        double advantage4 = 0;
        if(selected_heroes[3] != 0){
            advantage4 = doubTable[getCorrectId(i)][getCorrectId(selected_heroes[3])];
        }
        double advantage5 = 0;
        if(selected_heroes[4] != 0){
            advantage5 = doubTable[getCorrectId(i)][getCorrectId(selected_heroes[4])];
        }
        double advantageTotal = advantage1 + advantage2 + advantage3 + advantage4 + advantage5;
        return advantageTotal;
    }

    public double getBayesWR(int i, double[][] population_wr, int[] selected_heroes, double[][] player_win, double[][] player_loss) {

        List<Double> amount = new ArrayList<>();
        double winRate1 = 0;
        if(selected_heroes[0] != 0){
            winRate1 = (bayes_constant*(population_wr[getCorrectId(i)][getCorrectId(selected_heroes[0])]/100) + player_win[i][selected_heroes[0]])/
                    (bayes_constant+ player_win[i][selected_heroes[0]] + player_loss[i][selected_heroes[0]]);
            amount.add(winRate1);
        }
        double winRate2 = 0;
        if(selected_heroes[1] != 0){
            winRate2 = (bayes_constant*(population_wr[getCorrectId(i)][getCorrectId(selected_heroes[1])]/100) + player_win[i][selected_heroes[1]])/
                    (bayes_constant + player_win[i][selected_heroes[1]] + player_loss[i][selected_heroes[1]]);
            amount.add(winRate2);
        }
        double winRate3 = 0;
        if(selected_heroes[2] != 0){
            winRate3 = (bayes_constant*(population_wr[getCorrectId(i)][getCorrectId(selected_heroes[2])]/100) + player_win[i][selected_heroes[2]])/
                    (bayes_constant + player_win[i][selected_heroes[2]] + player_loss[i][selected_heroes[2]]);
            amount.add(winRate3);
        }
        double winRate4 = 0;
        if(selected_heroes[3] != 0){
            winRate4 = (bayes_constant*(population_wr[getCorrectId(i)][getCorrectId(selected_heroes[3])]/100) + player_win[i][selected_heroes[3]])/
                    (bayes_constant + player_win[i][selected_heroes[3]] + player_loss[i][selected_heroes[3]]);
            amount.add(winRate4);
        }
        double winRate5 = 0;
        if(selected_heroes[4] != 0){
            winRate5 = (bayes_constant*(population_wr[getCorrectId(i)][getCorrectId(selected_heroes[4])]/100) + player_win[i][selected_heroes[4]])/
                    (bayes_constant + player_win[i][selected_heroes[4]] + player_loss[i][selected_heroes[4]]);
            amount.add(winRate5);
        }

        double winRateTotal = (winRate1 + winRate2 + winRate3 + winRate4 + winRate5)/amount.size();

        if(amount.size() > 0) {
            return winRateTotal;
        } else {
            return 0;
        }
    }

    public List<int[]> getAllHeroCombinationsOnTeam(int[] heroList) {
        final List<int[]> heroPairs = new ArrayList<int[]>();
        for (int i = 0; i < heroList.length; ++i) {
            for (int j = i + 1; j < heroList.length; ++j) {
                heroPairs.add(new int[]{heroList[i], heroList[j]});
            }
        }
        return heroPairs;
    }

    public static List<int[]> getEnemyPairs(int[] list, int[] enemy){
        final List<int[]> pairs = new ArrayList<int[]>();
        for (int i = 0; i < list.length; ++i) {
            for (int j = 0; j < enemy.length; ++j) {
                pairs.add(new int[]{list[i], enemy[j]});
            }
        }
        return pairs;
    }

    public double calculateTeamAttribute(List<int[]> heroPairs, double[][] table) {
        double attribute = 0;
        for(int i = 0; i < heroPairs.size(); i++){
            int[] dummy = heroPairs.get(i);
            attribute += table[getCorrectId(dummy[0])][getCorrectId(dummy[1])];
        }
        return attribute;
    }

    public double calculateHeroAndEnemyAdvantage(List<int[]> heroPairs, double[][] table) {
        double attribute = 0;
        for(int i = 0; i < heroPairs.size(); i++){
            int[] dummy = heroPairs.get(i);
            attribute += table[getCorrectId(dummy[0])][getCorrectId(dummy[1])];
        }
        return attribute;
    }

    //Methods used for establishing hero characteristics
    private void addHeroImages(int[] images) {
        images[1]= R.drawable.antimage_full;
        images[2]= R.drawable.axe_full;
        images[3]= R.drawable.bane_full;
        images[4]= R.drawable.bloodseeker_full;
        images[5]= R.drawable.crystal_maiden_full;
        images[6]= R.drawable.drow_ranger_full;
        images[7]= R.drawable.earthshaker_full;
        images[8]= R.drawable.juggernaut_full;
        images[9]= R.drawable.mirana_full;
        images[11]= R.drawable.nevermore_full;
        images[10]= R.drawable.morphling_full;
        images[12]= R.drawable.phantom_lancer_full;
        images[13]= R.drawable.puck_full;
        images[14]= R.drawable.pudge_full;
        images[15]= R.drawable.razor_full;
        images[16]= R.drawable.sand_king_full;
        images[17]= R.drawable.storm_spirit_full;
        images[18]= R.drawable.sven_full;
        images[19]= R.drawable.tiny_full;
        images[20]= R.drawable.vengefulspirit_full;
        images[21]= R.drawable.windrunner_full;
        images[22]= R.drawable.zuus_full;
        images[23]= R.drawable.kunkka_full;
        images[25]= R.drawable.lina_full;
        images[31]= R.drawable.lich_full;
        images[26]= R.drawable.lion_full;
        images[27]= R.drawable.shadow_shaman_full;
        images[28]= R.drawable.slardar_full;
        images[29]= R.drawable.tidehunter_full;
        images[30]= R.drawable.witch_doctor_full;
        images[32]= R.drawable.riki_full;
        images[33]= R.drawable.enigma_full;
        images[34]= R.drawable.tinker_full;
        images[35]= R.drawable.sniper_full;
        images[36]= R.drawable.necrolyte_full;
        images[37]= R.drawable.warlock_full;
        images[38]= R.drawable.beastmaster_full;
        images[39]= R.drawable.queenofpain_full;
        images[40]= R.drawable.venomancer_full;
        images[41]= R.drawable.faceless_void_full;
        images[42]= R.drawable.skeleton_king_full;
        images[43]= R.drawable.death_prophet_full;
        images[44]= R.drawable.phantom_assassin_full;
        images[45]= R.drawable.pugna_full;
        images[46]= R.drawable.templar_assassin_full;
        images[47]= R.drawable.viper_full;
        images[48]= R.drawable.luna_full;
        images[49]= R.drawable.dragon_knight_full;
        images[50]= R.drawable.dazzle_full;
        images[51]= R.drawable.rattletrap_full;
        images[52]= R.drawable.leshrac_full;
        images[53]= R.drawable.furion_full;
        images[54]= R.drawable.life_stealer_full;
        images[55]= R.drawable.dark_seer_full;
        images[56]= R.drawable.clinkz_full;
        images[57]= R.drawable.omniknight_full;
        images[58]= R.drawable.enchantress_full;
        images[59]= R.drawable.huskar_full;
        images[60]= R.drawable.night_stalker_full;
        images[61]= R.drawable.broodmother_full;
        images[62]= R.drawable.bounty_hunter_full;
        images[63]= R.drawable.weaver_full;
        images[64]= R.drawable.jakiro_full;
        images[65]= R.drawable.batrider_full;
        images[66]= R.drawable.chen_full;
        images[67]= R.drawable.spectre_full;
        images[69]= R.drawable.doom_bringer_full;
        images[68]= R.drawable.ancient_apparition_full;
        images[70]= R.drawable.ursa_full;
        images[71]= R.drawable.spirit_breaker_full;
        images[72]= R.drawable.gyrocopter_full;
        images[73]= R.drawable.alchemist_full;
        images[74]= R.drawable.invoker_full;
        images[75]= R.drawable.silencer_full;
        images[76]= R.drawable.obsidian_destroyer_full;
        images[77]= R.drawable.lycan_full;
        images[78]= R.drawable.brewmaster_full;
        images[79]= R.drawable.shadow_demon_full;
        images[80]= R.drawable.lone_druid_full;
        images[81]= R.drawable.chaos_knight_full;
        images[82]= R.drawable.meepo_full;
        images[83]= R.drawable.treant_full;
        images[84]= R.drawable.ogre_magi_full;
        images[85]= R.drawable.undying_full;
        images[86]= R.drawable.rubick_full;
        images[87]= R.drawable.disruptor_full;
        images[88]= R.drawable.nyx_assassin_full;
        images[89]= R.drawable.naga_siren_full;
        images[90]= R.drawable.keeper_of_the_light_full;
        images[91]= R.drawable.wisp_full;
        images[92]= R.drawable.visage_full;
        images[93]= R.drawable.slark_full;
        images[94]= R.drawable.medusa_full;
        images[95]= R.drawable.troll_warlord_full;
        images[96]= R.drawable.centaur_full;
        images[97]= R.drawable.magnataur_full;
        images[98]= R.drawable.shredder_full;
        images[99]= R.drawable.bristleback_full;
        images[100]= R.drawable.tusk_full;
        images[101]= R.drawable.skywrath_mage_full;
        images[102]= R.drawable.abaddon_full;
        images[103]= R.drawable.elder_titan_full;
        images[104]= R.drawable.legion_commander_full;
        images[106]= R.drawable.ember_spirit_full;
        images[107]= R.drawable.earth_spirit_full;
        images[108]= R.drawable.abyssal_underlord_full;
        images[109]= R.drawable.terrorblade_full;
        images[110]= R.drawable.phoenix_full;
        images[105]= R.drawable.techies_full;
        images[111]= R.drawable.oracle_full;
        images[112]= R.drawable.winter_wyvern_full;
        images[113]= R.drawable.arc_warden_full;
        images[114]= R.drawable.monkey_king_full;
    }

    private void addHeroNames(String[] names) {
        names[1] = "Anti-Mage";
        names[2] = "Axe";
        names[3] = "Bane";
        names[4] = "Bloodseeker";
        names[5] = "Crystal Maiden";
        names[6] = "Drow Ranger";
        names[7] = "Earthshaker";
        names[8] = "Juggernaut";
        names[9] = "Mirana";
        names[11] = "Shadow Fiend";
        names[10] = "Morphling";
        names[12] = "Phantom Lancer";
        names[13] = "Puck";
        names[14] = "Pudge";
        names[15] = "Razor";
        names[16] = "Sand King";
        names[17] = "Storm Spirit";
        names[18] = "Sven";
        names[19] = "Tiny";
        names[20] = "Vengeful Spirit";
        names[21] = "Windranger";
        names[22] = "Zeus";
        names[23] = "Kunkka";
        names[25] = "Lina";
        names[31] = "Lich";
        names[26] = "Lion";
        names[27] = "Shadow Shaman";
        names[28] = "Slardar";
        names[29] = "Tidehunter";
        names[30] = "Witch Doctor";
        names[32] = "Riki";
        names[33] = "Enigma";
        names[34] = "Tinker";
        names[35] = "Sniper";
        names[36] = "Necrophos";
        names[37] = "Warlock";
        names[38] = "Beastmaster";
        names[39] = "Queen of Pain";
        names[40] = "Venomancer";
        names[41] = "Faceless Void";
        names[42] = "Wraith King";
        names[43] = "Death Prophet";
        names[44] = "Phantom Assassin";
        names[45] = "Pugna";
        names[46] = "Templar Assassin";
        names[47] = "Viper";
        names[48] = "Luna";
        names[49] = "Dragon Knight";
        names[50] = "Dazzle";
        names[51] = "Clockwerk";
        names[52] = "Leshrac";
        names[53] = "Nature's Prophet";
        names[54] = "Lifestealer";
        names[55] = "Dark Seer";
        names[56] = "Clinkz";
        names[57] = "Omniknight";
        names[58] = "Enchantress";
        names[59] = "Huskar";
        names[60] = "Night Stalker";
        names[61] = "Broodmother";
        names[62] = "Bounty Hunter";
        names[63] = "Weaver";
        names[64] = "Jakiro";
        names[65] = "Batrider";
        names[66] = "Chen";
        names[67] = "Spectre";
        names[69] = "Doom";
        names[68] = "Ancient Apparition";
        names[70] = "Ursa";
        names[71] = "Spirit Breaker";
        names[72] = "Gyrocopter";
        names[73] = "Alchemist";
        names[74] = "Invoker";
        names[75] = "Silencer";
        names[76] = "Outworld Devourer";
        names[77] = "Lycanthrope";
        names[78] = "Brewmaster";
        names[79] = "Shadow Demon";
        names[80] = "Lone Druid";
        names[81] = "Chaos Knight";
        names[82] = "Meepo";
        names[83] = "Treant Protector";
        names[84] = "Ogre Magi";
        names[85] = "Undying";
        names[86] = "Rubick";
        names[87] = "Disruptor";
        names[88] = "Nyx Assassin";
        names[89] = "Naga Siren";
        names[90] = "Keeper of the Light";
        names[91] = "Io";
        names[92] = "Visage";
        names[93] = "Slark";
        names[94] = "Medusa";
        names[95] = "Troll Warlord";
        names[96] = "Centaur Warrunner";
        names[97] = "Magnus";
        names[98] = "Timbersaw";
        names[99] = "Bristleback";
        names[100] = "Tusk";
        names[101] = "Skywrath Mage";
        names[102] = "Abaddon";
        names[103] = "Elder Titan";
        names[104] = "Legion Commander";
        names[106] = "Ember Spirit";
        names[107] = "Earth Spirit";
        names[108] = "Underlord";
        names[109] = "Terrorblade";
        names[110] = "Phoenix";
        names[105] = "Techies";
        names[111] = "Oracle";
        names[112] = "Winter Wyvern";
        names[113] = "Arc Warden";
        names[114] = "Monkey King";
    }

    private void addHeroSubNames(String[] subnames) {
        subnames[1] = "AM";
        subnames[2] = "";
        subnames[3] = "Atropos";
        subnames[4] = "BS Strygwyr Seeker";
        subnames[5] = "CM Rylai";
        subnames[6] = "Traxex";
        subnames[7] = "ES Shaker";
        subnames[8] = "";
        subnames[9] = "POTM";
        subnames[11] = "SF";
        subnames[10] = "";
        subnames[12] = "PL";
        subnames[13] = "";
        subnames[14] = "";
        subnames[15] = "";
        subnames[16] = "SK";
        subnames[17] = "SS";
        subnames[18] = "";
        subnames[19] = "";
        subnames[20] = "VS";
        subnames[21] = "WR";
        subnames[22] = "";
        subnames[23] = "";
        subnames[25] = "";
        subnames[31] = "";
        subnames[26] = "";
        subnames[27] = "SS Rhasta";
        subnames[28] = "";
        subnames[29] = "";
        subnames[30] = "WD";
        subnames[32] = "SA";
        subnames[33] = "Nigma";
        subnames[34] = "";
        subnames[35] = "";
        subnames[36] = "";
        subnames[37] = "Lock";
        subnames[38] = "BM";
        subnames[39] = "QoP";
        subnames[40] = "";
        subnames[41] = "FV";
        subnames[42] = "WK Osterion SK";
        subnames[43] = "DP Krobelus";
        subnames[44] = "PA Mortred";
        subnames[45] = "";
        subnames[46] = "TA Lanaya";
        subnames[47] = "";
        subnames[48] = "";
        subnames[49] = "DK Davion";
        subnames[50] = "";
        subnames[51] = "Rattletrap CG CW";
        subnames[52] = "TS";
        subnames[53] = "NP Furion";
        subnames[54] = "LS Naix";
        subnames[55] = "DS Seer";
        subnames[56] = "Bone BF ";
        subnames[57] = "";
        subnames[58] = "";
        subnames[59] = "SW";
        subnames[60] = "NS Balanar";
        subnames[61] = "BM";
        subnames[62] = "BH Gondar";
        subnames[63] = "NW";
        subnames[64] = "THD";
        subnames[65] = "";
        subnames[66] = "HK";
        subnames[67] = "";
        subnames[69] = "Lucifer";
        subnames[68] = "AA";
        subnames[70] = "";
        subnames[71] = "SB Space cow";
        subnames[72] = "";
        subnames[73] = "";
        subnames[74] = "Kael Carl voker";
        subnames[75] = "";
        subnames[76] = "OD Outworld Devourer";
        subnames[77] = "";
        subnames[78] = "";
        subnames[79] = "SD";
        subnames[80] = "Bear LD Sylla";
        subnames[81] = "CK";
        subnames[82] = "";
        subnames[83] = "TP";
        subnames[84] = "OM";
        subnames[85] = "Dirge";
        subnames[86] = "GM";
        subnames[87] = "SC Thrall";
        subnames[88] = "NA";
        subnames[89] = "NS";
        subnames[90] = "Kotl";
        subnames[91] = "Wisp";
        subnames[92] = "";
        subnames[93] = "";
        subnames[94] = "Dusa Gorgon";
        subnames[95] = "";
        subnames[96] = "CW Bradwarden";
        subnames[97] = "";
        subnames[98] = "Shredder";
        subnames[99] = "BB";
        subnames[100] = "";
        subnames[101] = "";
        subnames[102] = "";
        subnames[103] = "TC ET";
        subnames[104] = "LC";
        subnames[106] = "ES";
        subnames[107] = "ES";
        subnames[108] = "Pitlord";
        subnames[109] = "TB";
        subnames[110] = "";
        subnames[105] = "";
        subnames[111] = "";
        subnames[112] = "WW";
        subnames[113] = "AC";
        subnames[114] = "MK Wukong";
    }

    private int getCorrectId(int id) {
        if (id == 10) {return 11;}
        if (id == 11) {return 10;}
        if (id == 25) {return 24;}
        if (id == 31) {return 25;}
        if (id == 32) {return 31;}
        if (id == 33) {return 32;}
        if (id == 34) {return 33;}
        if (id == 35) {return 34;}
        if (id == 36) {return 35;}
        if (id == 37) {return 36;}
        if (id == 38) {return 37;}
        if (id == 39) {return 38;}
        if (id == 40) {return 39;}
        if (id == 41) {return 40;}
        if (id == 42) {return 41;}
        if (id == 43) {return 42;}
        if (id == 44) {return 43;}
        if (id == 45) {return 44;}
        if (id == 46) {return 45;}
        if (id == 47) {return 46;}
        if (id == 48) {return 47;}
        if (id == 49) {return 48;}
        if (id == 50) {return 49;}
        if (id == 51) {return 50;}
        if (id == 52) {return 51;}
        if (id == 53) {return 52;}
        if (id == 54) {return 53;}
        if (id == 55) {return 54;}
        if (id == 56) {return 55;}
        if (id == 57) {return 56;}
        if (id == 58) {return 57;}
        if (id == 59) {return 58;}
        if (id == 60) {return 59;}
        if (id == 61) {return 60;}
        if (id == 62) {return 61;}
        if (id == 63) {return 62;}
        if (id == 64) {return 63;}
        if (id == 65) {return 64;}
        if (id == 66) {return 65;}
        if (id == 67) {return 66;}
        if (id == 69) {return 67;}
        if (id == 70) {return 69;}
        if (id == 71) {return 70;}
        if (id == 72) {return 71;}
        if (id == 73) {return 72;}
        if (id == 74) {return 73;}
        if (id == 75) {return 74;}
        if (id == 76) {return 75;}
        if (id == 77) {return 76;}
        if (id == 78) {return 77;}
        if (id == 79) {return 78;}
        if (id == 80) {return 79;}
        if (id == 81) {return 80;}
        if (id == 82) {return 81;}
        if (id == 83) {return 82;}
        if (id == 84) {return 83;}
        if (id == 85) {return 84;}
        if (id == 86) {return 85;}
        if (id == 87) {return 86;}
        if (id == 88) {return 87;}
        if (id == 89) {return 88;}
        if (id == 90) {return 89;}
        if (id == 91) {return 90;}
        if (id == 92) {return 91;}
        if (id == 93) {return 92;}
        if (id == 94) {return 93;}
        if (id == 95) {return 94;}
        if (id == 96) {return 95;}
        if (id == 97) {return 96;}
        if (id == 98) {return 97;}
        if (id == 99) {return 98;}
        if (id == 100) {return 99;}
        if (id == 101) {return 100;}
        if (id == 102) {return 101;}
        if (id == 103) {return 102;}
        if (id == 104) {return 103;}
        if (id == 106) {return 104;}
        if (id == 107) {return 105;}
        if (id == 109) {return 106;}
        if (id == 110) {return 107;}
        if (id == 111) {return 108;}
        if (id == 105) {return 109;}
        if (id == 112) {return 110;}
        if (id == 113) {return 111;}
        if (id == 108) {return 112;}
        if (id == 114) {return 113;}
        return id;
    }

    private void define_heroes() {
        allied_heroes[0] = Ah_id1;
        allied_heroes[1] = Ah_id2;
        allied_heroes[2] = Ah_id3;
        allied_heroes[3] = Ah_id4;
        allied_heroes[4] = Ah_id5;
        enemy_heroes[0] = Eh_id1;
        enemy_heroes[1] = Eh_id2;
        enemy_heroes[2] = Eh_id3;
        enemy_heroes[3] = Eh_id4;
        enemy_heroes[4] = Eh_id5;

        alliedIcons[0] = Ally1;
        alliedIcons[1] = Ally2;
        alliedIcons[2] = Ally3;
        alliedIcons[3] = Ally4;
        alliedIcons[4] = Ally5;
        enemyIcons[0] = Enemy1;
        enemyIcons[1] = Enemy2;
        enemyIcons[2] = Enemy3;
        enemyIcons[3] = Enemy4;
        enemyIcons[4] = Enemy5;

    }

    //Methods for filtering the hero list
    public String reorder(boolean clicked) {
        String str ="";
        count = count + 3;
        if(count % 4 == 0){
            if(user_stats) {
                Collections.sort(heroList, new CustomComparator());
            } else {
                Collections.sort(heroList, new BaseComparator());
            }
            str = "Win %";
            if(clicked){
                Toast.makeText(getApplicationContext(), "Sorting by Win %",
                        Toast.LENGTH_SHORT).show();
            }

        } else if(count % 4 == 1) {
            Collections.sort(heroList, new DoubleComparator());
            str = "Advantage";
            if(clicked) {
                Toast.makeText(getApplicationContext(), "Sorting by Advantage",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (count % 4 == 2) {
            Collections.sort(heroList, new SynergyComparator());
            str = "Synergy";
            if(clicked) {
                Toast.makeText(getApplicationContext(), "Sorting by Synergy",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (count % 4 == 3) {
            Collections.sort(heroList, new SynergyAdvantageComparator());
            str = "Adv + Syn";
            if (clicked) {
                Toast.makeText(getApplicationContext(), "Sorting by Advantage and Synergy",
                        Toast.LENGTH_SHORT).show();
            }
        }
        mListView1.setAdapter(adapter);

        return str;
    }

    public void selectRole(int val) {
        heroList.clear();

        for(int i = 0; i < heroListCopy.size(); i++) {
            heroList.add(i, heroListCopy.get(i));
        }

        role_selected = true;
        updateDesiredRole(val, selectedHeroList);

        if(role_selected) {
            heroList.retainAll(selectedHeroList);
        }

        count++;
        reorder(false);
        mListView1.setAdapter(adapter);
    }

    public void updateSelectedHeroesList(int val, List<Hero> dupeHeroList) {
        if(val != 0){
            dupeHeroList.add(new Hero(names[val],images[val], val, subnames[val]));
        }
    }

    public void updateDesiredRole(int val, List<Hero> selectedRoleList) {
        selectedRoleList.clear();
        if(val == 0) {
            selectedRoleList.clear();
            role_selected = false;
        } else {
            for(int i = 1; i < hero_role.length; i++) {
                if(hero_role[getCorrectId(i)][val] == 1) {
                    selectedRoleList.add(new Hero(names[i],images[i], i, subnames[i]));
                }
            }
        }
    }

    //Methods for resetting and initialising app
    public void loadData() {
        //SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();

        SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!preferences.getString("edittext_preference", "default_value").equals("default_value")){
            user_id = preferences.getString("edittext_preference", "default_value");
        } else {
            user_id = String.valueOf(0);
        }

        if(!tables_loaded){
            String advString = "Advantages";
            String synString = "Synergies";
            String vsEnemiesString = "Win_vs_enemies";
            String withAlliesString = "Win_synergies";
            String endString = "";
            String middleString = "_n";
            Log.d("PREF", preferences.getString("skill_setting_preference", "default_value"));
            Log.d("PREF", preferences.getString("time_setting_preference", "default_value"));
            if(preferences.getString("time_setting_preference", "default_value").equals("1")){
                endString ="";
            } else if(preferences.getString("time_setting_preference", "default_value").equals("2")){
                endString ="_700";
            } else {
                endString ="";
            }
            if(preferences.getString("skill_setting_preference", "default_value").equals("1")) {
                normal = true;
                middleString = "_n";

            } else if (preferences.getString("skill_setting_preference", "default_value").equals("2")) {
                high = true;
                middleString ="_h";
            } else if (preferences.getString("skill_setting_preference", "default_value").equals("3")) {
                veryHigh = true;
                middleString = "_vh";
            }
            Win_vs_enemies_table = myDb.advantageTableBuilder(vsEnemiesString + middleString + endString);
            Win_with_allies_table = myDb.advantageTableBuilder(withAlliesString + middleString + endString);
            Advantage_table = myDb.advantageTableBuilder(advString + middleString + endString);
            Synergy_table = myDb.advantageTableBuilder(synString + middleString + endString);
            populationWinRates = sc.winRateArrayBuilder(Win_vs_enemies_table);
            tables_loaded = true;
            /*
            if(preferences.getString("skill_setting_preference", "default_value").equals("1")) {
                normal = true;
                Advantage_table = myDb.advantageTableBuilder("Advantages_n");
                Synergy_table = myDb.advantageTableBuilder("Synergies_n");
                Win_vs_enemies_table = myDb.advantageTableBuilder("Win_vs_enemies_n");
                Win_with_allies_table = myDb.advantageTableBuilder("Win_synergies_n");
                populationWinRates = sc.winRateArrayBuilder(Win_vs_enemies_table);
            } else if (preferences.getString("skill_setting_preference", "default_value").equals("2")) {
                high = true;
                Advantage_table = myDb.advantageTableBuilder("Advantages_h");
                Synergy_table = myDb.advantageTableBuilder("Synergies_h");
                Win_vs_enemies_table = myDb.advantageTableBuilder("Win_vs_enemies_h");
                Win_with_allies_table = myDb.advantageTableBuilder("Win_synergies_h");
                populationWinRates = sc.winRateArrayBuilder(Win_vs_enemies_table);
            } else if (preferences.getString("skill_setting_preference", "default_value").equals("3")) {
                veryHigh = true;
                Advantage_table = myDb.advantageTableBuilder("Advantages_vh");
                Synergy_table = myDb.advantageTableBuilder("Synergies_vh");
                Win_vs_enemies_table = myDb.advantageTableBuilder("Win_vs_enemies_vh");
                Win_with_allies_table = myDb.advantageTableBuilder("Win_synergies_vh");
                populationWinRates = sc.winRateArrayBuilder(Win_vs_enemies_table);
            }
            */
            Log.d("adv string", advString + middleString + endString);
            Log.d("syn string", synString + middleString + endString);
            Log.d("vs string", vsEnemiesString + middleString + endString);
            Log.d("with string", withAlliesString + middleString + endString);
        }



        /*
        if(isParsable(preferences.getString("bayes_preference", "default_value"))){
            bayes_constant = Integer.parseInt(preferences.getString("bayes_preference", "default_value"));
        } else {
            bayes_constant = 3;
        }
        */
    }

    private void clear(){
        heroList.clear();
        role_selected = false;
        heroListCopy.clear();
        selected_role = 0;
        str = new double[hero_pool_size][hero_pool_size];
        ally_str = new double[hero_pool_size][hero_pool_size];
        temp = new double[hero_pool_size][hero_pool_size];
        hero_map = new HashMap<>();

        Enemy1.setImageResource(0);
        Enemy2.setImageResource(0);
        Enemy3.setImageResource(0);
        Enemy4.setImageResource(0);
        Enemy5.setImageResource(0);

        Ally1.setImageResource(0);
        Ally2.setImageResource(0);
        Ally3.setImageResource(0);
        Ally4.setImageResource(0);
        Ally5.setImageResource(0);

        hero_id = 0;
        Eh_id1 = 0;
        Eh_id2 = 0;
        Eh_id3 = 0;
        Eh_id4 = 0;
        Eh_id5 = 0;

        Ah_id1 = 0;
        Ah_id2 = 0;
        Ah_id3 = 0;
        Ah_id4 = 0;
        Ah_id5 = 0;
        define_heroes();

        allied_team_synergy = 0;
        allied_team_advantage = 0;
        enemy_team_synergy = 0;
        enemy_team_advantage = 0;

        setUpTeamAdvantagesAndSynergies();

        has_started = false;

        chosenHero = null;
        loadData();

        user_stats = true;

        initiateHeroJsonRetrieval("0", Ally5, against_hero);

        mListView1.setAdapter(adapter);
    }

    //Methods for parsing Json requests
    public static Map<Integer,List<Integer>> parseArray(JSONArray json){
        Map<Integer, List<Integer>> out = new HashMap<>();
        for (int i = 0; i < json.length(); i++){
            try {
                Integer hero_id = json.getJSONObject(i).getInt("hero_id");
                Integer wins = json.getJSONObject(i).getInt("win");
                Integer games = json.getJSONObject(i).getInt("games");
                Integer losses = games - wins;
                List<Integer> winLoss = new ArrayList<>();
                winLoss.add(wins);
                winLoss.add(losses);
                out.put(hero_id,winLoss);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return out;
    }







    public static boolean isParsable(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }

    public class JsonHeroTask extends AsyncTask<String, String, Map<Integer,List<Integer>>> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Map<Integer,List<Integer>> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                JSONArray jsonArray = new JSONArray((buffer.toString()));
                heroPopulationRates = parseArray(jsonArray);

                return heroPopulationRates;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map<Integer,List<Integer>> result) {
            super.onPostExecute(result);

            //getHeroWinRates(result);
            //if (pd.isShowing()){
            //    pd.dismiss();
            //}
            //  txtJson.setText(result);
        }

        public Map<Integer,List<Integer>> parseArray(JSONArray json){
            Map<Integer, List<Integer>> out = new HashMap<>();

            for (int i = 0; i < json.length(); i++){
                try {
                    Integer hero_id = json.getJSONObject(i).getInt("hero_id");
                    Integer onek_games = json.getJSONObject(i).getInt("1000_pick");
                    Integer onek_wins = json.getJSONObject(i).getInt("1000_win");
                    Integer twok_games = json.getJSONObject(i).getInt("2000_pick");
                    Integer twok_wins = json.getJSONObject(i).getInt("2000_win");
                    Integer threek_games = json.getJSONObject(i).getInt("3000_pick");
                    Integer threek_wins = json.getJSONObject(i).getInt("3000_win");
                    Integer fourk_games = json.getJSONObject(i).getInt("4000_pick");
                    Integer fourk_wins = json.getJSONObject(i).getInt("4000_win");
                    Integer fivek_games = json.getJSONObject(i).getInt("5000_pick");
                    Integer fivek_wins = json.getJSONObject(i).getInt("5000_win");
                    Integer total_wins = 0;
                    Integer total_games = 0;
                    //Integer losses = games - wins;
                    List<Integer> winLoss = new ArrayList<>();
                    if(normal){
                        total_wins = onek_wins+twok_wins;
                        total_games = onek_games+twok_games;
                        //Log.d("NORMAL", "HERE");
                        //winLoss.add(onek_wins);
                        //winLoss.add(onek_games);
                        //winLoss.add(twok_wins);
                        //winLoss.add(twok_games);
                    } else if(high){
                        total_wins = threek_wins;
                        total_games = threek_games;
                        //Log.d("HIGH", "HERE");
                        //winLoss.add(threek_wins);
                        //winLoss.add(threek_games);
                    } else if(veryHigh) {
                        total_wins = fourk_wins+fivek_wins;
                        total_games = fourk_games+fivek_games;
                        //Log.d("veryHIGH", "HERE");
                        //winLoss.add(fourk_wins);
                        //winLoss.add(fourk_games);
                        //winLoss.add(fivek_wins);
                        //winLoss.add(fivek_games);
                    }
                    winLoss.add(total_wins);
                    winLoss.add(total_games);

                    out.put(hero_id,winLoss);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return out;
        }
    }

    public static Map<Integer,List<Integer>> parseChosenArray(JSONArray json){
        Map<Integer, List<Integer>> out = new HashMap<>();
        for (int i = 0; i < json.length(); i++){
            try {
                Integer hero_id = json.getJSONObject(i).getInt("hero_id");
                Integer wins = json.getJSONObject(i).getInt("with_win");
                Integer games = json.getJSONObject(i).getInt("with_games");
                Integer againstWins = json.getJSONObject(i).getInt("against_win");
                Integer againstGames = json.getJSONObject(i).getInt("against_games");
                List<Integer> winLoss = new ArrayList<>();
                winLoss.add(wins);
                winLoss.add(games);
                winLoss.add(againstWins);
                winLoss.add(againstGames);
                out.put(hero_id,winLoss);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return out;
    }

    public Map<Integer,Double> getHeroWinRates(Map<Integer,List<Integer>> output){
        heroPopulationRates = output;
        Map<Integer, Double> populationWR = new HashMap<>();

        for (int i = 1; i < hero_pool_size; i++){
            if(i != 24){
                //Log.d("HEROPOP", String.valueOf(heroPopulationRates));
                double winRate = Double.valueOf(heroPopulationRates.get(i).get(0))/Double.valueOf(heroPopulationRates.get(i).get(1));
                populationWR.put(i, winRate);
            }

        }

        return populationWR;
    }

    public void setHeroWinRates(){
        JsonHeroTask jsonTask = new JsonHeroTask();
        jsonTask.execute("https://api.opendota.com/api/heroStats");
    }

    public void setChosenHeroWinRates(int ally5) {
        JsonChosenTask jsonTask = new JsonChosenTask();
        jsonTask.execute("https://api.opendota.com/api/players/47608793/heroes?hero_id=" + String.valueOf(ally5));
        Log.d("URL", "https://api.opendota.com/api/players/47608793/heroes?hero_id=" + String.valueOf(ally5));
    }

    public class JsonChosenTask extends AsyncTask<String, String, Map<Integer,List<Integer>>> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Map<Integer, List<Integer>> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                JSONArray jsonArray = new JSONArray((buffer.toString()));
                chosenHeroStats = parseChosenArray(jsonArray);

                return chosenHeroStats;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map<Integer, List<Integer>> result) {
            super.onPostExecute(result);

            //getHeroWinRates(result);
            //if (pd.isShowing()){
            //    pd.dismiss();
            //}
            //  txtJson.setText(result);
        }
    }

}




    /*


    private void shrinkBox() {
        Configuration configuration = this.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;
        int screenHeightDp = configuration.screenHeightDp;
        int smallestScreenWidthDp = configuration.smallestScreenWidthDp;
        Enemy1.getLayoutParams().width = (int) screenHeightDp/6;
        Enemy1.getLayoutParams().height = (int) 1.50*screenWidthDp/6;
        Enemy2.getLayoutParams().width = (int) screenHeightDp/6;
        Enemy2.getLayoutParams().height = (int) 1.50*screenWidthDp/6;
        Enemy3.getLayoutParams().width = (int) screenHeightDp/6;
        Enemy3.getLayoutParams().height = (int) 1.50*screenWidthDp/6;
        Enemy4.getLayoutParams().width = (int) screenHeightDp/6;
        Enemy4.getLayoutParams().height = (int) 1.50*screenWidthDp/6;
        Enemy5.getLayoutParams().width = (int) screenHeightDp/6;
        Enemy5.getLayoutParams().height = (int) 1.50*screenWidthDp/6;
    }

    public static Map<String,String> parse(JSONObject json , Map<String,String> out) throws JSONException {
        Iterator<String> keys = json.keys();

        while(keys.hasNext()){
            String key = keys.next();

            String val = null;
            try{
                JSONObject value = json.getJSONObject(key);
                parse(value,out);
            }catch(Exception e){
                val = json.getString(key);
            }

            if(val != null){
                out.put(key,val);
            }
        }
        return out;
    }


    */
/*
    private int getCorrectId(int id) {
        if (id == 10) {return 11;}
        if (id == 11) {return 10;}
        if (id == 24) {return 25;}
        if (id == 25) {return 31;}
        if (id == 31) {return 32;}
        if (id == 32) {return 33;}
        if (id == 33) {return 34;}
        if (id == 34) {return 35;}
        if (id == 35) {return 36;}
        if (id == 36) {return 37;}
        if (id == 37) {return 38;}
        if (id == 38) {return 39;}
        if (id == 39) {return 40;}
        if (id == 40) {return 41;}
        if (id == 41) {return 42;}
        if (id == 42) {return 43;}
        if (id == 43) {return 44;}
        if (id == 44) {return 45;}
        if (id == 45) {return 46;}
        if (id == 46) {return 47;}
        if (id == 47) {return 48;}
        if (id == 48) {return 49;}
        if (id == 49) {return 50;}
        if (id == 50) {return 51;}
        if (id == 51) {return 52;}
        if (id == 52) {return 53;}
        if (id == 53) {return 54;}
        if (id == 54) {return 55;}
        if (id == 55) {return 56;}
        if (id == 56) {return 57;}
        if (id == 57) {return 58;}
        if (id == 58) {return 59;}
        if (id == 59) {return 60;}
        if (id == 60) {return 61;}
        if (id == 61) {return 62;}
        if (id == 62) {return 63;}
        if (id == 63) {return 64;}
        if (id == 64) {return 65;}
        if (id == 65) {return 66;}
        if (id == 66) {return 67;}
        if (id == 67) {return 69;}
        if (id == 69) {return 70;}
        if (id == 70) {return 71;}
        if (id == 71) {return 72;}
        if (id == 72) {return 73;}
        if (id == 73) {return 74;}
        if (id == 74) {return 75;}
        if (id == 75) {return 76;}
        if (id == 76) {return 77;}
        if (id == 77) {return 78;}
        if (id == 78) {return 79;}
        if (id == 79) {return 80;}
        if (id == 80) {return 81;}
        if (id == 81) {return 82;}
        if (id == 82) {return 83;}
        if (id == 83) {return 84;}
        if (id == 84) {return 85;}
        if (id == 85) {return 86;}
        if (id == 86) {return 87;}
        if (id == 87) {return 88;}
        if (id == 88) {return 89;}
        if (id == 89) {return 90;}
        if (id == 90) {return 91;}
        if (id == 91) {return 92;}
        if (id == 92) {return 93;}
        if (id == 93) {return 94;}
        if (id == 94) {return 95;}
        if (id == 95) {return 96;}
        if (id == 96) {return 97;}
        if (id == 97) {return 98;}
        if (id == 98) {return 99;}
        if (id == 99) {return 100;}
        if (id == 100) {return 101;}
        if (id == 101) {return 102;}
        if (id == 102) {return 103;}
        if (id == 103) {return 104;}
        if (id == 104) {return 106;}
        if (id == 105) {return 107;}
        if (id == 106) {return 109;}
        if (id == 107) {return 110;}
        if (id == 111) {return 108;}
        if (id == 105) {return 109;}
        if (id == 112) {return 110;}
        if (id == 113) {return 111;}
        if (id == 108) {return 112;}
        if (id == 114) {return 113;}
        return id;

          subnames[1] = "AM Magina";
        subnames[2] = "Mogul Khan";
        subnames[3] = "Atropos";
        subnames[4] = "Strygwyr BS Seeker ";
        subnames[5] = "CM Rylai";
        subnames[6] = "Traxex";
        subnames[7] = "ES Raigor";
        subnames[8] = "Yurnero";
        subnames[9] = "POTM";
        subnames[11] = "Nevermore SF";
        subnames[10] = "Morphling";
        subnames[12] = "PL Azwraith";
        subnames[13] = "Puck Faerie Dragon";
        subnames[14] = "Pudge Butcher";
        subnames[15] = "Razor Lightning Revenant";
        subnames[16] = "SK Crixalis";
        subnames[17] = "SS Raijin";
        subnames[18] = "Sven Rogue Knight";
        subnames[19] = "Tiny Stone Giant";
        subnames[20] = "VS Shendelzare";
        subnames[21] = "WR Alleria Lyralei";
        subnames[22] = "Zeus Lord";
        subnames[23] = "Admiral";
        subnames[25] = "Slayer";
        subnames[31] = "Ethreain Kel'Thuzad";
        subnames[26] = "Demon Witch";
        subnames[27] = "SS Rhasta";
        subnames[28] = "Slithereen Guard";
        subnames[29] = "Leaviathan";
        subnames[30] = "WD Zharvakko Vol'jin";
        subnames[32] = "SA Stealth Assassin";
        subnames[33] = "Nig";
        subnames[34] = "Boush";
        subnames[35] = "Kardel Sharpeye Dwarf";
        subnames[36] = "Necrophos Rotund'jere";
        subnames[37] = "Demnok Lannik Lock";
        subnames[38] = "Karroch BM";
        subnames[39] = "QoP Akasha";
        subnames[40] = "Lesale Deathbringer";
        subnames[41] = "FV Darkterror Void";
        subnames[42] = "WK Osterion SK";
        subnames[43] = "DP Krobelus";
        subnames[44] = "PA Mortred";
        subnames[45] = "Oblivion";
        subnames[46] = "TA Lanaya";
        subnames[47] = "Netherdrake";
        subnames[48] = "Moon Rider";
        subnames[49] = "DK Davion";
        subnames[50] = "Shadow Priest";
        subnames[51] = "Rattletrap CG CW";
        subnames[52] = "TS";
        subnames[53] = "NP Furion Prophet";
        subnames[54] = "LS Naix";
        subnames[55] = "Ish'kafel DS Seer";
        subnames[56] = "Bone Fletcher BF ";
        subnames[57] = "Purist Thunderwrath";
        subnames[58] = "Aiushtha";
        subnames[59] = "Sacred Warrior SW";
        subnames[60] = "NS Balanar";
        subnames[61] = "BM Black Arachnia Spider";
        subnames[62] = "BH Gondar";
        subnames[63] = "SKitskurr NW Nerubian";
        subnames[64] = "THD Dragon Twin Headed";
        subnames[65] = "Jin'zakk";
        subnames[66] = "Holy Knight";
        subnames[67] = "Mercurial";
        subnames[69] = "Lucifer";
        subnames[68] = "AA Kaldr";
        subnames[70] = "Ulfsaar Warrior";
        subnames[71] = "SB Barathrum";
        subnames[72] = "Aurel Vlaicu";
        subnames[73] = "Razzil Darkbrew";
        subnames[74] = "Kael Carl voker";
        subnames[75] = "Nortrom";
        subnames[76] = "OD Destroyer Harbringer";
        subnames[77] = "Banehallow Wolf";
        subnames[78] = "Drunk Mangix Panda";
        subnames[79] = "SD Eredar";
        subnames[80] = "Bear LD Druid Sylla";
        subnames[81] = "CK Nessaj";
        subnames[82] = "Geomancer";
        subnames[83] = "Rooftrellen";
        subnames[84] = "Aggron Stonebreak";
        subnames[85] = "Dirge Almighty";
        subnames[86] = "Grand Magus";
        subnames[87] = "Stormcrafter Thrall";
        subnames[88] = "Nerubian Assassin Anub'arak";
        subnames[89] = "Slithice NS";
        subnames[90] = "Kotl Ezalor Old man";
        subnames[91] = "Io Guardian Wisp";
        subnames[92] = "Necro'lic";
        subnames[93] = "Fish Nightcrawler";
        subnames[94] = "Dusa Gorgon";
        subnames[95] = "Jah'rakal";
        subnames[96] = "Bradwarden Warchief";
        subnames[97] = "Mag";
        subnames[98] = "Rizzrack Shredder Goblin";
        subnames[99] = "BB Rigwarl";
        subnames[100] = "Ymir";
        subnames[101] = "Dragonus";
        subnames[102] = "Lord of Avernus Arthas";
        subnames[103] = "Tauren Chieftan ET";
        subnames[104] = "LC Tresdin";
        subnames[106] = "ES Xin";
        subnames[107] = "ES Kaolin";
        subnames[108] = "Vrogros Pitlord";
        subnames[109] = "TB Demon Marauder";
        subnames[110] = "Bird Fire";
        subnames[105] = "Squee Spleen Spoon Bomb";
        subnames[111] = "Nerif";
        subnames[112] = "WW Auroth";
        subnames[113] = "AC Zet";
        subnames[114] = "MK Wukong";
    }
    */



