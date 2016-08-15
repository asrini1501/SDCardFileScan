package files.com.org.filescan;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import util.FileArrayAdapter;
import util.FileSizeComaparator;
import util.ListFiles;
/**
 * Created by Srinivas on 8/12/2016.
 */

public class FileScanHomeActivity extends AppCompatActivity {

    private ArrayList<File> fileList = new ArrayList<File>();
    private ListView listView;
    long totaldata;
    long average;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_scan_home);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("Top 10 Files");

        fileList = ListFiles.getFileList();

        if(ListFiles.getFlag()==1){

            Collections.sort(fileList, new FileSizeComaparator());
            listView = (ListView) findViewById(R.id.listView);
            FileArrayAdapter adapter = new FileArrayAdapter(this, R.layout.file_list_view, fileList);
            listView.setAdapter(adapter);
            //ListFiles.setFileList(null);
        }
        else {
            Intent intent=new Intent(FileScanHomeActivity.this,MainHomeActivity.class);
            startActivity(intent);
            finish();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
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




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    public String formatSize(long v) {
        if (v < 1024) {
            return v + " B";
        }
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), "KMGTPE".charAt(z - 1));
    }


}