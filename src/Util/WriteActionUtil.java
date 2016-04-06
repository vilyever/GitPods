package Util;

import com.intellij.openapi.application.ApplicationManager;

/**
 * Created by vilyever on 2016/4/5.
 */
public class WriteActionUtil {
    public static void runWriteAction(Runnable runnable) {
        Object lock = new Object();
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                });
            }
        });
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
