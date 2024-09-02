package com.xaniapp.xani;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava2.RxDataStore;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.xaniapp.xani.business.CryptographyBusiness;
import com.xaniapp.xani.business.DatastoreBusiness;
import com.xaniapp.xani.databinding.ActivityMainBinding;
import com.xaniapp.xani.dataaccess.AppDatabase;
import com.xaniapp.xani.entites.da.Post;
import com.xaniapp.xani.entites.da.User;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static AppDatabase appDatabase;
    RxDataStore<Preferences> dataStoreRX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /* this will be changed for a settings form eventually */
        var datastore = DatastoreBusiness.getInstance(this);

        var username = datastore.getStringValue(DatastoreBusiness.Key.USERNAME);
        var hash = datastore.getStringValue(DatastoreBusiness.Key.HASH);
        if (username == null) {
            datastore.setStringValue(DatastoreBusiness.Key.USERNAME, "Death");
        }

        if (hash == null) {
            var encrypted = CryptographyBusiness.getSHA256("FreeBeer");
            datastore.setStringValue(DatastoreBusiness.Key.HASH, encrypted.data);
        }


        var apiThread = new Thread(() -> {

           appDatabase = AppDatabase.getDatabase(this);
           var userDao = appDatabase.userDao();
           var postDao = appDatabase.postDao();

           //String currentDBPath=getDatabasePath("xani.db").getAbsolutePath();

            var user = new User();
            user.u_id = 2;
            user.u_username = "@grouchydouglas1";

            var d = userDao.upsertData(user);
            var x = userDao.getUser(5);
            var y = userDao.getAll();


           var calendar =  Calendar.getInstance();
           var post = new Post();
           post.p_id = 3;
           post.p_content = "All together";
           post.p_datetime_created = calendar.getTime();
           postDao.upsertData(post);

           var allPosts = postDao.getAll();

         //  appDatabase.query("")

        });
        apiThread.start();






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}