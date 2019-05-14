package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.LoginActivity2;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.fragment.AllQuizFragment;
import com.kcirqueit.playandearn.fragment.DashboardFragment;
import com.kcirqueit.playandearn.model.User;
import com.squareup.picasso.Picasso;

public class FragmentContainerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = FragmentContainerActivity.class.getSimpleName();

    @BindView(R.id.dash2_toolbar)
    Toolbar mToolbar;

    private ActionBarDrawerToggle drawerToggle;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserRef;

    private User user;

    private CircleImageView headerUserIv;

    private TextView mUserNameTv, mUserEmailTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        setSupportActionBar(mToolbar);

        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        String allQuizFragment = getIntent().getStringExtra("allQuizFragment");

        if (allQuizFragment != null) {

            if (allQuizFragment.equals("allQuizFragment")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new AllQuizFragment());
                ft.commit();
            }

        } else {
            addFragment();
        }

    }

    public void addFragment () {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new DashboardFragment());
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        initHeaderView();

        mUserRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.getValue() != null) {
                    user = dataSnapshot.getValue(User.class);

                    if (!user.getPhotoUrl().equals("default")) {
                        Picasso.get().load(user.getPhotoUrl())
                                .placeholder(R.drawable.user_avater).into(headerUserIv);
                    }
                    mUserNameTv.setText(user.getUserName());
                    mUserEmailTv.setText(user.getEmail());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toException().toString());
            }

        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.menu_share:

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                String subject = "Novel App";
                String body = "If you love to read novel then the app is for you.";

                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, body);

                startActivity(Intent.createChooser(intent, "Share with"));
                mDrawerLayout.closeDrawers();
                break;

            case R.id.menu_rate:
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=K+Cirque+Apps"));
                startActivity(intent2);
                mDrawerLayout.closeDrawers();
                break;

            case R.id.menu_more_apps:
                Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=K+Cirque+Apps"));
                startActivity(intent3);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.menu_logout:
                mAuth.signOut();
                Intent loginIntent = new Intent(this, LoginActivity2.class);
                startActivity(loginIntent);
                finish();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.menu_home:
                addFragment();
                mDrawerLayout.closeDrawers();
                break;
        }
        return true;
    }

    public void initHeaderView() {

        View view = mNavigationView.getHeaderView(0);

        headerUserIv = view.findViewById(R.id.user_iv);
        mUserNameTv = view.findViewById(R.id.user_name_tv);
        mUserEmailTv = view.findViewById(R.id.user_email_tv);

    }
}
