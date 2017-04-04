package com.example.kyle.dotapicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setTitle("About");

        TextView myContact = (TextView) findViewById(R.id.my_email);
        myContact.setOnClickListener(this);
        TextView erskinContact = (TextView) findViewById(R.id.erskin_contact);
        erskinContact.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {

            case R.id.my_email:
                intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","kyle.holt387@gmail.com", null));
                //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                //intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                break;
            case R.id.erskin_contact:
                intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","erskin@eldrich.org", null));
                //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                //intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                break;
        }
    }

}
