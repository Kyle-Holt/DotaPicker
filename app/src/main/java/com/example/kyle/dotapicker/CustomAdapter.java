package com.example.kyle.dotapicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import static com.example.kyle.dotapicker.MainActivity.Ah_id1;
import static com.example.kyle.dotapicker.MainActivity.Ah_id2;
import static com.example.kyle.dotapicker.MainActivity.Ah_id3;
import static com.example.kyle.dotapicker.MainActivity.Ah_id4;
import static com.example.kyle.dotapicker.MainActivity.Eh_id1;
import static com.example.kyle.dotapicker.MainActivity.Eh_id2;
import static com.example.kyle.dotapicker.MainActivity.Eh_id3;
import static com.example.kyle.dotapicker.MainActivity.Eh_id4;
import static com.example.kyle.dotapicker.MainActivity.Eh_id5;

/**
 * Created by Kyle on 2/15/2017.
 */
public class CustomAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Hero> heroList;
    DecimalFormat numberFormat = new DecimalFormat("#.00");


    public CustomAdapter(Activity activity, List<Hero> heroList) {
        this.activity = activity;
        this.heroList = heroList;
    }

    public CustomAdapter() {
        //this is strictly to access the colouring methods
    }
    @Override
    public int getCount() {
        return heroList.size();
    }

    @Override
    public Object getItem(int position) {
        return heroList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SpannableStringBuilder sb;
        SpannableStringBuilder db;


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        ImageView image = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView ally_header = (TextView) convertView.findViewById(R.id.ally_header);
        TextView ally_bayes = (TextView) convertView.findViewById(R.id.allyBayes);
        TextView enemy_bayes = (TextView) convertView.findViewById(R.id.enemyBayes);
        TextView adv = (TextView) convertView.findViewById(R.id.advantage);
        TextView syn = (TextView) convertView.findViewById(R.id.synergy);
        TextView enemy_header = (TextView) convertView.findViewById(R.id.enemy_header);
        TextView total_rating = (TextView) convertView.findViewById(R.id.total_rating);
        TextView total_header = (TextView) convertView.findViewById(R.id.total_header);

        Hero h = heroList.get(position);

        // thumbnail image
        image.setImageResource(h.getIcon());

        // vs Enemy header
        enemy_header.setText("Stats vs Enemies");

        // total header
        total_header.setText("Total");

        // name
        title.setText(h.getName());

        // with allies header
        ally_header.setText("Stats with Allies");

        // set total rating
        if(Ah_id1 + Ah_id2 + Ah_id3 + Ah_id4 == 0){
            sb = determineColor((h.getBayesEnemyWR()*100), "", "%", 50);
            db = checkColor((h.getBayesEnemyWR()*100), sb, 0);
        } else if(Eh_id1 + Eh_id2 + Eh_id3 + Eh_id4 + Eh_id5 == 0){
            sb = determineColor((h.getBayesAlliedWR()*100), "", "%", 50);
            db = checkColor((h.getBayesAlliedWR()*100), sb, 0);
        } else {
            sb = determineColor((h.getBayesAlliedWR()*100+ h.getBayesEnemyWR()*100)/2, "", "%", 50);
            db = checkColor((h.getBayesAlliedWR()*100+ h.getBayesEnemyWR()*100)/2, sb, 0);
        }
        total_rating.setText(db);

        // get ally rating
        sb = determineColor(h.getBayesAlliedWR()*100, "Adj. Win %: ", "%", 50);
        db = checkColor(h.getBayesAlliedWR()*100, sb, 0);
        ally_bayes.setText(db);

        // get enemy rating
        sb = determineColor(h.getBayesEnemyWR()*100, "Adj. Win %: ", "%", 50);
        db = checkColor(h.getBayesEnemyWR()*100, sb, 0);
        enemy_bayes.setText(db);


        // set advantage
        if(h.getAdvantage() != null) {
            sb = determineColor(h.getAdvantage(), "Advantage: ", "%", 0);
            adv.setText(sb);
        }

        // set synergy
        if(h.getSynergy() != null) {
            sb = determineColor(h.getSynergy(), "Synergy: ", "%", 0);
            syn.setText(sb);
        }

        return convertView;
    }

    public SpannableStringBuilder determineColor(double hero_stat, String start_text, String end_text, int border) {
        String value = String.valueOf(numberFormat.format(hero_stat));
        String start = start_text;
        String end = end_text;
        final ForegroundColorSpan fcs;
        final SpannableStringBuilder sb = new SpannableStringBuilder(start + value + end);

        if(hero_stat > border) {
            fcs = new ForegroundColorSpan(Color.parseColor("#006400"));
        } else if (hero_stat < border) {
            fcs = new ForegroundColorSpan(Color.RED);
        } else {
            fcs = new ForegroundColorSpan(Color.parseColor("#888888"));
        }
        sb.setSpan(fcs, start.length(), start.length() + value.length() + end.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    public SpannableStringBuilder checkColor(double hero_stat, SpannableStringBuilder sb, int check) {
        String str = sb.toString();
        final ForegroundColorSpan fcs;
        SpannableStringBuilder db = new SpannableStringBuilder(str);
        if(hero_stat == check) {
            fcs = new ForegroundColorSpan(Color.parseColor("#888888"));
            db.setSpan(fcs, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return db;
        }
        return sb;
    }

}
