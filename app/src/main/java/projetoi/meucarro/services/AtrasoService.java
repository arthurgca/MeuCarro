package projetoi.meucarro.services;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.utils.CheckStatus;
import projetoi.meucarro.utils.StatusAdapterPlaceholder;

public class AtrasoService extends JobService {
    private HashMap manutencaoHash;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d("Job", "Job Started");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference ref = db.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataSnapshot carSnap = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid());
                String lastCarId = carSnap.child("lastCar").getValue().toString();
                CarroUser carroUser = carSnap.child("carrosList").child(lastCarId).getValue(CarroUser.class);


                DataSnapshot carrosDaMarca = dataSnapshot.child("carros").child(carroUser.marca);
                for (DataSnapshot ids : carrosDaMarca.getChildren()) {
                    if (ids.child("Modelo").getValue().toString().equals(carroUser.modelo)) {
                        manutencaoHash = (HashMap) ids.child("Manutenção").getValue();
                    }
                }

                showNotif(carroUser, manutencaoHash);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }

    private void showNotif(CarroUser carroUser, HashMap manutencaoHash) {
        ArrayList<StatusAdapterPlaceholder> list = new ArrayList<>();
        list.addAll(CheckStatus.checaStatus(manutencaoHash, carroUser));
        for (StatusAdapterPlaceholder i : list) {
            Log.d("Atraso", String.valueOf(i.isAtrasado()));
            if (i.isAtrasado()) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getBaseContext())
                                .setContentTitle("MeuCarro - Atraso")
                                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                                .setContentText(i.getManutencao()   );
                NotificationManager mNotificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());
            }
        }
    }
}