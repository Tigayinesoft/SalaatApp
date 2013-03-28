package dawoodibohra.salaat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ViewCompass extends View {
	
	private Paint markerPaint;
	private Paint textPaint;
	private Paint circlePaint;
	private Paint qiblaPaint;
	private String northString;
	private String eastString;
	private String southString;
	private String westString;
	private float bearing;
	public int qibla = 0 ;
	public int sun = 360 ; //set to 360+ to not show up
	public int sunAltitude = 0;
	public int moon = 360 ; //set to 360+ to not show up
	public int moonAltitude = 0;
	
	public ViewCompass(Context context) {
		super(context);
		initCompassView();
	}
	
	public ViewCompass(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCompassView();
	}
	
	public ViewCompass(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		initCompassView();
	}
	
	protected void initCompassView() {
		setFocusable(true);
		
		Resources r = this.getResources();
		
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor(r.getColor(R.color.background_color));
		circlePaint.setStrokeWidth(1);
		circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		northString = r.getString(R.string.cardinal_north);
		eastString = r.getString(R.string.cardinal_east);
		southString = r.getString(R.string.cardinal_south);
		westString = r.getString(R.string.cardinal_west);
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(r.getColor(R.color.text_color));
		textPaint.setTextSize(25);
		
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint.setColor(r.getColor(R.color.marker_color));

		qiblaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		qiblaPaint.setColor(r.getColor(R.color.qibla_color));
		qiblaPaint.setStrokeWidth(2);
		
		
	}

	public void setBearing(float _bearing) {
		bearing = _bearing;
	}
	
	public float getBearing() {
		return bearing;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int px = getMeasuredWidth()/2;
		int py = getMeasuredHeight()/2;
		int radius = Math.min(px,py);
		
		Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
	    int width = bitmapOrg.getWidth();
    	int height = bitmapOrg.getHeight();
    	int newWidth = radius/4;
    	int newHeight = radius/4;
    	float scaleWidth = ((float) newWidth) / width;
    	float scaleHeight = ((float) newHeight) / height;
    	Matrix matrix = new Matrix();
    	matrix.postScale(scaleWidth, scaleHeight);
    	// matrix.postRotate(45);    	
    	Bitmap resizedBitmapQibla = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
    	//BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
    	
    	Bitmap bitmapOrg2 = BitmapFactory.decodeResource(getResources(),R.drawable.sun);
	    int width2 = bitmapOrg2.getWidth();
    	int height2 = bitmapOrg2.getHeight();
    	int newWidth2 = radius/3;
    	int newHeight2 = radius/3;
    	float scaleWidth2 = ((float) newWidth2) / width2;
    	float scaleHeight2 = ((float) newHeight2) / height2;
    	Matrix matrix2 = new Matrix();
    	matrix2.postScale(scaleWidth2, scaleHeight2);
    	Bitmap resizedBitmapSun = Bitmap.createBitmap(bitmapOrg2, 0, 0, width2, height2, matrix2, true);

    	Bitmap bitmapOrg3 = BitmapFactory.decodeResource(getResources(),R.drawable.moon);
	    int width3 = bitmapOrg3.getWidth();
    	int height3 = bitmapOrg3.getHeight();
    	int newWidth3 = radius/3;
    	int newHeight3 = radius/3;
    	float scaleWidth3 = ((float) newWidth3) / width3;
    	float scaleHeight3 = ((float) newHeight3) / height3;
    	Matrix matrix3 = new Matrix();
    	matrix3.postScale(scaleWidth3, scaleHeight3);
    	Bitmap resizedBitmapMoon = Bitmap.createBitmap(bitmapOrg3, 0, 0, width3, height3, matrix3, true);

    	canvas.drawCircle(px, py, radius, circlePaint);
    	canvas.save();
    	canvas.rotate(-bearing,px,py);
    	
    	int textHeight;
		int textWidth;
		
		textPaint.setTextSize(radius/5);
		
		for (int i=0;i<24;i++) { //Draw stuff every 15
			if (i%2==0) //Draw a marker every 30.
				canvas.drawLine(px,py-radius,px,py-radius+radius/12,markerPaint); 
			canvas.save();
			canvas.translate(0,radius/5);
		
			if (i%6==0) { // Draw the 4 cardinal points
				String dirString="";
				switch(i) {
					case(0): dirString=northString; break;
					case(6): dirString=eastString; break;
					case(12): dirString=southString; break;
					case(18): dirString=westString; break;
				}
				textPaint.setTextSize(radius/6);
				textHeight = (int)textPaint.measureText(dirString);
				textWidth = (int)textPaint.measureText(dirString);
				canvas.drawText(dirString,px-textWidth/2,py-radius+textHeight/2,textPaint);
			}
			else if(i%2==0) { //Draw the text every alternate 30deg
				String angle = String.valueOf(i*15);
				textPaint.setTextSize(radius/10);
				textHeight = (int)textPaint.measureText(angle);
				textWidth = (int)textPaint.measureText(angle);
				float angleTextWidth = textPaint.measureText(angle);
				canvas.drawText(angle,px-angleTextWidth/2,py-radius,textPaint);
			}
			canvas.restore();
			canvas.rotate(15,px,py);
		}
		
		if (sunAltitude < newHeight2/2*90/radius)
			sunAltitude = newHeight2/2*90/radius;
		
		if (moonAltitude < newHeight3/2*90/radius)
			moonAltitude = newHeight3/2*90/radius;
		
		for (int i=0;i<360;i++) {
			if (i == sun) {
				// canvas.drawLine(px,py,px,py-radius,qiblaPaint); // center line for reference
				canvas.drawBitmap(resizedBitmapSun, px-newWidth2/2, (py-radius)+radius*sunAltitude/90-newHeight2/2, qiblaPaint);
			}
			if (i == moon) {
				// canvas.drawLine(px,py,px,py-radius,qiblaPaint); // center line for reference
				canvas.drawBitmap(resizedBitmapMoon, px-newWidth3/2, (py-radius)+radius*moonAltitude/90-newHeight3/2, qiblaPaint);
			}
			canvas.rotate(1,px,py);
		}
	
		for (int i=0;i<360;i++) {
		if (i == qibla) {
			canvas.drawLine(px+radius/15,py,px-radius/15,py,qiblaPaint);
			canvas.drawLine(px,py-radius,px+radius/15,py,qiblaPaint);
			canvas.drawLine(px,py-radius,px-radius/15,py,qiblaPaint);
			//old arrow
			//canvas.drawLine(px,py-radius,px-radius/8,py-radius+radius/8,qiblaPaint);
			//canvas.drawLine(px,py-radius,px+radius/8,py-radius+radius/8,qiblaPaint);
	    	canvas.drawBitmap(resizedBitmapQibla, px-newWidth/2, 55*py/100, qiblaPaint);
		}
		canvas.rotate(1,px,py);
		}
		
		canvas.drawCircle (px, py, radius/35, markerPaint);
		canvas.save();
	}
}