package dawoodibohra.salaat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.app.TabActivity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable; 

 /* import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;*/ 

public class ActivityMainTabHolder extends TabActivity {
	
	private TabHost tabHost;

	/* Handles item selections */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, Menu.NONE, "Settings")
            .setIcon(R.drawable.ic_menu_settings);
        menu.add(0, 1, Menu.NONE, "Location")
    		.setIcon(R.drawable.ic_menu_mylocation);
        menu.add(0, 2, Menu.NONE, "Quit")
        	.setIcon(R.drawable.ic_menu_close);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i;
        switch (item.getItemId()) {
        case 0:
        	i = new Intent (this, ActivitySettings.class);
        	startActivity(i);
            return true;
        case 1:
        	i = new Intent (this, ActivityLocationSettings.class);
        	startActivity(i);
            return true;
        case 2:
           finish();
            return true;
        }
        return false;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		LocationManager myManager;
		myManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		if (!prefs.contains(ActivityLocationSettings.SETTING_LAT) || !prefs.contains(ActivityLocationSettings.SETTING_LNG) || !prefs.contains(ActivityLocationSettings.SETTING_TZ) || !prefs.contains(ActivityLocationSettings.SETTING_CITY) || !prefs.contains(ActivityLocationSettings.SETTING_QIBLA) || !prefs.contains(ActivityLocationSettings.SETTING_MAGDEC))
        	if(!ActivityLocationSettings.getDefaultSettings(myManager,prefs,context)) {
            	Intent i = new Intent (this, ActivityLocationSettings.class);
            	startActivity(i);
        	}
		else {
			ActivityLocationSettings.TimeZoneUpdater(prefs, context);
			ActivityLocationSettings.LocationUpdater(myManager, prefs, context);
		}
			
		if (!prefs.contains(ActivitySettings.SETTING_AMPM)) {
	    	Editor editor = prefs.edit();
	    	editor.putBoolean(ActivitySettings.SETTING_AMPM, true); // 12-hr clock by default
	    	editor.commit();
    	}
		int fontSize = (int)prefs.getInt(ActivitySettings.SETTING_ACTUAL_FONT_SIZE, 16);
        setContentView(R.layout.main);
        
        Resources res = getResources();
        Drawable drawable;
        tabHost = getTabHost();  // The activity TabHost
        
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, ActivityNamaaz.class);
        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("namaaztimes");
        //spec.setIndicator(new MyTabIndicator(this, "Namaaz", fontSize)); // This method of setIndicator using a View is unavailable in API < 4. This causes the minimum API to bump up to 4.
        //spec.setIndicator("Namaaz");
        drawable = res.getDrawable(R.drawable.ic_tab_namaaz);
        spec.setIndicator("Namaaz",drawable);
        spec.setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, ActivityQibla.class);
        spec = tabHost.newTabSpec("qibla");
        //spec.setIndicator(new MyTabIndicator(this, "Qibla", fontSize));
        //spec.setIndicator("Qibla");
        drawable = res.getDrawable(R.drawable.ic_tab_qibla);
        spec.setIndicator("Qibla",drawable);
        spec.setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ActivityCalendar.class);
        spec = tabHost.newTabSpec("Calendar");
        //spec.setIndicator(new MyTabIndicator(this, "Calendar", fontSize));
        //spec.setIndicator("Calendar");
        drawable = res.getDrawable(R.drawable.ic_tab_calendar);
        spec.setIndicator("Calendar",drawable);
        spec.setContent(intent);
        tabHost.addTab(spec);
        
        //final float scale = context.getResources().getDisplayMetrics().density;
        //int height = (int) (64f*scale + 0.5f);
        //tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = height;
        //tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = height;
        //tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = height;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	setIntent(intent);
    	super.onNewIntent(intent);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    @Override
    protected void onResume() {
    	Context context = getApplicationContext();
        Intent startIntent = new Intent(context, ServiceNotifications.class);
    	context.startService(startIntent);
   		tabHost.setCurrentTab(getIntent().getIntExtra("dawoodibohra.salaat.desc",0));
   		super.onResume();
    }    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}