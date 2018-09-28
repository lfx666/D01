package com.dc.f01.utils;

/**
 * Created by Administrator on 2016/8/15.
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyUtils {

    //类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
    //没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
    static class SingletonHolder{
        //静态初始化器，由JVM来保证线程安全
        static PropertyUtils instance=new PropertyUtils();
    }
    private static String appConfig="/config.properties";

    private static Map<String,Properties> map=new HashMap<String,Properties>();

    //私有化构造方法
    private PropertyUtils(){}

    //线程安全
    public static PropertyUtils getInstance (){
        return SingletonHolder.instance;
    }

    public String getProperValue(String properFileName,String key){
        Properties pro=null;
        if(!properFileName.startsWith("/")){
            properFileName="/"+properFileName;
        }
        if(map.containsKey(properFileName)){
            pro=map.get(properFileName);
        }else{
            pro=new Properties();
            InputStream is=PropertyUtils.class.getResourceAsStream(properFileName);
            try {
                pro.load(is);
                map.put(properFileName, pro);
                if(is!=null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pro.isEmpty()?null:pro.getProperty(key);
    }
    public String getProperValue(String key){
        return this.getProperValue(appConfig,key);
    }

    public static String findPropertiesKey(String key) {
        return PropertyUtils.getInstance().getProperValue(key);
    }
    public static void main(String[] args){
        //System.out.println(PropertyUtils.getInstance().getProperValue("appConfig.properties","test"));
        System.out.println(PropertyUtils.getInstance().getProperValue("test"));
    }
}

