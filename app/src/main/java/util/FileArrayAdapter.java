package util;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import files.com.org.filescan.FileScanHomeActivity;
import files.com.org.filescan.R;


/**
 * Created by Srinivas on 8/9/2016.
 */

public class FileArrayAdapter extends ArrayAdapter {
    ArrayList<File> data = new ArrayList<>();
    int resource;
    LayoutInflater layoutInflater;


    public FileArrayAdapter(Context context, int resource, ArrayList<File> data) {
        super(context, resource);
        this.resource = resource;
        layoutInflater = LayoutInflater.from(context);
        for (int i = 0; i <10; i++) {
            this.data.add(data.get(i));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(resource, parent, false);
        TextView fname = (TextView) view.findViewById(R.id.filename);
        TextView fpath = (TextView) view.findViewById(R.id.fielpath);
        TextView fsize = (TextView) view.findViewById(R.id.filesize);
        fname.setText("File Name: " + data.get(position).getName());
        fpath.setText("File Path: " + data.get(position).getPath());
        fsize.setText("File Size: " + new FileScanHomeActivity().formatSize(data.get(position).length()));
        data.get(position).length();
        return view;
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
