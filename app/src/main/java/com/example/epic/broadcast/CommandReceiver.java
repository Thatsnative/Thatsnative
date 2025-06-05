package com.example.epic.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.epic.AdAwayApplication;
import com.example.epic.model.adblocking.AdBlockModel;
import com.example.epic.model.error.HostErrorException;
import com.example.epic.util.AppExecutors;

import timber.log.Timber;

/**
 * This broadcast receiver listens to commands from broadcast.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
public class CommandReceiver extends BroadcastReceiver {
    /**
     * This action allows to send commands to the application. See {@link Command} for extra values.
     */
    public static final String SEND_COMMAND_ACTION = "com.example.epic.action.SEND_COMMAND";
    private static final AppExecutors EXECUTORS = AppExecutors.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SEND_COMMAND_ACTION.equals(intent.getAction())) {
            AdBlockModel adBlockModel = ((AdAwayApplication) context.getApplicationContext()).getAdBlockModel();
            Command command = Command.readFromIntent(intent);
            Timber.i("CommandReceiver invoked with command %s.", command);
            EXECUTORS.diskIO().execute(() -> executeCommand(adBlockModel, command));
        }
    }

    private void executeCommand(AdBlockModel adBlockModel, Command command) {
        try {
            switch (command) {
                case START:
                    adBlockModel.apply();
                    break;
                case STOP:
                    adBlockModel.revert();
                    break;
                case UNKNOWN:
                    Timber.i("Failed to run an unsupported command.");
                    break;
            }
        } catch (HostErrorException e) {
            Timber.w(e, "Failed to apply ad block command " + command + ".");
        }
    }
}
