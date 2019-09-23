package orb.slam2.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import orb.slam2.android.FileChooserAdapter.FileInfo;

public class FileChooserActivity extends Activity {
	private GridView mGridView;
	private View mBackView;
	private View mBtExit, mBtOk;
	private TextView mTvPath;

	private String mSdcardRootPath;  //sdcard Root path
	private String mLastFilePath;    //Currently displayed path

	private ArrayList<FileInfo> mFileLists;
	private FileChooserAdapter mAdatper;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Set full screen
		setContentView(R.layout.filechooser_show);

		mSdcardRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();// �õ�sdcardĿ¼

		mBackView = findViewById(R.id.imgBackFolder);
		mBackView.setOnClickListener(mClickListener);
		mBtExit = findViewById(R.id.btExit);
		mBtExit.setOnClickListener(mClickListener);
		mBtOk = findViewById(R.id.btOK);
		mBtOk.setOnClickListener(mClickListener);

		mTvPath = (TextView) findViewById(R.id.tvPath);

		mGridView = (GridView) findViewById(R.id.gvFileChooser);
		mGridView.setEmptyView(findViewById(R.id.tvEmptyHint));
		mGridView.setOnItemClickListener(mItemClickListener);
		setGridViewAdapter(mSdcardRootPath);
	}

	//Configuration adapter
	private void setGridViewAdapter(String filePath) {
		updateFileItems(filePath);
		mAdatper = new FileChooserAdapter(this, mFileLists);
		mGridView.setAdapter(mAdatper);
	}

	//Update data based on path and notify Adatper to change data
	private void updateFileItems(String filePath) {
		mLastFilePath = filePath;
		mTvPath.setText(mLastFilePath);

		if (mFileLists == null)
			mFileLists = new ArrayList<FileInfo>();
		if (!mFileLists.isEmpty())
			mFileLists.clear();

		File[] files = folderScan(filePath);
		if (files == null)
			return;

		for (File file : files) {
			if (file.isHidden())  // Do not show hidden files
				continue;

			String fileAbsolutePath = file.getAbsolutePath();
			String fileName = file.getName();
			boolean isDirectory = false;
			if (file.isDirectory()) {
				isDirectory = true;
			}
			FileInfo fileInfo = new FileInfo(fileAbsolutePath, fileName, isDirectory);
			mFileLists.add(fileInfo);
		}
		//When first enter , the object of mAdatper don't initialized
		if (mAdatper != null)
			mAdatper.notifyDataSetChanged();  //Refresh
	}

	//Get all files for the current path
	private File[] folderScan(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		return files;
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.imgBackFolder:
					backProcess();
					break;
				case R.id.btExit:
					setResult(RESULT_CANCELED);
					finish();
					break;
				case R.id.btOK:
					Intent intent = new Intent();
					intent.putExtra(TestModeActivity.EXTRA_FILE_CHOOSER, mLastFilePath);
					setResult(RESULT_OK, intent);
					finish();
					break;
			}
		}
	};

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			FileInfo fileInfo = (FileInfo) (((FileChooserAdapter) adapterView.getAdapter()).getItem(position));
			//Click on the item as a folder to display all the files in the folder.
			if (fileInfo.isDirectory()) {
				updateFileItems(fileInfo.getFilePath());
			} else {
				Intent intent = new Intent();
				intent.putExtra(TestModeActivity.EXTRA_FILE_CHOOSER, fileInfo.getFilePath());
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			backProcess();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//Return to the previous directory operation
	public void backProcess() {
		//Determines whether the current path is an sdcard path. If not, it returns to the previous level.
		if (!mLastFilePath.equals(mSdcardRootPath)) {
			File thisFile = new File(mLastFilePath);
			String parentFilePath = thisFile.getParent();
			updateFileItems(parentFilePath);
		} else {   //Is the sdcard path, ending directly
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	private void toast(CharSequence hint) {
		Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
	}
}