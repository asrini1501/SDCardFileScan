package util;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.File;
import java.util.Comparator;

public class FileSizeComaparator implements Comparator<File> {
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public int compare(File a, File b) {
        long aSize = a.length();
        long bSize = b.length();
        if (aSize == bSize) {
            return 0;
        } else {
            return Long.compare(bSize, aSize);
        }
    }


}