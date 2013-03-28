package dawoodibohra.salaat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.GpsSatellite;

import android.widget.RelativeLayout;

import java.awt.*;

public class ActivityNamaaz extends Activity implements LocationListener {
	
	private TextView text;
	protected Time time = new Time();
	protected Time timeNow = new Time();
	
    private double latitude; 
	private double longitude;
	private double tz;
	private boolean ampm;
	private boolean livegps = false;
	
	SharedPreferences prefs;
	Context context;
	
	private LocationManager myManager;
	TextView LiveGPSSatTV;
	TextView LiveGPSLocTV;
	ViewCompass compassView;

	Button livegpsButton;
	
	@Override
    public void onStart() {
    	time.setToNow();
    	super.onStart();
    }

    @Override
    public void onResume() {
    	timeNow.setToNow();
    	NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	mNM.cancel(ServiceNotifications.NOTIFIER_NAMAAZ);
		main();
    	super.onResume();
    }

    private View.OnClickListener LiveGPS = new View.OnClickListener() {
    	public void onClick(View v) {
    		if (livegps == false) {
    			livegps = true;
    			livegpsButton.setText("Live GPS Tracking: ON");
    			startListening();
    		}
    		else {
    			livegps = false;
    			livegpsButton.setText("Live GPS Tracking: OFF");
    			stopListening();
    		}
    	}
    };

    /**********************************************************************
     * helpers for starting/stopping monitoring of GPS changes below 
     **********************************************************************/
    private void startListening() {
        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        myManager.addGpsStatusListener(listener);
    	//String toasty = "Live GPS Tracking Enabled" ;
    	//Toast toast = Toast.makeText(context, toasty, Toast.LENGTH_LONG);
   		//toast.show();
   		LiveGPSLocTV.setText("Location: Obtaining GPS Fix");
   		LiveGPSSatTV.setText("Satellites: Obtaining GPS Fix");
   		compassView.setBearing(0);
		compassView.invalidate();
        LiveGPSLocTV.setVisibility(View.VISIBLE);
   		LiveGPSSatTV.setVisibility(View.VISIBLE);
    }

    private void stopListening() {
        if (myManager != null) {
            myManager.removeUpdates(this);
        	myManager.removeGpsStatusListener(listener);
    	}
    	//String toasty = "Live GPS Tracking Disabled" ;
    	//Toast toast = Toast.makeText(context, toasty, Toast.LENGTH_LONG);
   		//toast.show();
   		LiveGPSLocTV.setText("Location: Live GPS Tracking Disabled");
   		LiveGPSSatTV.setText("Satellites: Live GPS Tracking Disabled");
   		compassView.setBearing(0);
   		compassView.qibla=0;
		compassView.invalidate();
   		LiveGPSLocTV.setVisibility(View.GONE);
   		LiveGPSSatTV.setVisibility(View.GONE);
   		compassView.setVisibility(View.GONE);
    }
    
