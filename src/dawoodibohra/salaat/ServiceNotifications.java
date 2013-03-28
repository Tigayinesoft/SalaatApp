package dawoodibohra.salaat;

import android.app.Service;
import android.os.PowerManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;

public class ServiceNotifications extends Service {

	private NotificationManager mNM;
	private PowerManager pm;
	private Context context;
	public static final int NOTIFIER_NAMAAZ = 1;
    public static final int NOTIFIER_MIQAAT = 2;
    private Time theTime = new Time();
    private SharedPreferences prefs;
    
    private PendingIntent alarmIntent;
    private AlarmManager alarms;
    
    private UtilNamaazTimesCalculator namaazTimesCalculator = new UtilNamaazTimesCalculator();
    double nextNamaaz[];

    public class LocalBinder extends Binder {
        ServiceNotifications getService() {
            return ServiceNotifications.this;
        }
    }

    @Override
    public void onCreate() {
    	mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        context = getApplicationContext();
        LocationManager myManager;
		myManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ActivityLocationSettings.TimeZoneUpdater(prefs, context);
		ActivityLocationSettings.LocationUpdater(myManager, prefs, context);
 		namaazTimesCalculator.setLocation((double)prefs.getFloat(ActivityLocationSettings.SETTING_LAT, 0), (double)prefs.getFloat(ActivityLocationSettings.SETTING_LNG, 0));
    	namaazTimesCalculator.setTimezone((double)prefs.getFloat(ActivityLocationSettings.SETTING_TZ, 0));
    	theTime.setToNow();
    	namaazTimesCalculator.setTime(theTime);
    	initializeAlarm();
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
    	if (!prefs.getBoolean(ActivitySettings.SETTING_NAMAAZ_NOTIFY, false) && !prefs.getBoolean(ActivitySettings.SETTING_MIQAAT_NOTIFY, false))
    		stopSelf();
        doOnStart();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	if (!prefs.getBoolean(ActivitySettings.SETTING_NAMAAZ_NOTIFY, false) && !prefs.getBoolean(ActivitySettings.SETTING_MIQAAT_NOTIFY, false))
    		stopSelf();
        doOnStart();
    	return Service.START_NOT_STICKY; // not supported in SDK
    }
    
