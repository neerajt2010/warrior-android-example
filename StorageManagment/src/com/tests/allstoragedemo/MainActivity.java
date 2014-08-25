package com.tests.allstoragedemo;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView tv;
	String TAG  = "-TAG-";
	static int count;
	String IMAGE_LIST_FILE = "imageinfo";
	String IMAGE_FOLDER = "MY_IMAGES" ;
	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button localDirBtn = (Button) findViewById(R.id.button1);
		Button localAssestReadBtn = (Button) findViewById(R.id.button2);
		Button readFromSDCardBtn = (Button) findViewById(R.id.button3);
		Button writeInSDCardBtn = (Button) findViewById(R.id.button4);
		Button takeSSBtn = (Button) findViewById(R.id.button5);
		Button readOhterPackageFile = (Button) findViewById(R.id.button6);

		tv = (TextView) findViewById(R.id.textView1);
		tv.setText("hello");

		localDirBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				writeFileToInternalStorage("storage.txt","coming from storage internal without permission");
				readFileFromInternalStorage("storage.txt");
			}
		});
		localAssestReadBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Properties prop = new Properties(); 
				try {
					prop = loadProptiesFromAssest();
				} catch (IOException e) {
					Log.e(TAG, "Exception", e);
				}
				Toast.makeText(getApplicationContext(), "Result " + prop.getProperty("port"),
						Toast.LENGTH_LONG).show();
			}
		});
		readFromSDCardBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					readFromSDCard("NewDir","myfile.txt");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}


		});
		writeInSDCardBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				writeOnSDCard("NewDir","myfile.txt","write some data here");

			}
		});
		takeSSBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				takeImage("new"+(count++));
				readFileFromInternalStorage(IMAGE_LIST_FILE);
			}
		});
		readOhterPackageFile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				writeFileToInternalStorageWithWorldReadablePermission("portConfig.txt","sciport=55000");
				//readFileFromInternalStorageOther("com.test.hello","sample.txt");

			}
		});
	}

	private void writeOnSDCard(String dirname,String filename,String dataToWrite){
		//<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> required in manifiest

		String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
				"/"+dirname+"/";

		FileOutputStream fOut = null;

		// Current state of the external media
		String extState = Environment.getExternalStorageState();

		// External media can be written onto
		if (extState.equals(Environment.MEDIA_MOUNTED))
		{
			try {
				// Make sure the path exists
				boolean exists = (new File(path)).exists();  
				if (!exists){ new File(path).mkdirs(); }  

				// Open output stream
				fOut = new FileOutputStream(path + filename);
				fOut.write(dataToWrite.getBytes());
				Log.d(TAG,"file written in sd card successfully");
				fOut.flush();


			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			finally {
				if (fOut != null) {
					try {
						fOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else{
			throw new RuntimeException(" sd card is not mounted ");
		}
	}

	private void readFromSDCard(String dir, String filename ) throws IOException {
		// Need permission android.permission.READ_EXTERNAL_STORAGE
		//String directory = Environment.getExternalStorageDirectory()+"/"+IMAGE_FOLDER;
		File file = new File(Environment.getExternalStorageDirectory()+"/"+dir+"/" + filename);
		Log.d("---", filename+"absolute path = "+file.getAbsolutePath()+"  canonical path --"+file.getCanonicalPath()+"----path--"+file.getPath());

		if (!file.exists()) {
			throw new RuntimeException("File not found at "+ file.getAbsolutePath());
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			Toast.makeText(getApplicationContext(), "Result " + builder.toString(),	Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


	}

	private void writeFileToInternalStorageWithWorldReadablePermission(String filename,String dataToWrite) {
		String eol = System.getProperty("line.separator");
		FileOutputStream fos = null;
		try {
			/*File cacheDir = getFilesDir();
			File file = new File(cacheDir, filename);
			Log.d("-----", "written in file "+file.getCanonicalPath());
			FileWriter fileWriter = new FileWriter(file);
			writer = new BufferedWriter(fileWriter);
			writer.write(dataToWrite + eol);*/

			fos = openFileOutput(filename, MODE_WORLD_READABLE);
			fos.write(dataToWrite.getBytes());
			fos.close();




		} catch (Exception e) {
			e.printStackTrace();
			Log.d("-----", "problem in file written in file successfully");
		} finally {
			Log.d("-----", "written in file successfully");
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void writeFileToInternalStorage(String filename,String dataToWrite) {
		String eol = System.getProperty("line.separator");
		BufferedWriter writer = null;
		try {
			File cacheDir = getFilesDir();
			File file = new File(cacheDir, filename);
			Log.d("-----", "written in file "+file.getCanonicalPath());
			FileWriter fileWriter = new FileWriter(file);
			writer = new BufferedWriter(fileWriter);
			writer.write(dataToWrite + eol);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("-----", "problem in file written in file successfully");
		} finally {
			Log.d("-----", "written in file successfully");
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readFileFromInternalStorage(String filename) {
		String eol = System.getProperty("line.separator");
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(openFileInput(filename)));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				buffer.append(line + eol);

			}

			tv.setText(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	} 
	private void readFileFromInternalStorageOtherApplicationWithWorldreadablePermission(String packagename,String filename) {
		String eol = System.getProperty("line.separator");
		BufferedReader input = null;
		try {

			input = new BufferedReader(new InputStreamReader(this.createPackageContext(packagename,0).openFileInput(filename)));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				buffer.append(line + eol);

			}

			tv.setText(buffer.toString());
		}
		catch(FileNotFoundException e){
			tv.setText(e.getMessage().toString());

		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	} 

	private Properties loadProptiesFromAssest() throws IOException {
		String[] fileList = { "config.properties" };
		Properties prop = new Properties();
		for (int i = fileList.length - 1; i >= 0; i--) {
			String file = fileList[i];
			try {
				InputStream fileStream = getAssets().open(file);
				prop.load(fileStream);
				fileStream.close();
			} catch (FileNotFoundException e) {
				Log.d("---", "Ignoring missing property file " + file);
			}
		}
		return prop;
	}

	private Properties loadProptiesFromAssest2(String filename /*"config.properties"*/) throws IOException {

		Properties prop = new Properties();

		String file = filename;
		try {
			InputStream fileStream = getAssets().open(file);
			prop.load(fileStream);
			fileStream.close();
		} catch (FileNotFoundException e) {
			Log.d("---", "Ignoring missing property file " + file);
		}

		return prop;
	}

	//-----------------------------image storing related methods----------------------------------------------------------------------------//

	/*This function is taking screenshot and then checking if sdcard is available then storing image is sd card else storing in internal 
	 * and updating a file in local which contain all the available image list.
	 * 
	 */
	
	private void  takeImage(String imageName){
		final int imageQuality = 90;
		final int imgMaxSize = 1000;
		FileOutputStream fos = null;
		
		System.gc();
		Bitmap bitmap;
		try {
			final Activity activity = this;
			if (activity != null) {

				// get the root view from the activity
				View view = activity.getWindow().getDecorView()
						.findViewById(android.R.id.content).getRootView();
				view.setDrawingCacheEnabled(true);
				view.setSaveEnabled(true);
				view.setEnabled(true);
				Bitmap originalImage = view.getDrawingCache();
				if (originalImage.getWidth() > imgMaxSize
						|| originalImage.getHeight() > imgMaxSize) {
					bitmap = Bitmap.createScaledBitmap(
							view.getDrawingCache(), originalImage.getWidth() / 2,
							originalImage.getHeight() / 2, true);
				} else {
					bitmap = Bitmap.createBitmap(originalImage);
				}
				
				String filepath = getFileCanonicalPath(imageName,this);
				
				Log.d(TAG,"file path coming is "+filepath);
				
				fos = new FileOutputStream(filepath);
				boolean success =bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, fos);
				bitmap.recycle();
				bitmap = null;
				if (success) {
					
					Log.e(TAG,"success--->"+ "{screenshot:\" written in file \"}");

				} else {
					Log.e(TAG,"Screenshot unsuccessful.");

					throw new IllegalStateException("No screenshot available.");
				}
				/**/

				/*else{
				Log.e(TAG,"No root activity for screenshot");
				throw new IllegalStateException("No screenshot available.");}*/
			}
		} 
		catch (Exception e) {
			/*if (i + 1 == SCREENSHOT_RETRIES) {
					throw new RuntimeException(e);
				} else {
					try {
						Thread.sleep(SCREENSHOT_RETRIES_DELAY);
					} catch (InterruptedException e1) {
					}
				}*/
			e.printStackTrace();
		}finally{
			try {
				fos.flush();
			
			fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	@SuppressLint("NewApi")
	private void  takessScaledImage(String imageName){ //giving null pointer error check later
		final int imageQuality = 90;
		final int imgMaxSize = 10000 ;
		FileOutputStream fos = null;
		System.gc();
		Bitmap bitmap;
		try {
			final Activity activity = this;
			if (activity != null) {

				// get the root view from the activity
				View view = activity.getWindow().getDecorView()
						.findViewById(android.R.id.content).getRootView();
				view.setDrawingCacheEnabled(true);
				view.setSaveEnabled(true);
				view.setEnabled(true);
				Bitmap originalImage = view.getDrawingCache();
				Log.d(TAG,"originalImage file size is  "+ originalImage.getByteCount());
				
				
				if (originalImage.getByteCount() > imgMaxSize ) {
					float sizefactor =originalImage.getByteCount()/imgMaxSize; 
					bitmap = Bitmap.createScaledBitmap(
							view.getDrawingCache(),(int) (originalImage.getWidth() / sizefactor),
							(int)(originalImage.getHeight() / sizefactor), true);
				} else {
					bitmap = Bitmap.createBitmap(originalImage);
				}

				String filepath = getFileCanonicalPath(imageName+".jpeg",this);

				Log.d(TAG,"file path coming is "+filepath);

				fos = new FileOutputStream(filepath);
				boolean success =bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, fos);
				bitmap.recycle();
				bitmap = null;
				if (success) {

					Log.e(TAG,"success--->"+ "{screenshot:\" written in file \"}");

				} else {
					Log.e(TAG,"Screenshot unsuccessful.");

					throw new IllegalStateException("No screenshot available.");
				}
				/**/

				/*else{
				Log.e(TAG,"No root activity for screenshot");
				throw new IllegalStateException("No screenshot available.");}*/
			}
		} 
		catch (Exception e) {
			/*if (i + 1 == SCREENSHOT_RETRIES) {
					throw new RuntimeException(e);
				} else {
					try {
						Thread.sleep(SCREENSHOT_RETRIES_DELAY);
					} catch (InterruptedException e1) {
					}
				}*/
			e.printStackTrace();
		}finally{
			try {
				fos.flush();

				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private String getFileCanonicalPath(String imageName, Context context) {
		String imagePath = "";

		try {
			if (checkExternalStorageAvailability(context)) {
				Log.d(TAG, "storing in sd card");
				String dirpath = Environment.getExternalStorageDirectory().getAbsolutePath() +
						"/"+IMAGE_FOLDER+"/";

				boolean exists = (new File(dirpath)).exists();  
				if (!exists){ new File(dirpath).mkdirs(); } 

				File file = new File(dirpath+imageName);
				imagePath = file.getCanonicalPath();
				saveImagePathToFile(context, imagePath);

			} else {
				Log.d(TAG, "storing in local memory ");
				imagePath = context.getFilesDir().getCanonicalPath() + "/"
						+ imageName;
				saveImagePathToFile(context, imagePath);
			}
		} catch (IOException e) {
			Log.d(TAG, e.toString());
		}
		return imagePath;

	}

	@SuppressWarnings("deprecation")
	private void saveImagePathToFile(Context context, String filePath) {

		String listOfFiles = readExistingList(context);
		if (listOfFiles != null) {
			listOfFiles = listOfFiles + "," + filePath;
		} else {
			listOfFiles = filePath;
		}
		FileOutputStream fout = null;
		try {
			fout = context.openFileOutput(IMAGE_LIST_FILE,Context.MODE_WORLD_READABLE);
			fout.write(listOfFiles.getBytes("UTF-8"));
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			Log.d(TAG, e.toString());
		} finally {
			try {
				if (fout != null) {
					fout.close();
				}
			} catch (IOException e) {
				Log.d(TAG, e.toString());
			}
		}
	}

	private boolean checkExternalStorageAvailability(Context context) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
			int res = context.checkCallingOrSelfPermission(permission);
			return res == PackageManager.PERMISSION_GRANTED;
		} else {
			return false;
		}

	}

	private String readExistingList(Context context) {
		String listOfFiles = "";

		File imageList = null;
		imageList = new File(context.getFilesDir(),
				IMAGE_LIST_FILE);
		if (imageList != null && imageList.exists()) {
			FileInputStream fin = null;
			BufferedReader br = null;
			String line = "";
			StringBuffer buffer = new StringBuffer();
			try {
				fin = new FileInputStream(imageList);
				br = new BufferedReader(new InputStreamReader(fin, "UTF-8"));
				while ((line = br.readLine()) != null) {
					buffer.append(line);
				}
			}

			catch (IOException e) {

			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e1) {
					Log.d(TAG, e1.toString());
				}
			}
			try {
				imageList.delete();
			} catch (SecurityException e) {
				Log.d(TAG, e.toString());
			}
			listOfFiles = buffer.toString();
		} else {
			listOfFiles = null;
		}

		return listOfFiles;
	}

}
