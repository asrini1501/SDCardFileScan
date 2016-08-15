package util;

import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Srinivas on 8/9/2016.
 */
public class ListFiles {


    public static void setFileList(ArrayList<File> fileList) {
        ListFiles.fileList = fileList;
    }

    public static ArrayList<File> getFileList() {
        return fileList;
    }

    static ArrayList<File> fileList = new ArrayList<File>();

    public static void setFlag(int flag) {
        ListFiles.flag = flag;
    }

    public static int getFlag() {
        return flag;
    }

    static int flag;

    public static Map getSortedMap() {
        return sortedMap;
    }

    public static void setSortedMap(Map sortedMap) {
        ListFiles.sortedMap = sortedMap;
    }

    static Map sortedMap;
}
