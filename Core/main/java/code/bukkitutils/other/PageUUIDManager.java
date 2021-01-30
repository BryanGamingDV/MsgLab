package code.bukkitutils.other;


import java.util.*;

public class PageUUIDManager {

    private final Map<Integer, List<UUID>> hashString;

    public PageUUIDManager(List<UUID> stringList) {
        hashString = new HashMap<>();

        int count = 0;
        int pagenumber = 0;

        List<UUID> page = new ArrayList<>();

        for (UUID string : stringList) {

            if (count >= 27) {
                hashString.put(pagenumber, page);
                page = new ArrayList<>();

                pagenumber++;
                count = 0;
            }

            page.add(string);
            count++;
        }

        if (count > 0){
            hashString.put(pagenumber, page);
        }
    }

    public Map<Integer, List<UUID>> getHash(){
        return hashString;
    }

    public Integer getMaxPage(){
        return hashString.size();
    }
}
