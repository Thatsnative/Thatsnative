package com.example.epic.db;

import static com.example.epic.db.entity.HostsSource.USER_SOURCE_ID;
import static com.example.epic.db.entity.HostsSource.USER_SOURCE_URL;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.epic.R;
import com.example.epic.db.converter.ListTypeConverter;
import com.example.epic.db.converter.ZonedDateTimeConverter;
import com.example.epic.db.dao.HostEntryDao;
import com.example.epic.db.dao.HostListItemDao;
import com.example.epic.db.dao.HostsSourceDao;
import com.example.epic.db.dao.RuleDao;
import com.example.epic.db.dao.RuleListItemDao;
import com.example.epic.db.entity.HostEntry;
import com.example.epic.db.entity.HostListItem;
import com.example.epic.db.entity.HostsSource;
import com.example.epic.db.entity.RuleEntity;
import com.example.epic.db.entity.RuleListItem;
import com.example.epic.db.hosts.SourceHosts;
import com.example.epic.util.AppExecutors;

/**
 * This class is the application database based on Room.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
@Database(entities = {HostsSource.class, HostListItem.class, HostEntry.class, RuleListItem.class, RuleEntity.class}, version = 1)
@TypeConverters({ListTypeConverter.class, ZonedDateTimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    /**
     * The database singleton instance.
     */
    private static volatile AppDatabase instance;

    /**
     * Get the database instance.
     *
     * @param context The application context.
     * @return The database instance.
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app.db"
                            )
                            .createFromAsset("db/app.db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    AppExecutors.getInstance().diskIO().execute(
                                            () -> AppDatabase.initialize(context, instance)
                                    );
                                }
                            }).build();
                }
            }
        }
        return instance;
    }

    /**
     * Initialize the database content.
     */
    private static void initialize(Context context, AppDatabase database) {
        // Check if there is no hosts source
        HostsSourceDao hostsSourceDao = database.hostsSourceDao();
        if (!hostsSourceDao.getAll().isEmpty()) {
            return;
        }
        // User list
        HostsSource userSource = new HostsSource();
        userSource.setLabel(context.getString(R.string.hosts_user_source));
        userSource.setId(USER_SOURCE_ID);
        userSource.setUrl(USER_SOURCE_URL);
        userSource.setAllowEnabled(true);
        userSource.setRedirectEnabled(false);
        hostsSourceDao.insert(userSource);

        SourceHosts.getAllSources().forEach(hostsSourceDao::insert);
    }

    /**
     * Get the hosts source DAO.
     *
     * @return The hosts source DAO.
     */
    public abstract HostsSourceDao hostsSourceDao();

    /**
     * Get the hosts list item DAO.
     *
     * @return The hosts list item DAO.
     */
    public abstract HostListItemDao hostsListItemDao();

    /**
     * Get the hosts entry DAO.
     *
     * @return The hosts entry DAO.
     */
    public abstract HostEntryDao hostEntryDao();

    public abstract RuleListItemDao ruleListItemDao();

    public abstract RuleDao ruleDao();
}
