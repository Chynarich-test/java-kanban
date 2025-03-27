package manager;

import java.util.Comparator;
import java.util.List;

public class FileBackedComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        String itemType1 = List.of(s1.split(",")).toArray(new String[0])[1];
        String itemType2 = List.of(s2.split(",")).toArray(new String[0])[1];

        if (itemType1.equals("EPIC") && !itemType2.equals("EPIC")) {
            return -1;
        } else if (!itemType1.equals("EPIC") && itemType2.equals("EPIC")) {
            return 1;
        } else {
            return 0;
        }
    }
}
