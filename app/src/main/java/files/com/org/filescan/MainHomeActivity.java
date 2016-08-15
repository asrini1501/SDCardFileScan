package files.com.org.filescan;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import util.FileArrayAdapter;
import util.FileSizeComaparator;
import util.ListFiles;
import util.Sort;

/**
 * Created by Srinivas on 8/12/2016.
 */

public class MainHomeActivity extends AppCompatActivity {

    Button scanBtn;
    Button bakBtn;
    Button homeBtn;
    Button show;
    Button share;
    ActivityManager am;

    Thread th;


    private File base;
    private ArrayList<File> fileList ;

    Intent intent;
    ProgressBar progress;
    LinearLayout linearLayout;
    LinearLayout linearLayoutR;
    RelativeLayout rLay;

    NotificationCompat.Builder nb=new NotificationCompat.Builder(this);

    NotificationCompat.Builder mBuilder =(NotificationCompat.Builder) nb
                    .setContentTitle("Search Notification");
    int mNotificationId = 001;


    HashMap<String, Integer> tableExt;


    private Context context;
    private NotificationManager nm;
    private Notification noti;
    private final int STATUS_BAR_NOTIFICATION = 1;

    TextView txt1;
    TextView txt2;
    TextView txt3;
    TextView txt4;
    TextView txt5;

    TextView txtVal1;
    TextView txtVal2;
    TextView txtVal3;
    TextView txtVal4;
    TextView txtVal5;

    TextView txtViewSize;

    TextView progressText;

    long totaldata;
    long average;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        scanBtn=(Button)findViewById(R.id.buttonScan);
        progress = (ProgressBar)findViewById(R.id.pb);
        bakBtn=(Button)findViewById(R.id.backBtn) ;
        homeBtn=(Button)findViewById(R.id.homeBtn) ;

        show=(Button)findViewById(R.id.show) ;
        share=(Button)findViewById(R.id.share) ;

        progress.setVisibility(ProgressBar.INVISIBLE);

        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
        linearLayout.setVisibility(LinearLayout.INVISIBLE);

        rLay=(RelativeLayout) findViewById(R.id.rLayout);
        rLay.setVisibility(LinearLayout.INVISIBLE);

        linearLayoutR=(LinearLayout)findViewById(R.id.linearLayoutR);
        linearLayoutR.setVisibility(LinearLayout.INVISIBLE);

        txt1=(TextView)findViewById(R.id.text1);
        txt2=(TextView)findViewById(R.id.text2);
        txt3=(TextView)findViewById(R.id.text3);
        txt4=(TextView)findViewById(R.id.text4);
        txt5=(TextView)findViewById(R.id.text5);


        txtVal1=(TextView)findViewById(R.id.textView1);
        txtVal2=(TextView)findViewById(R.id.textView2);
        txtVal3=(TextView)findViewById(R.id.textView3);
        txtVal4=(TextView)findViewById(R.id.textView4);
        txtVal5=(TextView)findViewById(R.id.textView5);

        txtViewSize=(TextView)findViewById(R.id.textViewSize);

        progressText=(TextView)findViewById(R.id.status_textMain);

        CharSequence tickerText = "hello";
        long when = System.currentTimeMillis();
        noti = new Notification(R.drawable.loading_1, tickerText, when);
        context = this.getApplicationContext();

