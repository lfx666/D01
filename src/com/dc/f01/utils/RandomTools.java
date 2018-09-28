package com.dc.f01.utils;

/**
 * Copyright: Copyright (c) 2014
 * Company: Fixed Star (Xiamen)
 * Version: 1.0.0
 * Author: ...
 * Date: 2014/9/3
 * Description:
 */
public final class RandomTools {
    private RandomTools(){}
    
    private static java.util.Random _RANDOM = new java.util.Random();
    public static int getRandomNum(int n){
        return (n > 0 ?  _RANDOM.nextInt(n):_RANDOM.nextInt());
    }
    public static long getRandomLong(){
        return _RANDOM.nextLong();
    }
    public static boolean getRandomBoolean(){
        return new Boolean(getRandomNum(2)==1);
    }
    /***************************根据当前时间生成随机码 start**************************/
    public static String getRandLongCurTimeMill(){
        return (String.valueOf(_RANDOM.nextLong()) + String.valueOf(System.currentTimeMillis())).replace("-","");
    }
    public static String getRandNumCurTimeMill(){
        return (String.valueOf(_RANDOM.nextInt()) + String.valueOf(System.currentTimeMillis())).replace("-","");
    }
    public static String getRandNumCurTimeMill(int n){
        return (String.valueOf(getRandomNum(n)) + String.valueOf(System.currentTimeMillis())).replace("-","");
    }
    /***************************根据当前时间生成随机码 end**************************/

    /**
     * 随机0-9加上英文大小写字母
     * @param totalLength
     * @return
     */
    public static String getRandNumAndEn(int totalLength){
        if(totalLength <= 0 || totalLength > 50){
            totalLength = 8;
        }
        StringBuilder rt = new StringBuilder(totalLength);
        for (int i=0;i<totalLength;i++){
            boolean isNum = getRandomBoolean();
            if(isNum){
                rt.append(getRandomNum(10));
            }else{
                int choice = getRandomBoolean() ? 65 : 97; //取得大写字母还是小写字母
                rt.append((char)(choice + getRandomNum(26)));
            }
        }
        return rt.toString();
    }
    
    /**
     * 获取随机码，第一个值为：英文字母
     * @param totalLength 随机码个数
     * @return
     */
    public static String getRandFirstEnAndNum(int totalLength){
        if(totalLength <= 0 || totalLength > 50){
            totalLength = 8;
        }
        StringBuilder rt = new StringBuilder(totalLength);
        for (int i=0;i<totalLength;i++){
            boolean isNum = getRandomBoolean();
            if (i==0) {//第一个值为：英文字母
            	int choice = getRandomBoolean() ? 65 : 97; 
                rt.append((char)(choice + getRandomNum(26)));
			}else{
				if(isNum){
	                rt.append(getRandomNum(10));
	            }else{
	                int choice = getRandomBoolean() ? 65 : 97; //取得大写字母还是小写字母
	                rt.append((char)(choice + getRandomNum(26)));
	            }
			}
        }
        return rt.toString();
    }
}
