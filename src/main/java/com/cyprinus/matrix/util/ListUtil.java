package com.cyprinus.matrix.util;

import java.util.*;

public class ListUtil {
    /**
     * list 求差集
     * @param n
     * @param m
     * @param <T>
     * @return
     */
    public static <T>List getDifferenceSet(List<T> n,List<T> m){
        //转化最长列表
        Set<T> set=new HashSet<>(n.size()>m.size()?n:m);
        //循环最短列表
        for (T t:n.size()>m.size()?m:n) {
            if(set.contains(t)){
                set.remove(t);
            }else {
                set.add(t);
            }
        }
        return new ArrayList(set);
    }
    /**
     * list 求交集
     * @param n
     * @param m
     * @param <T>
     * @return
     */
    public static <T>List getIntersection(List<T> n,List<T> m){
        Set<T> setN= new HashSet<>(n);
        Set<T> setM=new HashSet<>(m);
        setN.retainAll(setM);
        return new ArrayList(setN);
    }

    /**
     * list 集合并集
     * @param n
     * @param m
     * @param <T>
     * @return
     */
    public static <T>List getUnion(List<T> n,List<T> m){
        Set<T> setN= new HashSet<>(n);
        Set<T> setM=new HashSet<>(m);
        setN.addAll(setM);
        return new ArrayList(setN);
    }

    /**
     * 数组求差集
     * @param n
     * @param m
     * @param <T>
     * @return
     */
    public static <T>T[] getDifferenceSet(T[] n,T[] m){
        List<T> list= ListUtil.getDifferenceSet(Arrays.asList(n),Arrays.asList(m));
        return list.toArray(Arrays.copyOf(n,list.size()));
    }
    /**
     * 数组求交集
     * @param n
     * @param m
     * @param <T>
     * @return
     */
    public static <T>T[] getIntersection(T[] n,T[] m){
        List<T> list= ListUtil.getIntersection(Arrays.asList(n),Arrays.asList(m));
        return list.toArray(Arrays.copyOf(n,list.size()));
    }
    /**
     * 数组并集
     * @param n
     * @param m
     * @param <T>
     * @return
     */
    public static <T>T[] getUnion(T[] n,T[] m){
        List<T> list=ListUtil.getUnion(Arrays.asList(n),Arrays.asList(m));
        return list.toArray(Arrays.copyOf(n,list.size()));
    }

}
