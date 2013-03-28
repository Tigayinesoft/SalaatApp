package dawoodibohra.salaat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.CheckBox;
import android.widget.Toast;
import android.view.WindowManager;
import android.widget.TextView;

/* Settings Activity */
public class ActivitySettings extends Activity{

	private Spinner FontSizeSpinner;
	private Spinner AMPMSpinner;
	private Spinner CalTypeSpinner;
	private CheckBox CalGregCB;
	private CheckBox CalAllCB;
	private CheckBox CalShowDB;
	private CheckBox NamaazNotifyCB;
	private CheckBox NamaazNotifySoundCB;
	private TextView NamaazNotifyVibrateTV;
	private Spinner NamaazNotifyVibrateSpinner;
	private CheckBox MiqaatNotifyCB;
	private CheckBox LiveGPSCB;
	private String errorMessage;
	
	private Button testNamaazButton;
	private Button testMiqaatButton;	
	private Context context;
	
	public static final String SETTING_FONTSIZE = "SETTING_FONTSIZE";
	public static final String SETTING_ACTUAL_FONT_SIZE = "SETTING_ACTUAL_FONT_SIZE";	
	public static final String SETTING_AMPM = "SETTING_AMPM";
	public static final String SETTING_CALTYPE = "SETTING_CALTYPE";
	public static final String SETTING_SHOWGREG = "SETTING_SHOWGREG";
	public static final String SETTING_SHOWALL = "SETTING_SHOWALL";
	public static final String SETTING_SHOWDB = "SETTING_SHOWDB";
	public static final String SETTING_NAMAAZ_NOTIFY = "SETTING_NAMAAZ_NOTIFY";
	public static final String SETTING_NAMAAZ_NOTIFY_SOUND = "SETTING_NAMAAZ_NOTIFY_SOUND";
	public static final String SETTING_NAMAAZ_NOTIFY_VIBRATE = "SETTING_NAMAAZ_NOTIFY_VIBRATE";
	public static final String SETTING_MIQAAT_NOTIFY = "SETTING_MIQAAT_NOTIFY";
	public static final String SETTING_LIVEGPS = "SETTING_LIVEGPS";	
	
	// dp values
	public static final float FONTSIZE_SMALL = 14f;
	public static final float FONTSIZE_MEDIUM = 16f;
	public static final float FONTSIZE_LARGE = 18f;
	public static final float FONTSIZE_XLARGE = 20f;
	
SharedPreferences prefs;

	private View.OnClickListener TestNamaaz = new View.OnClickListener() {
		public void onClick(View v) {
			Context context = getApplicationContext();
	    	NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			ServiceNotifications.showNamaazNotification(mNM, pm, context, "Ends at 6:00 AM", "Fajr Namaaz", NamaazNotifyVibrateSpinner.getSelectedItemPosition(), NamaazNotifySoundCB.isChecked());
		}
	};   
	
	private View.OnClickListener TestMiqaat = new View.OnClickListener() {
		public void onClick(View v) {
			Context context = getApplicationContext();
	    	NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			ServiceNotifications.showMiqaatNotification(mNM, context, "\n• Ashara Mubaraka: Ninth Waaz (Ashura)", "Miqaats for 10 Muharram al-Haraam 1431H");
		}
	};   

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);
        
        context = getApplicationContext();
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        FontSizeSpinner = (Spinner)findViewById(R.id.SpinnerFontSize);
        AMPMSpinner = (Spinner)findViewById(R.id.SpinnerAMPM);
        CalTypeSpinner = (Spinner)findViewById(R.id.SpinnerCalType);
        CalGregCB = (CheckBox)findViewById(R.id.CheckBoxCalGreg);
        CalAllCB = (CheckBox)findViewById(R.id.CheckBoxCalAll);
        CalShowDB = (CheckBox)findViewById(R.id.CheckBoxCalDB);
        NamaazNotifyCB = (CheckBox)findViewById(R.id.CheckBoxNamaazNotify);
        NamaazNotifySoundCB = (CheckBox)findViewById(R.id.CheckBoxNamaazNotifySound);
        NamaazNotifyVibrateTV = (TextView)findViewById(R.id.TextViewNamaazNotifyVibrate);
        NamaazNotifyVibrateSpinner = (Spinner)findViewById(R.id.SpinnerNamaazNotifyVibrate);
        MiqaatNotifyCB = (CheckBox)findViewById(R.id.CheckBoxMiqaatNotify);
        LiveGPSCB = (CheckBox)findViewById(R.id.CheckBoxLiveGPS);
        
        testNamaazButton = (Button)findViewById(R.id.TestNamaaz);
        testNamaazButton.setOnClickListener(TestNamaaz);
        testMiqaatButton = (Button)findViewById(R.id.TestMiqaat);
        testMiqaatButton.setOnClickListener(TestMiqaat);
        
        populateSpinners() ;
        
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        updateUIFromPreferences();
        
        CalAllCB.setVisibility(View.GONE);
