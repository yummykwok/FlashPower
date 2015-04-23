package net.heybird.downloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadManager {

	private List<DownloadTask> mTasks;
	private static DownloadManager mInstance;

	public static interface UiNotifier {
		
	}

    public static enum TaskState {
        STARTED,
        PAUSED,
        CANCELED,
        DOWNLOADED,
        DELETED;

        public static TaskState valueOf(int ordinal) {
            TaskState res = null;
            switch (ordinal) {
                case 0:
                    res = STARTED;
                    break;
                case 1:
                    res = PAUSED;
                    break;
                case 2:
                    res = CANCELED;
                    break;
                case 3:
                    res = DOWNLOADED;
                    break;
                case 4:
                    res = DELETED;
                    break;
            }
            return res;
        }
    }

    public static class DownloadTask {
        private int id;
        private String name;
        private String url;
        private String path;
        private int size;
        private int downloaded;
        private TaskState state;
        private String time;
        private List<TaskPart> parts;
        
        public DownloadTask(int id, String name, String url, String path, 
                            int size, int dowloaded, int state, String time){
            this.id = id;
            this.name = name;
            this.url = url;
            this.path = path;
            this.size = size;
            this.downloaded = downloaded;
            this.state = TaskState.valueOf(state);
            this.time = time;
            this.parts = Collections.synchronizedList(new ArrayList<TaskPart>());

            if (id < 0) {
            	//add to db
            }
        }
        
        protected void addThread(TaskPart part) {
        	parts.add(part);
        }
    }

    public static class TaskPart {
        private int id;
        private int taskId;
        private int offset;
        private int size;
        private int downloaded;
        private Thread thread;
    }

    public static DownloadManager getInstance() {
        if (null==mInstance){
            synchronized (DownloadManager.class) {
                if (null==mInstance) {
                	mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }
    
    public static synchronized void close() {
    	mInstance = null;
    }

    /**
     * Load tasks from database
     */
    private void loadFromDb() {
    	
    }
    
    public void startNewTask(String name, String url, String path, int size, int parts) {
    	
    }
}
