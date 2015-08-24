package my.tools;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Test {

    public static void main(String[] args) {

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("java","a");
        map.put("go","a");
        map.put("objective-c","a");
        map.put("c#","a");


        Map<String,String> treeMap = new TreeMap<String, String>(
                new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return Integer.compare(s1.length(), s2.length());
                    }
                }
        );

        treeMap.putAll(map);

        System.out.println(treeMap);
    }
}