    private void doOnStart() {
    	theTime.setToNow();
    	namaazTimesCalculator.setTime(theTime);
		namaazTimesCalculator.setLocation((double)prefs.getFloat(ActivityLocationSettings.SETTING_LAT, 0), (double)prefs.getFloat(ActivityLocationSettings.SETTING_LNG, 0));
    	namaazTimesCalculator.setTimezone((double)prefs.getFloat(ActivityLocationSettings.SETTING_TZ, 0));
		nextNamaaz = namaazTimesCalculator.getState();
        setAlarm();
    	if (prefs.getBoolean(ActivitySettings.SETTING_NAMAAZ_NOTIFY, false))
    		processCurrentNamaaz();
    	if (prefs.getBoolean(ActivitySettings.SETTING_MIQAAT_NOTIFY, false))
    		processTodaysMiqaat(); // really tomorrow's in a sense
    	stopSelf();
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    private void initializeAlarm() {
    	alarms = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    	String ALARM_ACTION = BroadcastReceiverNotifications.ACTION_NAMAAZ_NOTIFY_ALARM;
    	Intent intentToFire = new Intent(ALARM_ACTION);
    	//intentToFire.addCategory(Intent.CATEGORY_HOME); // don't seem to need it, not sure why
    	alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
    }
    
    private void setAlarm() {
    	long Offset = Math.round(nextNamaaz[2] * 1000. * 60. * 60.) + 5000L; // just adding a safety 5s
       	int alarmType = AlarmManager.RTC_WAKEUP;
    	alarms.cancel(alarmIntent);
    	alarms.set(alarmType, System.currentTimeMillis()+Offset, alarmIntent);
    }
    
    private void processCurrentNamaaz() {
    	String notificationTitle = "";
    	String notificationText = "";
    	int namaazNotifyVibrate = prefs.getInt(ActivitySettings.SETTING_NAMAAZ_NOTIFY_VIBRATE, 0);
    	boolean azaan = prefs.getBoolean(ActivitySettings.SETTING_NAMAAZ_NOTIFY_SOUND, false);
    	theTime.setToNow();
    	double timeToEnd = nextNamaaz[2] + (double)theTime.hour + (double)theTime.minute/60. + (double)theTime.second/3600.;
    	
    	if (nextNamaaz[0] == 1 && nextNamaaz[1] < 0.0028) {
    		notificationTitle = "Fajr Namaaz";
			notificationText = "Ends at " + ActivityNamaaz.convertTimeToString(timeToEnd, true, false);
			showNamaazNotification(mNM, pm, context, notificationText, notificationTitle, namaazNotifyVibrate, azaan);
    	}
    	else if (nextNamaaz[0] == 3 && nextNamaaz[1] < 0.0028) {
    		notificationTitle = "Zohr/Asr Namaaz";
			notificationText = "Ends at " + ActivityNamaaz.convertTimeToString(timeToEnd, true, false);
			showNamaazNotification(mNM, pm, context, notificationText, notificationTitle, namaazNotifyVibrate, azaan);
    	}
    	else if (nextNamaaz[0] == 6 && nextNamaaz[1] < 0.0028) {
    		notificationTitle = "Magrib/Isha Namaaz";
			notificationText = "Ends at " + ActivityNamaaz.convertTimeToString(timeToEnd, true, false);
    		showNamaazNotification(mNM, pm, context, notificationText, notificationTitle, namaazNotifyVibrate, azaan);
    	}
    }
    
    static public void showNamaazNotification(NotificationManager mNM, PowerManager pm, Context context, String notificationText, String notificationTitle, int namaazNotifyVibrate, boolean azaan) {
    	
    	Notification notification = new Notification();
    	notification.icon = R.drawable.icon_small;
    	notification.when = System.currentTimeMillis();
    	
        Intent intent = new Intent(context, ActivityMainTabHolder.class);
        int goTo = 0;
        intent.putExtra("dawoodibohra.salaat.desc", goTo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);
        notification.setLatestEventInfo(context, notificationTitle, notificationText, contentIntent);
        
        if (namaazNotifyVibrate == 1) {
        	long[] longVibrate = new long[] { 1000, 1000, 1000 };
        	notification.vibrate = longVibrate;
        }
        else if (namaazNotifyVibrate == 2) {
        	long[] longVibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
        	notification.vibrate = longVibrate;
        }
        else if (namaazNotifyVibrate == 3) {
        	long[] longVibrate = new long[] { 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 };
        	notification.vibrate = longVibrate;
        }
        
    	if (azaan) {
    		notification.sound = Uri.parse("android.resource://dawoodibohra.salaat/" + R.raw.azaan);
    		notification.audioStreamType = AudioManager.STREAM_NOTIFICATION;
    	}
    	
		notification.ledARGB = Color.GREEN;
    	notification.ledOffMS = 0;
    	notification.ledOnMS = 1;
    	
    	notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS ;
    	notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL ;
    	
    	mNM.cancel(NOTIFIER_NAMAAZ);
        mNM.notify(NOTIFIER_NAMAAZ, notification);
        
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
        wl.acquire(5000);
    }
    
    private void processTodaysMiqaat() {
    	String notificationTitle = "";
    	String notificationText = "";
    	
    	Time time = new Time();
    	time.setToNow(); 
    	time.set(time.toMillis(false)+86400000L); // add 24 hours to make it tomorrow
    	
    	int[] date = UtilCalendar.getMisriDate(time);
    	String miqaat = UtilCalendar.getMiqaat(date);
    	
    	if (nextNamaaz[0] == 6 && nextNamaaz[1] < 0.0028 && miqaat != "") {
    		notificationTitle = "Miqaats for " + Integer.toString(date[0]) + " " + UtilCalendar.getMisriMonth(date[1]) + " " + Integer.toString(date[2]) + "H" ;
			notificationText = miqaat;
    		showMiqaatNotification(mNM, context, notificationText, notificationTitle);
    	}
    }
    
    static public void showMiqaatNotification(NotificationManager mNM, Context context, String notificationText, String notificationTitle) {
    	Notification notification = new Notification();
    	notification.icon = R.drawable.icon_small;
    	notification.when = System.currentTimeMillis();
        Intent intent = new Intent(context, ActivityMainTabHolder.class);
        int goTo = 2;
        intent.putExtra("dawoodibohra.salaat.desc", goTo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);
        notification.setLatestEventInfo(context, notificationTitle, notificationText, contentIntent);
    	
		notification.ledARGB = Color.RED;
    	notification.ledOffMS = 0;
    	notification.ledOnMS = 1;
    	
    	notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS ;
    	notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL ;
    	
    	mNM.cancel(NOTIFIER_MIQAAT);
        mNM.notify(NOTIFIER_MIQAAT, notification);
    }
}