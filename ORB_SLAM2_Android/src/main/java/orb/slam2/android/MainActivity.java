package orb.slam2.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private String VOCPath = "/storage/emulated/0/SLAM/VOC/ORBvoc.txt";
	private String TUMPath = "/storage/emulated/0/SLAM/Calibration/List.yaml";
	Button datasetMode, testMode;

	Button ChooseCalibration, ChooseVOC;
	TextView CalibrationTxt, VOCPathText;

	private static final int REQUEST_CODE_2 = 2;   //TUM file request code
	private static final int REQUEST_CODE_3 = 3;   //VOC file request code
	private Intent fileChooserIntent;
	public static final String EXTRA_FILE_CHOOSER = "file_chooser";

	LinearLayout loading, origin;
	GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//Hide title
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Set full screen
		setContentView(R.layout.activity_main);
		//     datasetMode=(Button)findViewById(R.id.dataset_mode);
//		cameraMode=(Button)findViewById(R.id.camera_mode);
		testMode = (Button) findViewById(R.id.test_mode);
		//      datasetMode.setOnClickListener(this);
		//     cameraMode.setOnClickListener(this);
		testMode.setOnClickListener(this);

		ChooseCalibration = (Button) findViewById(R.id.choose_calibration);
		ChooseVOC = (Button) findViewById(R.id.choose_voc);
		ChooseCalibration.setOnClickListener(this);
		ChooseVOC.setOnClickListener(this);
		CalibrationTxt = (TextView) findViewById(R.id.cal_path_txt);
		VOCPathText = (TextView) findViewById(R.id.voc_path_txt);

//add on April 27th ,li
		CalibrationTxt.setText("calibration path is " + TUMPath);
		VOCPathText.setText("VOC path is " + VOCPath);

		fileChooserIntent = new Intent(this, FileChooserActivity.class);

		loading = (LinearLayout) this.findViewById(R.id.loading);
		origin = (LinearLayout) this.findViewById(R.id.origin);

		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		OnTouchListener rootListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mGestureDetector.onTouchEvent(event);
				return true;
			}
		};
		View rootView = findViewById(R.id.FrameLayout1);
		rootView.setOnTouchListener(rootListener);
	}

	// change on April 27th ,li
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.dataset_mode:
//			startActivity(new Intent(MainActivity.this,DataSetModeActivity.class));
				break;
//		case R.id.camera_mode:
//			//Toast.makeText(MainActivity.this, "on the way...", Toast.LENGTH_LONG).show();
//			startActivity(new Intent(MainActivity.this,CameraModeActivity.class));
//			break;

			case R.id.test_mode:
				//Toast.makeText(MainActivity.this, "on the way...", Toast.LENGTH_LONG).show();
				// startActivity(new Intent(MainActivity.this,TestModeActivity.class));
				Bundle bundle = new Bundle();
				bundle.putString("voc", VOCPath);
				bundle.putString("calibration", TUMPath);
				Intent intent = new Intent(MainActivity.this, ORBSLAMForTestActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.choose_calibration:
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					startActivityForResult(fileChooserIntent, REQUEST_CODE_2);
				} else {
					Toast.makeText(MainActivity.this, "can't find SDcard", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.choose_voc:
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					startActivityForResult(fileChooserIntent, REQUEST_CODE_3);
				} else {
					Toast.makeText(MainActivity.this, "can't find SDcard", Toast.LENGTH_LONG).show();
				}
				break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(MainActivity.this, "no return value", Toast.LENGTH_LONG).show();
			return;
		}
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_2) {
			//Get the path name
			TUMPath = data.getStringExtra(EXTRA_FILE_CHOOSER);
			CalibrationTxt.setText("calibration path is " + TUMPath);
			return;
		}
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_3) {
			//Get the path name
			VOCPath = data.getStringExtra(EXTRA_FILE_CHOOSER);
			VOCPathText.setText("VOC path is " + VOCPath);
			return;
		}
	}

	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// TODO Auto-generated method stub
			if ((e1.getX() - e2.getX() > 50) && Math.abs(velocityX) > 50) {
				loading.setVisibility(View.VISIBLE);
				origin.setVisibility(View.INVISIBLE);
			} else if ((e2.getX() - e1.getX() > 50) && Math.abs(velocityX) > 50) {
				origin.setVisibility(View.VISIBLE);
				loading.setVisibility(View.INVISIBLE);
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
}
