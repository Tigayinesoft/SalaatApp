package dawoodibohra.salaat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiverNotifications extends BroadcastReceiver {
	
	public static final String ACTION_NAMAAZ_NOTIFY_ALARM = "dawoodibohra.salaat.ACTION_NAMAAZ_NOTIFY_ALARM";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startIntent = new Intent(context, ServiceNotifications.class);
		context.startService(startIntent);
	}
	
}