        Intent notiIntent = new Intent(context, FileScanHomeActivity.class);
        PendingIntent pi = PendingIntent.getService(context, 0, notiIntent, 0);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        CharSequence title = "Downloading initializing...";
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notif);
        contentView.setImageViewResource(R.id.status_icon, R.drawable.loading_1);
        contentView.setTextViewText(R.id.status_text, title);
        contentView.setProgressBar(R.id.status_progress, 100, 0, false);
        noti.contentView = contentView;
        noti.contentIntent = pi;

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am= (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                linearLayoutR.setVisibility(LinearLayout.INVISIBLE);
                rLay.setVisibility(LinearLayout.INVISIBLE);
                new SearchSD().execute("");

            }
        });

        bakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                am.killBackgroundProcesses(getPackageName());
                ListFiles.setFileList(null);
                ListFiles.setFlag(0);

                Log.d("file name", "Back PRESSED" );

                statusBarProgressNotify("Search Stopped",0);

                progress.setVisibility(ProgressBar.INVISIBLE);
                linearLayout.setVisibility(LinearLayout.INVISIBLE);
                scanBtn.setVisibility(Button.VISIBLE);
                scanBtn.setText("Restart Scan");
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(LinearLayout.INVISIBLE);
                linearLayoutR.setVisibility(LinearLayout.VISIBLE);
                scanBtn.setVisibility(LinearLayout.VISIBLE);
                progress.setVisibility(ProgressBar.INVISIBLE);
                scanBtn.setText("Re-Scan");
                show.setEnabled(false);
                share.setEnabled(false);

            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(MainHomeActivity.this, FileScanHomeActivity.class);
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareIntent=new Intent(Intent.ACTION_SEND);

                shareIntent.setType("text/plain");

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"File Scan Result "+dateFormat.format(date));

                String dataShare="";

                ArrayList<File> shareList = ListFiles.getFileList();

                Collections.sort(shareList, new FileSizeComaparator());

                for (int i = 0; i <10; i++) {
                    dataShare=dataShare+"\n"+"File Name: " +shareList.get(i).getName()
                            +"\n"+"File Path: " +shareList.get(i).getPath()
                            +"\n"+"File Size: " +new MainHomeActivity().formatSize(shareList.get(i).length())
                            +"\n"+"---------------------------------------";
                }
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, dataShare);

                startActivity(shareIntent);
                shareList=null;
            }
        });



      }


    public void getExtfile(File dir) {
        //returns an array of files which are available in the given directory
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                //check for the file is directory
                if (listFile[i].isDirectory()) {
                    //recursive call
                    getExtfile(listFile[i]);
                } else {
                    Log.d("file name", "" + listFile[i].getName());
                    int checkforext = listFile[i].getName().lastIndexOf('.');

                    if (checkforext != -1) {
                        if (listFile[i].getName().charAt(checkforext) == '.')

                        {
                            fileList.add(listFile[i]);
                        }
                    }
                }

            }
        }
    }


    public void getExtOccurance(ArrayList<File> fileNameS){


        String extension;

        tableExt= new HashMap<String, Integer>();


        for(int i=0;i<fileNameS.size();i++)
        {

            int checkforext = fileNameS.get(i).getName().lastIndexOf('.');


            if (checkforext != -1) {

                extension = fileNameS.get(i).getName().substring(checkforext + 1).toLowerCase();

                if(tableExt.size()!=0) {
                    Integer count = tableExt.get(extension);
                    if (count == null) {
                        tableExt.put(extension, 1);
                    } else {
                        tableExt.put(extension, count + 1);
                    }
                }
                else{
                    tableExt.put(extension, 1);
                }

            }
        }


        Map sortedMap = new TreeMap();

        sortedMap= Sort.sortByValue(tableExt);

        Set keys= sortedMap.keySet();
        //int flag=0;

        for(Iterator k=keys.iterator();k.hasNext();)
        {

            String key=(String)k.next();

            System.out.println( key +" :------" + sortedMap.get(key));

        }

        ListFiles.setSortedMap(sortedMap);
    }


    private void statusBarProgressNotify(String status, int flg) {

        nb.setContentText(status);

        if(flg==1)
        nb.setSmallIcon(R.drawable.loading_1);
        else
        nb.setSmallIcon(R.mipmap.ic_launcher);


        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    /**
     * Background Async Task to download file
     * */
    class SearchSD extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //progress.setMax(50);
            progress.setVisibility(ProgressBar.VISIBLE);
            linearLayout.setVisibility(LinearLayout.VISIBLE);
            scanBtn.setVisibility(Button.INVISIBLE);
        }

        @Override
        protected void onCancelled() {

        }

        @Override
        protected String doInBackground(String... dummy) {

            ListFiles.setFileList(null);
            int count;
            ListFiles.setFlag(1);

            final String state = Environment.getExternalStorageState();


            if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {
                try {

                    base = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath());

                    fileList = new ArrayList<File>();


                    statusBarProgressNotify("Search is in Prgress", 1);


                    //returns an array of files which are available in the given directory
                    File listFile[] = base.listFiles();
                    if (listFile != null && listFile.length > 0) {
                        for (int i = 0; i < listFile.length; i++) {

                            publishProgress(i + "");

                            if (ListFiles.getFlag() == 1) {

                                CharSequence title = "Searching " + i % 100 + "%";
                                noti.contentView.setTextViewText(R.id.status_text, title);
                                noti.contentView.setProgressBar(R.id.status_progress, 100, i % 100, false);
                                nm.notify(STATUS_BAR_NOTIFICATION, noti);
                            }

                            //check for the file is directory
                            if (listFile[i].isDirectory()) {
                                //recursive call
                                getExtfile(listFile[i]);
                            } else {
                                Log.d("file name", "" + listFile[i].getName());
                                int checkforext = listFile[i].getName().lastIndexOf('.');

                                if (checkforext != -1) {
                                    if (listFile[i].getName().charAt(checkforext) == '.')

                                    {
                                        fileList.add(listFile[i]);
                                    }
                                }
                            }

                        }
                    }

                    publishProgress("100");


                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }

            }
                ListFiles.setFileList(fileList);

            totaldata=0;

            for (int i = 0; i < fileList.size(); i++) {
                totaldata += fileList.get(i).length();
            }
            average = totaldata / fileList.size();

                getExtOccurance(fileList);


            return null;
        }


        @Override
        protected void onProgressUpdate(String... prog) {
            progress.setProgress(Integer.parseInt(prog[0]));
            progressText.setText(Integer.parseInt(prog[0]) % 100 + "%");

        }

        @Override
        protected void onPostExecute(String file_url) {

            if(ListFiles.getFlag()==1) {
                linearLayoutR.setVisibility(LinearLayout.VISIBLE);
                rLay.setVisibility(LinearLayout.VISIBLE);
                show.setEnabled(true);
                share.setEnabled(true);
                statusBarProgressNotify("Search Completed", 0);


                Map sortedMap = ListFiles.getSortedMap();

                Set keys= sortedMap.keySet();
                int textFlg=1;

                for(Iterator k=keys.iterator();k.hasNext();)
                {
                    String key = (String) k.next();

                    if(textFlg<=5) {

                        if(textFlg==1){
                            txt1.setText(key);
                            txtVal1.setText(sortedMap.get(key)+"");
                            Log.v("******","1");
                        }
                        else if(textFlg==2){
                            txt2.setText(key);
                            txtVal2.setText(sortedMap.get(key)+"");
                            Log.v("******","2");

                        }
                        else if(textFlg==3){
                            txt3.setText(key);
                            txtVal3.setText(sortedMap.get(key)+"");
                            Log.v("******","3");

                        }
                        else if(textFlg==4){
                            txt4.setText(key);
                            txtVal4.setText(sortedMap.get(key)+"");

                        }
                        else if(textFlg==5){
                            txt5.setText(key);
                            txtVal5.setText(sortedMap.get(key)+"");
                        }
                    }
                    else{
                        break;
                    }

                    textFlg++;

                }

                txtViewSize.setText("Average File Size: "+formatSize(average));
                scanBtn.setText("Re-Scan");


            }
            progress.setVisibility(ProgressBar.INVISIBLE);
            linearLayout.setVisibility(LinearLayout.INVISIBLE);
            scanBtn.setVisibility(Button.VISIBLE);
            fileList=null;

        }

    }

    public String formatSize(long v) {
        if (v < 1024) {
            return v + " B";
        }
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), "KMGTPE".charAt(z - 1));
    }


}
