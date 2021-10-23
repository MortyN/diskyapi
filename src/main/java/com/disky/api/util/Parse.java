package com.disky.api.util;



import org.apache.tomcat.util.codec.binary.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Parse {

    public static <T> boolean nullOrEmpty(List<T> list){
        return (list == null || list.size() <= 0);
    }
    public static <T> String listAsQuestionMarks(List<T> list){
        int idx = 0; String str = "";
        for(Object o : list)
            str += str == "" ? "? " : ",? ";
        return str;
    }
}
