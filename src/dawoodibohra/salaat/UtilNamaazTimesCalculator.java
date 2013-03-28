package dawoodibohra.salaat;

import android.text.format.Time;

public class UtilNamaazTimesCalculator {    

	private double lat = 0;
	private double lng = 0;
	private double tz = 0;
	private double pi = 3.14159265;
	private Time time = new Time();
	private double year=2010;
	private double month=1;
	private double day=1;
	
	//  zenith for sunrise/sunset is debatable. Online zenith angle of sunrise/sunset is 90.5, fajr is 99.5, sihori is 101.5 - values here are tweaked to meet mumineen.org
	private final double riseZenith=90.825;
	private final double setZenith=90.95;
	private final double fajrZenith=99.6;
	private final double sihoriZenith=101.5;

	public void setLocation(double latToSet, double lngToSet) {
		lat = latToSet;
		lng = lngToSet;
		return;
	}
	
	public void setTimezone(double tzToSet) {
		tz = tzToSet;
		return;
	}
	
	public void setTime(Time timeToSet) {
    	time.set(timeToSet);
    	setDate();
	}
	
	private void setDate() {
		year = time.year;
    	month = time.month + 1;
    	day = time.monthDay;		
	}

	public double[] getNamaazTimes() {
		double[] namaazTimes = calculateNamaazTimes(calculateRiseSet(false,riseZenith),calculateRiseSet(true,setZenith),calculateRiseSet(false,fajrZenith),calculateRiseSet(false,sihoriZenith));
		return namaazTimes;
	}
	
	public double[] getState() {
		double realTime = (double)time.hour + (double)time.minute/60. + (double)time.second/3600.;
		double[] namaazTimes = getNamaazTimes();
		double[] returnArray = {0,0,0,0};
		// returnArray[0] = state
		// returnArray[1] = timeInCurrentState
		// returnArray[2] = timeToNextState
		// returnArray[3] = timeToNextToNextState - only set in the cases needed
		
		// Note: namaazTimes[7] can be > 12 or < 12.. logic changes substantially in 2 modes
		if (((namaazTimes[7] < 12) && (realTime > namaazTimes[7]) && (realTime < (namaazTimes[6]+0.1667))) || ((namaazTimes[7] > 12) && ((realTime < (namaazTimes[6]+0.1667) || (realTime > namaazTimes[7]))))) { // fajr-magrib handled here
			if (realTime < namaazTimes[1]) {
				returnArray[0] = 0;
				returnArray[1] = realTime - namaazTimes[0]; 
				returnArray[2] = namaazTimes[1] - realTime;	// fajr to come in
			}
			else if (realTime < namaazTimes[2]) {
				returnArray[0] = 1;
				returnArray[1] = realTime - namaazTimes[1];
				returnArray[2] = namaazTimes[2] - realTime; // fajr ada for
			}
			else if (realTime < namaazTimes[3]) {
				returnArray[0] = 2;
				returnArray[1] = realTime - namaazTimes[2];
				returnArray[2] = namaazTimes[3] - realTime; // zohr to come in
			}
			else if (realTime < namaazTimes[4]) {
				returnArray[0] = 3;
				returnArray[1] = realTime - namaazTimes[3];
				returnArray[2] = namaazTimes[4] - realTime; // zohr/asr ada for
			}
			else if (realTime < namaazTimes[5]) {
				returnArray[0] = 4;
				returnArray[1] = realTime - namaazTimes[4];
				returnArray[2] = namaazTimes[5] - realTime; // asr ada for
				returnArray[3] = namaazTimes[6] - realTime; // magrib to come in
			}
			else if (realTime < namaazTimes[6]) {
				returnArray[0] = 5;
				returnArray[1] = realTime - namaazTimes[5];
				returnArray[2] = namaazTimes[6] - realTime; // magrib to come
			}
			else if (realTime < (namaazTimes[6]+.1667)) { // 0.1667 = 10 min rule for Magrib
				returnArray[0] = 6;
				returnArray[1] = realTime - namaazTimes[6];
				returnArray[2] = namaazTimes[6] + 0.1667 - realTime; // magrib ada for
				returnArray[3] = namaazTimes[7] - realTime; // isha ada for
				if (returnArray[3] < 0)
					returnArray[3] = returnArray[3] + 24;
			}
			// if realTime still large --> fajr to come for next day!
			else {
				returnArray[0] = 0;
				time.set(time.toMillis(false)+86400000);
				setDate();
				namaazTimes = getNamaazTimes();
				returnArray[1] = (24. - realTime) + namaazTimes[1];
				time.set(time.toMillis(false)-86400000);
				setDate();
			}
		}
		else { // magrib passed
			if ((namaazTimes[7] > 12) && (realTime < namaazTimes[7])) { // isha in PM
				// isha ada for
				returnArray[0] = 7;
				returnArray[1] = realTime - (namaazTimes[6] + 0.25) ;
				returnArray[2] = namaazTimes[7] - realTime;
			}
			else if ((namaazTimes[7] < 12) && (realTime > namaazTimes[6]+.25)) { // isha in AM, still in PM
				// isha ada for
				returnArray[0] = 7;
				returnArray[1] = realTime - (namaazTimes[6] + 0.25) ;
				returnArray[2] = namaazTimes[7] + 24. - realTime;
			}
			else if ((namaazTimes[7] < 12) && (realTime < namaazTimes[7])) { // isha in AM, time in AM
				// isha ada for
				returnArray[0] = 7;
				returnArray[1] = realTime + (24 - (namaazTimes[6] + 0.25)) ;
				returnArray[2] = namaazTimes[7] - realTime;
			}
		}
		
		return returnArray;
	}
	
