package me.bryangaming.chatlab.utils.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageCreator {

    private Map<Integer, List<String>> hashString;

    public PageCreator(List<String> stringList) {
        hashString = new HashMap<>();

        int count = 0;
        int pagenumber = 0;

        List<String> page = new ArrayList<>();

        for (String string : stringList) {
            if (count >= 9) {
                hashString.put(pagenumber, page);
                page = new ArrayList<>();

                pagenumber++;
                count = 0;
            }

            page.add(string);
            count++;
        }

        if (count > 0) {
            hashString.put(pagenumber, page);
        }
    }

    public Map<Integer, List<String>> getHashString() {
        return hashString;
    }

    public Integer getMaxPage() {
        return hashString.size();
    }

    public boolean pageExists(Integer page) {
        return hashString.get(page) != null;
    }

    public boolean lineExists(Integer page, Integer line) {
        return hashString.get(page).get(line - 1) != null;
    }
}
