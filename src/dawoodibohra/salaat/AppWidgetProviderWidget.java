//package dawoodibohra.salaat;
//import android.content.Intent;
//
//import android.appwidget.AppWidgetManager;
//import android.appwidget.AppWidgetProvider;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import android.text.format.Time;
//import android.widget.RemoteViews;
//import android.app.PendingIntent;
//public class AppWidgetProviderWidget extends AppWidgetProvider {
//@Override
//	public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds) {
//	
//	final int N = appWidgetIds.length;
//	for (int i = 0; i < N; i++) {
//		int appWidgetId = appWidgetIds[i];
//		
//		Intent intent = new Intent(context, ActivityMainTabHolder.class);
//		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//		RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
//		views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
//		views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
//		views.setTextViewText(R.id.widget_text, getText(context));
//		appWidgetManager.updateAppWidget(appWidgetId, views); }
//}
//	
//	private String getText(Context context) {
//		Time time = new Time();
//		time.setToNow();
//		
//    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//		
//		if (!prefs.contains(ActivityLocationSettings.SETTING_LAT) || !prefs.contains(ActivityLocationSettings.SETTING_LNG) || !prefs.contains(ActivityLocationSettings.SETTING_TZ) || !prefs.contains(ActivityLocationSettings.SETTING_CITY)) {
//        	return "Set location.";
//		}
//		else {
//	    	double latitude = (double)prefs.getFloat(ActivityLocationSettings.SETTING_LAT, 0); 
//	    	double longitude = (double)prefs.getFloat(ActivityLocationSettings.SETTING_LNG, 0);
//	    	double tz = (double)prefs.getFloat(ActivityLocationSettings.SETTING_TZ, 0);
//	    	
//	    	UtilNamaazTimesCalculator namaazTimesCalculator = new UtilNamaazTimesCalculator();
//	    	namaazTimesCalculator.setLocation(latitude, longitude);
//	    	namaazTimesCalculator.setTime(time);
//	    	namaazTimesCalculator.setTimezone(tz);
//	    	return ActivityNamaaz.nextNamaazString(namaazTimesCalculator.getState());
//		}
//	}
//}