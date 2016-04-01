package com.yanxinwei.bluetoothspppro.util;

import java.util.ArrayList;

/**
 * Created by yanxinwei on 16/4/1.
 */
public class S {

    private static int[] a = {121,-11,12,12,3,4,78,89};
    private static int[] b = {20,-11,12,12,78};
    private static int[] c = {89,20,-11,12,12,3,4};
    private static int[] d = {78,89,20,-11,12,12};
//    private static int[] e = {78,89,20,-11,12,12};
    private static int[] f = {3,4,78,89,20,-11,12,12,78,89,20,-11,12,12,3,4,78,89,20,-11,12,12,78,89,20};
    private static int[] g;

    public static void g(){
        if (g == null){
            g = new int[57];
            int i = 0;
            for (int[] ints : getList()){
                int r = c(ints, i);
                i += r;
            }
        }
    }

    public static ArrayList<int[]> getList(){
        ArrayList<int[]> result = new ArrayList<>();
        result.add(a);
        result.add(b);
        result.add(c);
        result.add(d);
        result.add(d);
        result.add(f);
        return result;
    }

    public static int c(int[] a, int i){
        int r = a.length;
        System.arraycopy(a, 0, g, i, a.length);
        return r;
    }

    public static String sign(String content){
        int length = content.length();
        char[] result = new char[length];
        for(int i = 0;i<length;i++){
            result[i] = (char) (content.charAt(i) ^ g[i]);
        }
        return new String(result);
    }

    public static void main(String[] args){
        g();
    }


}
