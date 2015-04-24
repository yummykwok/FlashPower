package net.heybird.downloader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import net.heybird.downloader.DownloadManager.Notifier;

public class DownloadTask extends Handler{
    
    public static final int MSG_PROGRESS_INC = 1;
    public static final int MSG_THREAD_FAILURE = 2;
    public static final int MSG_THREAD_COMPLETE = 3;

    public static enum TaskState {
        READY,
        STARTED,
        PAUSED,
        CANCELED,
        COMPLETED,
        DELETED,
        FAILED;
        public static TaskState valueOf(int ordinal) {
            TaskState res = null;
            switch (ordinal) {
                case 0:
                    res = READY;
                    break;
                case 1:
                    res = STARTED;
                    break;
                case 2:
                    res = PAUSED;
                    break;
                case 3:
                    res = CANCELED;
                    break;
                case 4:
                    res = COMPLETED;
                    break;
                case 5:
                    res = DELETED;
                    break;
                case 6:
                    res = FAILED;
                    break;
            }
            return res;
        }
    }

    private int id;
    private String name;
    private String url;
    private String path;
    private int size;
    private int downloaded;
    private TaskState state;
    private String time;
    private List<TaskPart> parts;
    private WeakReference<Notifier> notifier;

    /* msg.arg1 : id
     * msg.arg2 : size
     * msg.obj  : exception
     */
    public void handleMessage(Message msg) {
        Notifier notify = notifier.get();
        switch (msg.what) {
            case MSG_PROGRESS_INC:
                downloaded += msg.arg2;
                if (notify != null) {
                    notify.onIncreaseDownloaded(id, msg.arg2);
                }
                break;
            case MSG_THREAD_FAILURE:
                break;
            case MSG_THREAD_COMPLETE:
                if (notify != null) {
                    notify.onTaskCompleted(id);
                }
                break;
        }
    }

    public DownloadTask(int id, String name, String url, String path, 
                        int size, int downloaded, int state, String time){
        this.id = id;
        this.name = name;
        this.url = url;
        this.path = path;
        this.size = size;
        this.downloaded = downloaded;
        this.state = TaskState.valueOf(state);
        this.time = time;
        this.parts = new ArrayList<TaskPart>();
    }

    public int getId() {
        return this.id;
    }

    public int getSize() {
        return this.size;
    }

    public int getDownloaded() {
        return this.downloaded;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getPath() {
        return this.path;
    }

    public TaskState getState() {
        return this.state;
    }

    protected Iterator<TaskPart> iteratorTaskPart() {
        return parts.iterator();
    }

    protected void setNotifier(Notifier noti) {
        notifier = new WeakReference<Notifier>(noti);
    }

    protected void addThread(TaskPart part) {
        parts.add(part);
    }

    protected void stop() {
        Iterator<TaskPart> it = parts.iterator();
        state = TaskState.PAUSED;
        while (it.hasNext()) {
            it.next().stop();
        }
    }

    protected void start() {
        Iterator<TaskPart> it = parts.iterator();
        while (it.hasNext()) {
            it.next().start();
        }
    }

    protected void cancel() {
        stop();
        state = TaskState.CANCELED;
    }

    
}
