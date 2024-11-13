package com.example.appvehiculos;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.appvehiculos.databinding.ActivityMain3Binding;
import com.example.appvehiculos.work.NotifyUserWorker;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class MainActivity3 extends AppCompatActivity {

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain3Binding binding;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Configuración del Navigation Drawer
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_promociones, R.id.nav_vehiculos, R.id.nav_promoactivas, R.id.nav_promocrear, R.id.nav_vehiculoscrear, R.id.nav_documentos)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Obtener el tipo de usuario desde el intent
        userType = getIntent().getStringExtra("USER_TYPE");
        if (userType != null) {
            setupSidebar(userType);
        }

        // Mostrar el nombre y correo del usuario en el Navigation Drawer
        showUserInfo();

        // Solicitar permiso de notificaciones si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            } else {
                // Programar el Worker para notificaciones semanales
                scheduleWeeklyNotification();
            }
        } else {
            // Programar el Worker para notificaciones semanales en versiones anteriores
            scheduleWeeklyNotification();
        }
    }

    private void scheduleWeeklyNotification() {
        WorkRequest notificationWorkRequest =
                new PeriodicWorkRequest.Builder(NotifyUserWorker.class, 7, TimeUnit.DAYS) // Cambiado a 7 días para notificaciones semanales
                        .build();

        WorkManager.getInstance(this).enqueue(notificationWorkRequest);
    }

/* Esto es para cada 15 minutos
    private void scheduleWeeklyNotification() {
        WorkRequest notificationWorkRequest =
                new PeriodicWorkRequest.Builder(NotifyUserWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueue(notificationWorkRequest);
    }
*/


    private void setupSidebar(String userType) {
        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            Menu menu = navigationView.getMenu();

            if ("usuario".equals(userType)) {
                // Mostrar opciones para "usuario"
                menu.findItem(R.id.nav_home).setVisible(true);
                menu.findItem(R.id.nav_gallery).setVisible(true);
                menu.findItem(R.id.nav_vehiculos).setVisible(true);
                menu.findItem(R.id.nav_vehiculoscrear).setVisible(true);
                menu.findItem(R.id.nav_promociones).setVisible(true);
                menu.findItem(R.id.nav_documentos).setVisible(true);

                // Ocultar opciones de "taller"
                menu.findItem(R.id.nav_promocrear).setVisible(false);
                menu.findItem(R.id.nav_promoactivas).setVisible(false);
            } else if ("taller".equals(userType)) {
                // Mostrar opciones para "taller"
                menu.findItem(R.id.nav_home).setVisible(true);
                menu.findItem(R.id.nav_promocrear).setVisible(true);
                menu.findItem(R.id.nav_promoactivas).setVisible(true);

                // Ocultar opciones de "usuario"
                menu.findItem(R.id.nav_gallery).setVisible(false);
                menu.findItem(R.id.nav_vehiculos).setVisible(false);
                menu.findItem(R.id.nav_vehiculoscrear).setVisible(false);
                menu.findItem(R.id.nav_promociones).setVisible(false);
                menu.findItem(R.id.nav_documentos).setVisible(false);
            }
        }
    }

    private void showUserInfo() {
        // Obtener el ID del usuario autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener el nombre y correo desde Firestore
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String userName = task.getResult().getString("name");
                        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        // Acceder a las vistas mediante ViewBinding
                        View headerView = binding.navView.getHeaderView(0);
                        TextView nameTextView = headerView.findViewById(R.id.userNameTextView);
                        TextView emailTextView = headerView.findViewById(R.id.userEmailTextView);

                        // Establecer el nombre y correo en las vistas
                        nameTextView.setText(userName);
                        emailTextView.setText(userEmail);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity3, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, programar el Worker
                scheduleWeeklyNotification();
            } else {
                // Permiso denegado
                // Puedes mostrar un mensaje al usuario o manejar la denegación de permisos aquí
            }
        }
    }
}
