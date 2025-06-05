package com.example.epic.model.backup;

import static com.example.epic.db.entity.ListType.ALLOWED;
import static com.example.epic.db.entity.ListType.BLOCKED;
import static com.example.epic.db.entity.ListType.REDIRECTED;
import static com.example.epic.model.backup.BackupFormat.ALLOWED_KEY;
import static com.example.epic.model.backup.BackupFormat.BLOCKED_KEY;
import static com.example.epic.model.backup.BackupFormat.REDIRECTED_KEY;
import static com.example.epic.model.backup.BackupFormat.SOURCES_KEY;
import static com.example.epic.model.backup.BackupFormat.hostFromJson;
import static com.example.epic.model.backup.BackupFormat.sourceFromJson;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.UiThread;

import com.example.epic.R;
import com.example.epic.db.AppDatabase;
import com.example.epic.db.dao.HostListItemDao;
import com.example.epic.db.dao.HostsSourceDao;
import com.example.epic.db.entity.HostListItem;
import com.example.epic.db.entity.ListType;
import com.example.epic.util.AppExecutors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.Executor;

import timber.log.Timber;

/**
 * This class is a helper class to import user lists and hosts sources to a backup file.<br>
 * Importing a file source will no restore read access from Storage Access Framework.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
public final class BackupImporter {

    private BackupImporter() {

    }

    private static final Executor DISK_IO_EXECUTOR = AppExecutors.getInstance().diskIO();
    private static final Executor MAIN_THREAD_EXECUTOR = AppExecutors.getInstance().mainThread();

    /**
     * Import a backup file.
     *
     * @param context   The application context.
     * @param backupUri The URI of a backup file.
     */
    @UiThread
    public static void importFromBackup(Context context, Uri backupUri) {
        DISK_IO_EXECUTOR.execute(() -> {
            boolean imported = true;
            try {
                importBackup(context, backupUri);
            } catch (IOException e) {
                Timber.e(e, "Failed to import backup.");
                imported = false;
            }
            boolean successful = imported;
            MAIN_THREAD_EXECUTOR.execute(() -> notifyImportEnd(context, successful));
        });
    }

    @UiThread
    private static void notifyImportEnd(Context context, boolean successful) {
        Toast.makeText(
                context,
                context.getString(successful ? R.string.import_success : R.string.import_failed),
                Toast.LENGTH_LONG
        ).show();
    }

    static void importBackup(Context context, Uri backupUri) throws IOException {
        try (InputStream inputStream = context.getContentResolver().openInputStream(backupUri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
            JSONObject backupObject = new JSONObject(contentBuilder.toString());
            importBackup(context, backupObject);
        } catch (JSONException exception) {
            throw new IOException("Failed to parse backup file.", exception);
        } catch (FileNotFoundException exception) {
            throw new IOException("Failed to find backup file.", exception);
        } catch (IOException exception) {
            throw new IOException("Failed to read backup file.", exception);
        }
    }

    private static void importBackup(Context context, JSONObject backupObject) throws JSONException {
        AppDatabase database = AppDatabase.getInstance(context);
        HostsSourceDao hostsSourceDao = database.hostsSourceDao();
        HostListItemDao hostListItemDao = database.hostsListItemDao();

        importSourceBackup(hostsSourceDao, backupObject.getJSONArray(SOURCES_KEY));
        importListBackup(hostListItemDao, BLOCKED, backupObject.getJSONArray(BLOCKED_KEY));
        importListBackup(hostListItemDao, ALLOWED, backupObject.getJSONArray(ALLOWED_KEY));
        importListBackup(hostListItemDao, REDIRECTED, backupObject.getJSONArray(REDIRECTED_KEY));
    }

    private static void importSourceBackup(HostsSourceDao hostsSourceDao, JSONArray sources) throws JSONException {
        for (int index = 0; index < sources.length(); index++) {
            JSONObject sourceObject = sources.getJSONObject(index);
            hostsSourceDao.insert(sourceFromJson(sourceObject));
        }
    }

    private static void importListBackup(HostListItemDao hostListItemDao, ListType type, JSONArray hosts) throws JSONException {
        for (int index = 0; index < hosts.length(); index++) {
            JSONObject hostObject = hosts.getJSONObject(index);
            HostListItem host = hostFromJson(hostObject);
            host.setType(type);
            Optional<Integer> id = hostListItemDao.getHostId(host.getHost());
            if (id.isPresent()) {
                host.setId(id.get());
                hostListItemDao.update(host);
            } else {
                hostListItemDao.insert(host);
            }
        }
    }
}