    private GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                GpsStatus status = myManager.getGpsStatus(null);
                Iterable<GpsSatellite> sats = status.getSatellites();
                int i = 0;
                String stringy = "" ;
                for(GpsSatellite sat : sats){
                	i++;
                	if (i != 1)
                		stringy = stringy +"\n";
                	stringy = stringy + "Satellite " + i + ": PRN = " + sat.getPrn() + ", SNR = " + sat.getSnr();
                	if (sat.usedInFix())
                		stringy = stringy + " - ACTIVE";
                	}
                LiveGPSSatTV.setText(stringy);
            }
        }
    };
    
    /**********************************************************************
     * LocationListener overrides below 
     **********************************************************************/
    @Override
    public void onLocationChanged(Location location) {
    	Editor editor = prefs.edit();
    	editor.putFloat(ActivityLocationSettings.SETTING_LAT, (float)location.getLatitude());
    	editor.putFloat(ActivityLocationSettings.SETTING_LNG, (float)location.getLongitude());
    	editor.putInt(ActivityLocationSettings.SETTING_QIBLA, ActivityLocationSettings.determineQibla(location.getLatitude(),location.getLongitude()));
    	editor.putInt(ActivityLocationSettings.SETTING_MAGDEC, ActivityLocationSettings.determineMagDec(location.getLatitude(),location.getLongitude()));
    	editor.putBoolean(ActivityLocationSettings.SETTING_AUTOLOC, false);
    	editor.putInt(ActivityLocationSettings.SETTING_LOC_METHOD, 1);
    	editor.putString(ActivityLocationSettings.SETTING_CITY, "Location: Unknown");
    	editor.commit();
    	
    	String stringy = "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude() + "\nAltitude: " + location.getAltitude() + "\nSpeed: " + location.getSpeed() + "\nGPS Bearing: " + location.getBearing() + "\nQibla Bearing: " + ActivityLocationSettings.determineQibla(location.getLatitude(),location.getLongitude());
    	LiveGPSLocTV.setText(stringy);
   		compassView.setVisibility(View.VISIBLE);
    	compassView.setBearing(location.getBearing());
    	compassView.qibla=ActivityLocationSettings.determineQibla(location.getLatitude(),location.getLongitude());
		compassView.invalidate();
    	timeNow.setToNow();
    	time.setToNow();
    	processHomeScreen();
    }    

    @Override
    public void onProviderDisabled(String provider) {}    

    @Override
    public void onProviderEnabled(String provider) {}    

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    
    // ==========================================================================
    
    private View.OnClickListener NextDay = new View.OnClickListener() {
    	public void onClick(View v) {
    		time.set(time.toMillis(false)+86400000L);
    		main();
    	}
    };
    
    private View.OnClickListener Next10Day = new View.OnClickListener() {
    	public void onClick(View v) {
    		time.set(time.toMillis(false)+864000000L);
    		processHomeScreen();
    	}
    };
    
    private View.OnClickListener PrevDay = new View.OnClickListener() {
    	public void onClick(View v) {
    		time.set(time.toMillis(false)-86400000L);
    		processHomeScreen();
    	}
    };

    private View.OnClickListener Prev10Day = new View.OnClickListener() {
    	public void onClick(View v) {
    		time.set(time.toMillis(false)-864000000L);
    		processHomeScreen();
    	}
    };

    private View.OnClickListener Today = new View.OnClickListener() {
    	public void onClick(View v) {
    		time.setToNow();
    		processHomeScreen();
    	}
    };
    
    public void main() {
    	setContentView(R.layout.namaaz);
    	
    	context = getApplicationContext();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		myManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		livegpsButton = (Button)findViewById(R.id.LiveGPS);
        livegpsButton.setOnClickListener(LiveGPS);
        if (livegps)
        	livegpsButton.setText("Live GPS Tracking: ON");
        else
        	livegpsButton.setText("Live GPS Tracking: OFF");
        
        RelativeLayout livegpslayout = (RelativeLayout)findViewById(R.id.RelativeLayout01);
        
        if (prefs.getBoolean(ActivitySettings.SETTING_LIVEGPS, false)) {
        	livegpslayout.setVisibility(View.VISIBLE);
        	livegpsButton.setVisibility(View.VISIBLE);
    	}
        else {
        	livegpslayout.setVisibility(View.GONE);
        	livegpsButton.setVisibility(View.GONE);
        }
        
        LiveGPSLocTV = (TextView) findViewById(R.id.LiveGPSLocTV);
        LiveGPSSatTV = (TextView) findViewById(R.id.LiveGPSSatTV);
        compassView = (ViewCompass)findViewById(R.id.CompassID);
        
        if (livegps == false)
        	stopListening();
        
    	processHomeScreen();
    }
        
    public void processHomeScreen() {
    	int[] mdate =  UtilCalendar.getMisriDate(time);  

		latitude = (double)prefs.getFloat(ActivityLocationSettings.SETTING_LAT, 0); 
		longitude = (double)prefs.getFloat(ActivityLocationSettings.SETTING_LNG, 0);
    	tz = (double)prefs.getFloat(ActivityLocationSettings.SETTING_TZ, 0);
    	ampm = (boolean)prefs.getBoolean(ActivitySettings.SETTING_AMPM, true);
    	int fontSize = (int)prefs.getInt(ActivitySettings.SETTING_ACTUAL_FONT_SIZE, 16);
    	
        Button nextbutton = (Button)findViewById(R.id.NextButton);
        nextbutton.setOnClickListener(NextDay);
        nextbutton.setTextSize(fontSize);

        Button next10button = (Button)findViewById(R.id.Next10Button);
        next10button.setOnClickListener(Next10Day);
        next10button.setTextSize(fontSize);
        
        Button prevbutton = (Button)findViewById(R.id.PrevButton);
        prevbutton.setOnClickListener(PrevDay);
        prevbutton.setTextSize(fontSize);
        
        Button prev10button = (Button)findViewById(R.id.Prev10Button);
        prev10button.setOnClickListener(Prev10Day);
        prev10button.setTextSize(fontSize);
        
        Button todaybutton = (Button)findViewById(R.id.TodayButton);
        todaybutton.setOnClickListener(Today);    	
        todaybutton.setTextSize(fontSize);
                
    	String temp;
    	
    	UtilNamaazTimesCalculator namaazTimesCalculatorForNow = new UtilNamaazTimesCalculator();
    	namaazTimesCalculatorForNow.setLocation(latitude, longitude);
    	namaazTimesCalculatorForNow.setTime(timeNow);
    	namaazTimesCalculatorForNow.setTimezone(tz);
    	text = (TextView) findViewById(R.id.NextNamaaz);
        text.setText(nextNamaazString(namaazTimesCalculatorForNow.getState()));
        text.setTextSize(fontSize);
    	
    	UtilNamaazTimesCalculator namaazTimesCalculator = new UtilNamaazTimesCalculator();
    	namaazTimesCalculator.setLocation(latitude, longitude);
    	namaazTimesCalculator.setTime(time);
    	namaazTimesCalculator.setTimezone(tz);
    	double[] times = namaazTimesCalculator.getNamaazTimes();
    	
    	text = (TextView) findViewById(R.id.Label0);
    	text.setTextSize(fontSize);
    	text = (TextView) findViewById(R.id.Label1);
    	text.setTextSize(fontSize);
    	text = (TextView) findViewById(R.id.Label2);
    	text.setTextSize(fontSize);
    	text = (TextView) findViewById(R.id.Label3);
    	text.setTextSize(fontSize);
    	text = (TextView) findViewById(R.id.Label4);
    	text.setTextSize(fontSize);
    	text = (TextView) findViewById(R.id.Label5);
    	text.setTextSize(fontSize);
    	text = (TextView) findViewById(R.id.Label6);
    	text.setTextSize(fontSize);
    	text = (TextView) findViewById(R.id.Label7);
    	text.setTextSize(fontSize);
    	text = (TextView) findViewById(R.id.Label8);
    	text.setTextSize(fontSize);
    	
    	temp = UtilCalendar.getGregDay(time.weekDay) + ", " + Integer.toString(time.monthDay) + " " + UtilCalendar.getGregMonth(time.month) + " " + Integer.toString(time.year) ;
    	text = (TextView) findViewById(R.id.GregDayInfo);
    	text.setText(temp);
    	text.setTextSize(fontSize);
    	
    	//temp = UtilCalendar.getMisriDay(time.weekDay) + ", " + Integer.toString(mdate[0]) + " " + UtilCalendar.getMisriMonth(mdate[1]) + " " + Integer.toString(mdate[2]) + "H" ;
    	temp = Integer.toString(mdate[0]) + " " + UtilCalendar.getMisriMonth(mdate[1]) + " " + Integer.toString(mdate[2]) + "H" ;
    	text = (TextView) findViewById(R.id.MisriDayInfo);
    	text.setText(temp);
    	text.setTextSize(fontSize);
    	
    	text = (TextView) findViewById(R.id.TimeField1);
        text.setText(convertTimeToString(times[0],ampm,false));
        text.setTextSize(fontSize);
        text = (TextView) findViewById(R.id.TimeField2);
        text.setText(convertTimeToString(times[1],ampm,false));
        text.setTextSize(fontSize);
        text = (TextView) findViewById(R.id.TimeField3);
        text.setText(convertTimeToString(times[2],ampm,false));
        text.setTextSize(fontSize);
        text = (TextView) findViewById(R.id.TimeField4);
        text.setText(convertTimeToString(times[3],ampm,false));
        text.setTextSize(fontSize);
        text = (TextView) findViewById(R.id.TimeField5);
        text.setText(convertTimeToString(times[4],ampm,false));
        text.setTextSize(fontSize);
        text = (TextView) findViewById(R.id.TimeField6);
        text.setText(convertTimeToString(times[5],ampm,false));
        text.setTextSize(fontSize);
        text = (TextView) findViewById(R.id.TimeField7);
        text.setText(convertTimeToString(times[6],ampm,false));
        text.setTextSize(fontSize);
        text = (TextView) findViewById(R.id.TimeField8);
        text.setText(convertTimeToString(times[7],ampm,false));
        text.setTextSize(fontSize);
        text = (TextView) findViewById(R.id.TimeField9);
        text.setText(convertTimeToString(times[8],ampm,false));
        text.setTextSize(fontSize);
    }
    
    public static String nextNamaazString(double[] nextNamaaz) {
    	String temp = "" ;
    	if (nextNamaaz[0] == 0)
    		temp = "Fajr starts in: " + convertTimeToString(nextNamaaz[2],false,false);
    	else if (nextNamaaz[0] == 1)
    		temp = "Fajr ends in: " + convertTimeToString(nextNamaaz[2],false,false);
    	else if (nextNamaaz[0] == 2)
    		temp = "Zohr/Asr starts in: " + convertTimeToString(nextNamaaz[2],false,false);
    	else if (nextNamaaz[0] == 3)
    		temp = "Zohr ends in: " + convertTimeToString(nextNamaaz[2],false,false);
    	else if (nextNamaaz[0] == 4)
    		temp = "Asr ends in: " + convertTimeToString(nextNamaaz[2],false,false) + " - Magrib starts in: " + convertTimeToString(nextNamaaz[3],false,false);
    	else if (nextNamaaz[0] == 5)
    		temp = temp + "Magrib/Isha starts in: " + convertTimeToString(nextNamaaz[2],false,false);
    	else if (nextNamaaz[0] == 6)
    		temp = temp + "Magrib ends in: " + convertTimeToString(nextNamaaz[2],false,false);
    	else if (nextNamaaz[0] == 7)
    		temp = temp + "Isha ends in: " + convertTimeToString(nextNamaaz[2],false,false);
    	return temp;
    }
    
    public static double truncateTo2(double x) {
		if (x > 0)
		    return Math.floor(x * 100)/100;
		  else
		    return Math.ceil(x * 100)/100;
    }
    
    public static String convertTimeToString(double time, boolean ampm, boolean showSeconds){
    	double hour = Math.floor(time) ;
    	double minutes = Math.floor((time % 1)*60);
    	double seconds = Math.floor(((time % 1)*60 % 1) * 60);
    	
    	if (seconds > 30 && !showSeconds) 
    	{

    		minutes = minutes + 1 ;
    	
	    	if (minutes == 60) {
	    		minutes = 0;
	    		hour = hour + 1;
	    	}
	    	
	    	if (hour == 24) {
	    		hour = 0;
	    	}
    	}
    	
    	
    	String shour = "" ;
    	String sminutes = "" ;
    	String sseconds = "" ;
    	
    	String TheTime = "" ;
    	
    	if (ampm) {
        	shour = Integer.toString((int)hour % 12) ;
        	sminutes = Integer.toString((int) minutes) ;
        	sseconds = Integer.toString((int) seconds) ;
    		if (hour == 0 || hour == 12)
    			shour = "12";
        	if (minutes < 10)
        		sminutes = "0" + sminutes;
    		if (hour < 12)
    			TheTime = shour + ":" + sminutes + " AM" ;
    		else
    			TheTime = shour + ":" + sminutes + " PM" ;
    	}
    	else {
        	shour = Integer.toString((int) hour) ;
        	sminutes = Integer.toString((int) minutes) ;
        	sseconds = Integer.toString((int) seconds) ;

        	if (hour < 10)
        		shour = "0" + shour;
        	if (minutes < 10)
        		sminutes = "0" + sminutes;
        	if (seconds < 10)
        		sseconds = "0" + sseconds;
        	
        	if (showSeconds)
        		TheTime = shour + ":" + sminutes +  ":" + sseconds; 
        	else
        		TheTime = shour + ":" + sminutes; 
    	}
    	return TheTime ;
    }
    
}