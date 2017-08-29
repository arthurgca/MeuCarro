package projetoi.meucarro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        createHomeFragment();

    }

    private void createHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        HomeFragment fragment = new HomeFragment();

        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment
        ).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_perfil) {
            fragment = new ProfileFragment();

        } else if (id == R.id.nav_trocarcarro) {
            fragment = new TrocarCarroFragment();

        } else if (id == R.id.nav_adicionar_carro) {
            fragment = new AdicionarCarroFragment();
        }else if (id == R.id.nav_calculadorabicombustiavel) {
            fragment = new GasCalculatorFragment();
        }else if (id == R.id.nav_relatoriogastos) {
            Intent ex = new Intent(MainActivity.this, ExpensesReportActivity.class);
            ex.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(ex);
        }else if (id == R.id.nav_statuscarro) {
            fragment = new CarroStatusFragment();
        }else if (id == R.id.nav_oficinas) {
            fragment = new OficinasFragment();
        }else if (id == R.id.nav_compararcarros) {
            fragment = new CompararCarroFragment();
        } else if (id == R.id.nav_marketplace) {
            fragment = new MarketplaceFragment();
        } else if (id == R.id.nav_logout) {
            Intent it = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(it);
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();


            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
