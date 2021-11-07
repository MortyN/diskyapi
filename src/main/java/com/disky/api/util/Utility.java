package com.disky.api.util;



import com.disky.api.model.GenericModel;
import org.apache.tomcat.util.codec.binary.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Utility {

    public static <T> boolean nullOrEmpty(Collection<T> list){
        return (list == null || list.size() <= 0);
    }
    public static <T> String listAsQuestionMarks(Collection<T> list){
        int idx = 0; String str = "";
        for(Object o : list)
            str += str == "" ? "? " : ",? ";
        return str;
    }

    public static <T extends GenericModel> boolean listContainsPrimaryKey(Collection<T> list, Long object){
        return !nullOrEmpty(list) && list.stream().anyMatch(o -> o.getPrimaryKey().equals(object));
    }
}
