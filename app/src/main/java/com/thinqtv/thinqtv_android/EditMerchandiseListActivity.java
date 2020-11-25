package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditMerchandiseListActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_merchandise_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Button addMerchandiseButton = findViewById(R.id.addMerchandiseButton);
        addMerchandiseButton.setOnClickListener(v -> {
            Intent i = new Intent(this, AddMerchandiseActivity.class);
            i.putExtra("edit", false);
            startActivity(i);
        });

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_layout, new MerchandiseListFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
