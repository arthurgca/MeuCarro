package projetoi.meucarro.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

import projetoi.meucarro.services.AtrasoService;

/**
 * Created by Arthur on 11/07/2017.
 */

public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                .setTrigger(Trigger.executionWindow(60, 2*60))
                .setService(AtrasoService.class) // the JobService that will be called
                .setTag("atraso")        // uniquely identifies the job
                .build();

        dispatcher.mustSchedule(myJob);
    }

}