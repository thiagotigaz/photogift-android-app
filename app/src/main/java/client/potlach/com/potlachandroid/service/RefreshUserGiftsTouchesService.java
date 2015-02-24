package client.potlach.com.potlachandroid.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import client.potlach.com.potlachandroid.PhotoGiftApplication;
import client.potlach.com.potlachandroid.R;
import client.potlach.com.potlachandroid.activity.GiftListActivity;
import client.potlach.com.potlachandroid.model.Gift;
import client.potlach.com.potlachandroid.singleton.DataHolder;
import client.potlach.com.potlachandroid.util.GiftTouchesTrackerOpenHelper;

/**
 * Created by thiago on 11/20/14.
 */
/*  This service fetches the gifts posted by the logged in user in the last three days
    and keep refreshing it based on the refresh interval, so if there are new touches
    it creates a notification.  */
public class RefreshUserGiftsTouchesService extends Service {

    private PhotoGiftApplication app;
    private GiftSvcApi giftSvc;
    private int notificationCount = 0;
    private static final String TAG = "RefreshService";
    private static boolean running = true;
    private ScheduledThreadPoolExecutor pool;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("RefreshService","Refresh service started!");
        init();

        pool = new ScheduledThreadPoolExecutor(1);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(RefreshUserGiftsTouchesService.this);

        long startDelay = 0;
        long refreshInterval = Long.parseLong(defaultSharedPreferences.getString(getString(R.string.pref_key_sync_frequency), "30"));
        Log.d("RefreshService","Refresh Interval: "+ refreshInterval+" Minutes, get from SharedPreferences");
//        long refreshInterval = 20;

        TimeUnit unit = TimeUnit.MINUTES;
//        TimeUnit unit = TimeUnit.SECONDS;
        pool.scheduleAtFixedRate(new RefreshGiftsTask(),
                startDelay, refreshInterval, unit);
//        Thread t = new Thread(new RefreshGiftsTask());
//        t.start();
        return START_STICKY;
    }

    private void init() {
        app = (PhotoGiftApplication) getApplication();
        giftSvc = GiftSvc.getOrShowLogin(null);
        running = true;
    }

    private void createNotification(String msg, Gift gift){
        Log.d("RefreshService","Notification being created!");
        DataHolder.getInstance().setSelectedGift(gift);
        notificationCount++;
        int icone = R.drawable.ic_launcher;
        String aviso = msg;
        long data = System.currentTimeMillis();
        String titulo = "You have new touches!";
        Context context = getApplicationContext();
        Intent intent = new Intent(context, GiftListActivity.class);
        intent.putExtra(GiftListActivity.SEARCH_GIFT, gift.getId());
        intent.putExtra(GiftListActivity.SEARCH_ACTION, GiftListActivity.SearchGiftActionEnum.SEARCH_BY_GIFT);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, notificationCount, intent,
                        PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification(icone, aviso, data);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.setLatestEventInfo(context, titulo,
                msg, pendingIntent);
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(ns);
        notificationManager.notify(notificationCount, notification);
    }

    private class RefreshGiftsTask implements Runnable{

        @Override
        public void run() {
            if(!running){
                try {
                    //Cancel scheduled but not started task, and avoid new ones
                    pool.shutdown();
                    //Wait for the running tasks
                    pool.awaitTermination(5, TimeUnit.SECONDS);
                    //Interrupt the threads and shutdown the scheduler
                    pool.shutdownNow();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                onDestroy();
            }
            else{
                Log.d("RefreshService", "Gifts are being refreshed");
            /*  Fetch latest gifts, add it to sqlite if it does not exist.
                if gifts already in sqlite compare touches count to see if
                it's necessary to create notification.  */
                GiftTouchesTrackerOpenHelper gtOpenHelper = new GiftTouchesTrackerOpenHelper(RefreshUserGiftsTouchesService.this);
                List<Gift> latestGifts = giftSvc.findLatestByOwner();
                Log.d("RefreshService","SIZE: "+latestGifts.size());
                for(Gift g:latestGifts){
                    //Check if gift is already in SQLite, if so compare touches and create
                    //the notification based on the result, otherwise insert it into SQLite.
                    Log.d("RefreshService","Gift title: "+ g.getTitle());

                    Long numberOfTouches = null;
                    numberOfTouches = gtOpenHelper.getGiftTouches(g.getId());
                    //gift already exists in sqlite, compare number of touches.
                    if(numberOfTouches!=null){
                        Log.d("RefreshService","Gift title: "+ g.getTitle()+ "Already exist in SQLite");
//                        createNotification("teste", g);
                        if(numberOfTouches!=g.getLikeTouches()){
                            createNotification("The \""+ g.getTitle() +"\" gift has "+ (g.getLikeTouches() - numberOfTouches) +" new touches!", g);
                            gtOpenHelper.updateGiftTouches(g);
                            Log.d("RefreshService", "Gift title: " + g.getTitle() + "have to create notification");
                        }
                    }
                    //gift does not exist yet. Add it to sqlite to future comparisons
                    else{
                        Log.d("RefreshService","Gift title: "+ g.getTitle()+ "does not exist in SQLite");

                        gtOpenHelper.addGift(g);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //clear all gifts where giftOwnerId == current Logged In User Id.
        stopSelf();
        super.onDestroy();

    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        RefreshUserGiftsTouchesService.running = running;
    }
}