	private double calculateRiseSet(Boolean set, double zenith) {
		
    	double N1 = Math.floor(275 * month / 9);
    	double N2 = Math.floor((month + 9) / 12);
    	double N3 = (1 + Math.floor((year - 4 * Math.floor(year / 4) + 2) / 3));
    	double N = N1 - (N2 * N3) + day - 30;
    	double lngHour = lng / 15;
    	double t ;
    	if (set)
    		t = N + ((18 - lngHour) / 24);
    	else
    		t = N + ((6 - lngHour) / 24);
    	double M = (0.9856 * t) - 3.289;
    	double L = (M + (1.916 * Math.sin(M*pi/180)) + (0.020 * Math.sin(2 * M*pi/180)) + 282.634) % 360;
    	double RA = Math.atan(0.91764 * Math.tan(L*pi/180))*180/pi;
    	double Lquadrant  = (Math.floor( L/90)) * 90;
    	double RAquadrant = (Math.floor(RA/90)) * 90;
    	RA = RA + (Lquadrant - RAquadrant);
    	RA = RA / 15;
    	double sinDec = 0.39782 * Math.sin(L*pi/180);
    	double cosDec = Math.cos(Math.asin(sinDec));
    	double cosH = (Math.cos(zenith*pi/180) - (sinDec * Math.sin(lat*pi/180))) / (cosDec * Math.cos(lat*pi/180));
    	double H ;
    	if (set)
    		H = Math.acos(cosH)*180/pi;
    	else
    		H = 360 - Math.acos(cosH)*180/pi;
    	H = H/15;
    	double T = H + RA - (0.06571 * t) - 6.622;
    	double UT = T - lngHour;
    	double localT = (UT + tz + 24) % 24;

    	return localT;
    }
	
    private double[] calculateNamaazTimes(double rise, double set, double fajr, double sihori){
    	double zawaal = (rise + set) / 2 ;
    	double dayghari = (set - rise) / 12 ;
    	double nightghari = 2 - dayghari ;
    	double zuhrend = zawaal + 2*dayghari ;
    	double asrend = zawaal + 4*dayghari ;
    	double nisfullayl = (zawaal + 12) % 24 ;
    	double nisfullaylend = nisfullayl + nightghari ;
    	
    	if ((rise - sihori) < 75./60.)
    		sihori = rise - 75./60. ; // 75 min rule of thumb
    	    	
    	double[] times = {sihori, fajr, rise, zawaal, zuhrend, asrend, set, nisfullayl, nisfullaylend} ;
    	return times;
    }
}