//        CalShowDB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//	         	if (isChecked)
//	         		CalAllCB.setVisibility(View.VISIBLE);
//	         	else
//	         		CalAllCB.setVisibility(View.GONE);
//         	}
//		});
    	           
        NamaazNotifyCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	         	if (isChecked) {
	         		NamaazNotifySoundCB.setVisibility(View.VISIBLE);
	         		NamaazNotifyVibrateTV.setVisibility(View.VISIBLE);
	         		NamaazNotifyVibrateSpinner.setVisibility(View.VISIBLE);
	         		testNamaazButton.setVisibility(View.VISIBLE);
	         	}
	         	else {
	         		NamaazNotifySoundCB.setVisibility(View.GONE);
	         		NamaazNotifyVibrateTV.setVisibility(View.GONE);
	         		NamaazNotifyVibrateSpinner.setVisibility(View.GONE);
	         		testNamaazButton.setVisibility(View.GONE);
	         	}
         	}
		});
        
        MiqaatNotifyCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	         	if (isChecked) {
	         		testMiqaatButton.setVisibility(View.VISIBLE);	         	}
	         	else {
	         		testMiqaatButton.setVisibility(View.GONE);
	         	}
         	}
		});

    }
	
	@Override
    public void onPause() {
		if (savePreferences()) {
			finish();
		}
		else {
			Context context = getApplicationContext();
	    	Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
	    	toast.show();
		}
		super.onPause();
	}
	
    public void populateSpinners() {
    	ArrayAdapter<CharSequence> fAdapter;
    	int spinner_dd_item;
    	
    	fAdapter = ArrayAdapter.createFromResource(this, R.array.fontsize_options,
                android.R.layout.simple_spinner_item);
    	spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
		fAdapter.setDropDownViewResource(spinner_dd_item);
		FontSizeSpinner.setAdapter(fAdapter);

		fAdapter = ArrayAdapter.createFromResource(this, R.array.ampmmethod_options,
                android.R.layout.simple_spinner_item);
    	spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
		fAdapter.setDropDownViewResource(spinner_dd_item);
		AMPMSpinner.setAdapter(fAdapter);
		
		fAdapter = ArrayAdapter.createFromResource(this, R.array.cal_options,
                android.R.layout.simple_spinner_item);
    	spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
		fAdapter.setDropDownViewResource(spinner_dd_item);
		CalTypeSpinner.setAdapter(fAdapter);
		
		fAdapter = ArrayAdapter.createFromResource(this, R.array.vibrate_options,
                android.R.layout.simple_spinner_item);
    	spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
		fAdapter.setDropDownViewResource(spinner_dd_item);
		NamaazNotifyVibrateSpinner.setAdapter(fAdapter);
    	}

    public void updateUIFromPreferences() {
    	if (prefs.getBoolean(SETTING_AMPM, true))
    		AMPMSpinner.setSelection(0);
    	else
    		AMPMSpinner.setSelection(1);
    	
		if (prefs.getInt(SETTING_FONTSIZE, 1) < 4)
			FontSizeSpinner.setSelection(prefs.getInt(SETTING_FONTSIZE, 1));
		else
			FontSizeSpinner.setSelection(1);
		
		CalTypeSpinner.setSelection(prefs.getInt(SETTING_CALTYPE, 1));
    	CalGregCB.setChecked(prefs.getBoolean(SETTING_SHOWGREG, true));
    	CalAllCB.setChecked(prefs.getBoolean(SETTING_SHOWALL, true));
    	CalShowDB.setChecked(prefs.getBoolean(SETTING_SHOWDB, true));
    	NamaazNotifyCB.setChecked(prefs.getBoolean(SETTING_NAMAAZ_NOTIFY, false));
    	NamaazNotifySoundCB.setChecked(prefs.getBoolean(SETTING_NAMAAZ_NOTIFY_SOUND, false));
    	NamaazNotifyVibrateSpinner.setSelection(prefs.getInt(SETTING_NAMAAZ_NOTIFY_VIBRATE, 0));
    	MiqaatNotifyCB.setChecked(prefs.getBoolean(SETTING_MIQAAT_NOTIFY, false));
    	LiveGPSCB.setChecked(prefs.getBoolean(SETTING_LIVEGPS, false));
    	
    	//if (prefs.getBoolean(SETTING_SHOWDB, true))
    	//	CalAllCB.setVisibility(View.VISIBLE);
    	//else
    		CalAllCB.setVisibility(View.GONE);
    	
     	if (prefs.getBoolean(SETTING_NAMAAZ_NOTIFY, false)) {
     		NamaazNotifySoundCB.setVisibility(View.VISIBLE);
     		NamaazNotifyVibrateTV.setVisibility(View.VISIBLE);
 			NamaazNotifyVibrateSpinner.setVisibility(View.VISIBLE);
 			testNamaazButton.setVisibility(View.VISIBLE);
     	}
     	else {
     		NamaazNotifySoundCB.setVisibility(View.GONE);
     		NamaazNotifyVibrateTV.setVisibility(View.GONE);
     		NamaazNotifyVibrateSpinner.setVisibility(View.GONE);
     		testNamaazButton.setVisibility(View.GONE);
     	}
     	
     	if (prefs.getBoolean(SETTING_MIQAAT_NOTIFY, false)) {
 			testMiqaatButton.setVisibility(View.VISIBLE);
     	}
     	else {
     		testMiqaatButton.setVisibility(View.GONE);
     	}
    }

    public boolean savePreferences() {

    	boolean ampm = true;
    	int fontSize;

    	float scale = context.getResources().getDisplayMetrics().density;
    	scale = 1; //no need to scale since .setTextSize already device-independant in dp
    	
    	if (FontSizeSpinner.getSelectedItemPosition() == 0)
    		fontSize = (int) (FONTSIZE_SMALL*scale);
    	else if (FontSizeSpinner.getSelectedItemPosition() == 1)
    		fontSize = (int) (FONTSIZE_MEDIUM*scale);
    	else if (FontSizeSpinner.getSelectedItemPosition() == 2)
    		fontSize = (int) (FONTSIZE_LARGE*scale);
    	else
    		fontSize = (int) (FONTSIZE_XLARGE*scale);
    	
    	if (AMPMSpinner.getSelectedItemPosition() == 0)
    		ampm = true;
    	else
    		ampm = false;
    	
    	int calType = CalTypeSpinner.getSelectedItemPosition();
    	
    	boolean calGreg = CalGregCB.isChecked();
    	//boolean calAll = CalAllCB.isChecked();
    	boolean calAll = true;
    	boolean showDB = CalShowDB.isChecked();
    	boolean namaazNotify = NamaazNotifyCB.isChecked();
    	boolean namaazNotifySound = NamaazNotifySoundCB.isChecked();
    	int namaazNotifyVibrate = NamaazNotifyVibrateSpinner.getSelectedItemPosition();
    	boolean miqaatNotify = MiqaatNotifyCB.isChecked();
    	boolean liveGPS = LiveGPSCB.isChecked();
    	
    	String toasty = "Settings saved." ;
    	//toasty = Float.toString(scale);
    	Context context = getApplicationContext();
    	Toast toast = Toast.makeText(context, toasty, Toast.LENGTH_LONG);
   		toast.show();

    	Editor editor = prefs.edit();
    	editor.putInt(SETTING_FONTSIZE, FontSizeSpinner.getSelectedItemPosition());
    	editor.putInt(SETTING_ACTUAL_FONT_SIZE, fontSize);    	
    	editor.putBoolean(SETTING_AMPM, ampm);
    	editor.putInt(SETTING_CALTYPE, calType);
    	editor.putBoolean(SETTING_SHOWDB, showDB);
    	editor.putBoolean(SETTING_SHOWGREG, calGreg);
    	editor.putBoolean(SETTING_SHOWALL, calAll);
    	editor.putBoolean(SETTING_NAMAAZ_NOTIFY, namaazNotify);
    	editor.putBoolean(SETTING_NAMAAZ_NOTIFY_SOUND, namaazNotifySound);
    	editor.putInt(SETTING_NAMAAZ_NOTIFY_VIBRATE, namaazNotifyVibrate);
    	editor.putBoolean(SETTING_MIQAAT_NOTIFY, miqaatNotify);
    	editor.putBoolean(SETTING_LIVEGPS, liveGPS);
    	editor.commit();
    	
    	return true;
    }
}