package com.kcirqueit.playandearn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;

import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.fragment.DashboardFragment;

public class FragmentContainerActivity extends AppCompatActivity {

    @BindView(R.id.dash2_toolbar)
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Quiz Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addFragment();
    }

    public void addFragment () {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new DashboardFragment());
        ft.commit();
    }
}
