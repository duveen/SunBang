package kr.o3selab.sunbang.Instance;

import android.app.ProgressDialog;
import android.util.Log;

import java.util.Collections;
import java.util.LinkedList;

public class ThreadGroupHandler extends Thread {

    private Thread[] threadGroup;
    private ProgressDialog progressDialog;

    public ThreadGroupHandler(Thread[] threadGroup, ProgressDialog progressDialog) {
        this.threadGroup = threadGroup;
        this.progressDialog = progressDialog;
    }

    @Override
    public void run() {
        try {
            LinkedList<Thread> threadLinkedList = new LinkedList<>();

            Collections.addAll(threadLinkedList, threadGroup);

            Log.d("check", "다이얼로그 실행");
            DB.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });

            while(true) {

                for(int i = 0 ; i < threadLinkedList.size(); i++) {

                    Thread th = threadLinkedList.get(i);

                    if(th.getState().equals(State.TERMINATED)){
                        threadLinkedList.remove(th);
                    }
                }

                if(threadLinkedList.size() == 0) break;
                sleep(100);
            }

            DB.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });

        } catch (Exception e) {
            DB.sendToast("에러: " + e.getMessage(), 2);
        }
    }
}
