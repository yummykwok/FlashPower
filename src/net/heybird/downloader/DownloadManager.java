package net.heybird.downloader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class DownloadManager extends Service{

    private List<DownloadTask> mTasks;
    private ArrayList<WeakReference<Notifier>> mNotifiers;
    private DmBinder dmBinder = new DmBinder();

    public class DmBinder extends Binder {
        public List<DownloadTask> getTaskList() {
            return mTasks;
        }

        public synchronized void registerForNotifier(Notifier noti) {
            if (noti != null) {
                removeCleared();
                mNotifiers.add(new WeakReference<Notifier>(noti));
            }
        }

        public synchronized void unregisterForNotifier(Notifier noti) {
            if (noti != null) {
                for (int i = mNotifiers.size()-1; i>=0; i--) {
                    if (mNotifiers.get(i).get().equals(noti)) {
                        mNotifiers.remove(i);
                    }
                }
            }
        }
    }

    public static interface Notifier {
        void onIncreaseDownloaded(int id, int size);
        void onDownloadFailed(int id, Exception e);
        void onTaskCompleted(int id);
        void onStarted(int id, int size);
    }

    private synchronized void removeCleared() {
        for (int i = mNotifiers.size()-1; i >= 0; i--) {
            if (mNotifiers.get(i).get() == null) {
                mNotifiers.remove(i);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return dmBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifiers = new ArrayList<WeakReference<Notifier>>();
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Load tasks from database
     */
    private void loadFromDb() {
        
    }

}
