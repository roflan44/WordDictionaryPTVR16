package utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class SortUtils {
    //reversed sord map. return sorted LinkedHashMap
    public static <K,V extends Comparable<? super V>> Map<K,V> sortMapReverseByValue(Map<K,V> map){
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                 .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            ));
    }
    public static <K,V extends Comparable<? super V>> Map<K,V> sortMapByValue(Map<K,V> map){
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                 .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            ));
    }
    
}