package com.example.kyle.dotapicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.example.kyle.dotapicker.MainActivity.Ah_id1;
import static com.example.kyle.dotapicker.MainActivity.Ah_id2;
import static com.example.kyle.dotapicker.MainActivity.Ah_id3;
import static com.example.kyle.dotapicker.MainActivity.Ah_id4;
import static com.example.kyle.dotapicker.MainActivity.Eh_id1;
import static com.example.kyle.dotapicker.MainActivity.Eh_id2;
import static com.example.kyle.dotapicker.MainActivity.Eh_id3;
import static com.example.kyle.dotapicker.MainActivity.Eh_id4;
import static com.example.kyle.dotapicker.MainActivity.Eh_id5;
import static com.example.kyle.dotapicker.MainActivity.hero_pool_size;
import static com.example.kyle.dotapicker.MainActivity.images;
import static com.example.kyle.dotapicker.MainActivity.names;
import static com.example.kyle.dotapicker.MainActivity.subnames;

public class SearchActivity extends AppCompatActivity {

    private ListView listView;
    private MyAppAdapter myAppAdapter;
    private List<Hero> heroArrayList;
    private List<Hero> dupeHeroList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = (ListView) findViewById(R.id.listViewSearch);
        heroArrayList = new ArrayList<>();
        for(int i = 1; i < hero_pool_size; i++){
            if(i != 24){
                heroArrayList.add(new Hero(names[i],images[i], i, subnames[i]));
            }
        }

        updateSelectedHeroesList(Eh_id1);
        updateSelectedHeroesList(Eh_id2);
        updateSelectedHeroesList(Eh_id3);
        updateSelectedHeroesList(Eh_id4);
        updateSelectedHeroesList(Eh_id5);

        updateSelectedHeroesList(Ah_id1);
        updateSelectedHeroesList(Ah_id2);
        updateSelectedHeroesList(Ah_id3);
        updateSelectedHeroesList(Ah_id4);


        heroArrayList.removeAll(dupeHeroList);
        Collections.sort(heroArrayList, new Comparator<Hero>() {
            @Override
            public int compare(final Hero object1, final Hero object2) {
                return object1.getName().compareTo(object2.getName());
            }
        });

        myAppAdapter = new MyAppAdapter(heroArrayList, SearchActivity.this);
        listView.setAdapter(myAppAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                LinearLayout ll = (LinearLayout) view;
                TextView tv = (TextView) ll.findViewById(R.id.hero_id);
                final String text = tv.getText().toString();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",text);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

                SearchActivity.this.finish();
            }
        });
    }

    public class MyAppAdapter extends BaseAdapter {

        public class ViewHolder {
            TextView txtTitle, txtId, txtSubTitle;
        }

        public List<Hero> heroSearchList;

        public Context context;
        ArrayList<Hero> arraylist;

        private MyAppAdapter(List<Hero> apps, Context context) {
            this.heroSearchList = apps;
            this.context = context;
            arraylist = new ArrayList<Hero>();
            arraylist.addAll(heroSearchList);

        }

        @Override
        public int getCount() {
            return heroSearchList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            ViewHolder viewHolder;

            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_view_search, null);

                viewHolder = new ViewHolder();
                viewHolder.txtTitle = (TextView) rowView.findViewById(R.id.hero_title);
                viewHolder.txtSubTitle = (TextView) rowView.findViewById(R.id.subtitle);
                viewHolder.txtId = (TextView) rowView.findViewById(R.id.hero_id);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.txtTitle.setText(heroSearchList.get(position).getName() + "");
            viewHolder.txtSubTitle.setText(heroSearchList.get(position).getSubnames() + "");
            viewHolder.txtId.setText(heroSearchList.get(position).getId() + "");
            return rowView;


        }

        public void filter(String charText) {


            charText = charText.toLowerCase(Locale.getDefault());

            heroSearchList.clear();
            if (charText.length() == 0) {
                heroSearchList.addAll(arraylist);

            } else {
                for (Hero postDetail : arraylist) {
                    if (charText.length() != 0 && postDetail.getName().toLowerCase(Locale.getDefault()).startsWith(charText)) {
                        heroSearchList.add(postDetail);
                    } else if (charText.length() != 0 && postDetail.getSubnames().toLowerCase(Locale.getDefault()).contains(charText)) {
                        heroSearchList.add(postDetail);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    showInputMethod(v.findFocus());

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                myAppAdapter.filter(searchQuery.toString().trim());
                listView.invalidate();
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });
        return true;
    }

    private void showInputMethod(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(v, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.hero_title) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateSelectedHeroesList(int val) {
        if(val != 0){
            dupeHeroList.add(new Hero(names[val],images[val], val, subnames[val]));
        }
    }


}
