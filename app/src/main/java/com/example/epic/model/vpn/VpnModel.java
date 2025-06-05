package com.example.epic.model.vpn;

import static com.example.epic.model.adblocking.AdBlockMethod.VPN;
import static com.example.epic.model.error.HostError.ENABLE_VPN_FAIL;

import android.content.Context;
import android.util.LruCache;

import com.example.epic.R;
import com.example.epic.analityc.AnalyticLogger;
import com.example.epic.db.AppDatabase;
import com.example.epic.db.dao.HostEntryDao;
import com.example.epic.db.dao.RuleDao;
import com.example.epic.db.entity.HostEntry;
import com.example.epic.db.entity.RuleEntity;
import com.example.epic.helper.PreferenceHelper;
import com.example.epic.model.adblocking.AdBlockMethod;
import com.example.epic.model.adblocking.AdBlockModel;
import com.example.epic.model.error.HostErrorException;
import com.example.epic.vpn.VpnServiceControls;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import timber.log.Timber;

/**
 * This class is the model to represent VPN service configuration.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
public class VpnModel extends AdBlockModel {
    private final HostEntryDao hostEntryDao;
    private final RuleDao ruleDao;
    private final LruCache<String, HostEntry> blockCache;
    private final LruCache<String, RuleEntity> ruleBlockCache;
    private final LinkedHashSet<String> logs;
    private boolean recordingLogs;
    private int requestCount;
    private Context context;

    /**
     * Constructor.
     *
     * @param context The application context.
     */
    public VpnModel(Context context) {
        super(context);
        this.context = context;
        AppDatabase database = AppDatabase.getInstance(context);
        this.hostEntryDao = database.hostEntryDao();
        this.ruleDao = database.ruleDao();
        this.blockCache = new LruCache<String, HostEntry>(4 * 1024) {
            @Override
            protected HostEntry create(String key) {
                return VpnModel.this.hostEntryDao.getEntry(key);
            }
        };
        this.ruleBlockCache = new LruCache<String, RuleEntity>(4 * 1024) {
            @Override
            protected RuleEntity create(String key) {
                return VpnModel.this.ruleDao.matchRules(key);
            }
        };
        this.logs = new LinkedHashSet<>();
        this.recordingLogs = true;
        this.requestCount = 0;
        this.applied.postValue(VpnServiceControls.isRunning(context));
    }

    @Override
    public AdBlockMethod getMethod() {
        return VPN;
    }

    @Override
    public void apply() throws HostErrorException {
        if(!PreferenceHelper.isAuthorized(this.context)){
            return;
        }
        // Clear cache
        this.blockCache.evictAll();
        // Start VPN
        boolean started = VpnServiceControls.start(this.context);
        this.applied.postValue(started);
        if (!started) {
            throw new HostErrorException(ENABLE_VPN_FAIL);
        }
        setState(R.string.status_vpn_configuration_updated);
        PreferenceHelper.setIsVpnRunning(this.context,true);
        AnalyticLogger.INSTANCE.info("VPN protection enabled!");
    }

    @Override
    public void revert() {
        VpnServiceControls.stop(this.context);
        this.applied.postValue(false);
        PreferenceHelper.setIsVpnRunning(this.context,false);
        AnalyticLogger.INSTANCE.info("VPN protection disabled!");
    }

    @Override
    public boolean isRecordingLogs() {
        return this.recordingLogs;
    }

    @Override
    public void setRecordingLogs(boolean recording) {
        this.recordingLogs = recording;
    }

    @Override
    public List<String> getLogs() {
        return new ArrayList<>(this.logs);
    }

    @Override
    public void clearLogs() {
        this.logs.clear();
    }

    /**
     * Checks host entry related to an host name.
     *
     * @param host A hostname to check.
     * @return The related host entry.
     */
    public HostEntry getEntry(String host) {
        // Compute miss rate periodically
        this.requestCount++;
        if (this.requestCount >= 1000) {
            int hits = this.blockCache.hitCount();
            int misses = this.blockCache.missCount();
            double missRate = 100D * (hits + misses) / misses;
            Timber.d("Host cache miss rate: %s.", missRate);
            this.requestCount = 0;
        }
        // Add host to logs
        if (this.recordingLogs) {
            this.logs.add(host);
        }
        // Check cache
        return this.blockCache.get(host);
    }

    public RuleEntity getRuleEntry(String host) {
        // Compute miss rate periodically
        this.requestCount++;
        if (this.requestCount >= 1000) {
            int hits = this.ruleBlockCache.hitCount();
            int misses = this.ruleBlockCache.missCount();
            double missRate = 100D * (hits + misses) / misses;
            Timber.d("Host cache miss rate: %s.", missRate);
            this.requestCount = 0;
        }
        // Add host to logs
        if (this.recordingLogs) {
            this.logs.add(host);
        }
        // Check cache
        return this.ruleBlockCache.get(host);
    }
}
