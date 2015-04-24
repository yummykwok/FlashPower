package net.heybird.downloader;

import net.heybird.downloader.DownloadTask.TaskState;

public class TaskPart {

    private int id;
    private int offset;
    private int size;
    private int downloaded;
    private Thread thread;
    private DownloadTask task;
    private TaskState state;
    public TaskPart(DownloadTask t, int id) {
        state = TaskState.READY;
        task = t;
    }

    protected synchronized void stop() {
        if (thread!=null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        thread = null;
    }

    protected synchronized void start() {
        if (state == TaskState.STARTED || state == TaskState.COMPLETED) {
            return;
        }

        state = TaskState.STARTED;
        thread = new Thread() {
            public void run(){
                //handler.obtainMessage(MSG_PROGRESS_INC, id, size).sendToTarget();
                
            }
        };
        thread.start();
    }

    protected synchronized void reset() {
        stop();
        this.downloaded = 0;
        this.state = TaskState.READY;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getDownloaded() {
        return this.downloaded;
    }
    
    public TaskState getState() {
        return state;
    }
}
