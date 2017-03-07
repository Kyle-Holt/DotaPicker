package com.example.kyle.dotapicker;

import android.os.AsyncTask;
import android.util.Log;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTask extends AsyncTask<String, String, Map<Integer,List<Integer>>> {

    protected void onPreExecute() {
        super.onPreExecute();

        //ProgressDialog pd = new ProgressDialog(MainActivity.class);
        //pd.setMessage("Please Wait");
        //pd.setCancelable(false);
        //pd.show();
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
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

            }

            JSONArray jsonArray = new JSONArray((buffer.toString()));
            Map<Integer,List<Integer>> heroPopulationRates = parseArray(jsonArray);

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
        //if (pd.isShowing()){
        //    pd.dismiss();
        //}
      //  txtJson.setText(result);
    }

    public static Map<Integer,List<Integer>> parseArray(JSONArray json){
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
                Integer total_wins = onek_wins+twok_wins+threek_wins+fourk_wins+fivek_wins;
                Integer total_games = onek_games+twok_games+threek_games+fourk_games+fivek_games;
                //Integer losses = games - wins;
                List<Integer> winLoss = new ArrayList<>();
                winLoss.add(onek_wins);
                winLoss.add(onek_games);
                winLoss.add(twok_wins);
                winLoss.add(twok_games);
                winLoss.add(threek_wins);
                winLoss.add(threek_games);
                winLoss.add(fourk_wins);
                winLoss.add(fourk_games);
                winLoss.add(fivek_wins);
                winLoss.add(fivek_games);
                winLoss.add(total_wins);
                winLoss.add(total_games);
                out.put(hero_id,winLoss);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.d("MONKEY KING", out.get(114).toString());
        return out;
    }
}

