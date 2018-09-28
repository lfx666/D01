package com.dc.f01.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
public class DataTools {
    public static void setPrepareStatementParameter(PreparedStatement stmt, int index, String type, Object value) throws Exception{
        if (type.equalsIgnoreCase("String")) {
            String content = value.toString();
            if (content.length() > 2000) {
                stmt.setCharacterStream(index, new StringReader(content), content.length());
            }
            else
                stmt.setString(index, content);
        }
        else if (type.equalsIgnoreCase("Short")) { stmt.setShort(index, Short.parseShort(value.toString()));
        } else if (type.equalsIgnoreCase("Integer")) { stmt.setInt(index, Integer.parseInt(value.toString()));
        } else if (type.equalsIgnoreCase("Long")) { stmt.setLong(index, Long.parseLong(value.toString()));
        } else if (type.equalsIgnoreCase("Double")) { stmt.setDouble(index, Double.parseDouble(value.toString()));
        } else if (type.equalsIgnoreCase("Float")) { stmt.setFloat(index, Float.parseFloat(value.toString()));
        } else if (type.equalsIgnoreCase("Byte")) { stmt.setByte(index, Byte.parseByte(value.toString()));
        } else if (type.equalsIgnoreCase("Char")) { stmt.setString(index, value.toString());
        } else if (type.equalsIgnoreCase("Boolean")) { stmt.setBoolean(index, Boolean.getBoolean(value.toString()));
        } else if (type.equalsIgnoreCase("Date")) {
            if (value instanceof java.sql.Date)
                stmt.setDate(index, (java.sql.Date)value);
            else
                stmt.setDate(index, java.sql.Date.valueOf(value.toString()));
        } else if (type.equalsIgnoreCase("Time")) {
            if (value instanceof Time)
                stmt.setTime(index, (Time)value);
            else
                stmt.setTime(index, Time.valueOf(value.toString()));
        } else if (type.equalsIgnoreCase("DateTime")) {
            if (value instanceof Timestamp)
                stmt.setTimestamp(index, (Timestamp)value);
            else if (value instanceof java.sql.Date)
                stmt.setTimestamp(index, new Timestamp(((java.sql.Date)value).getTime()));
            else
                stmt.setTimestamp(index, Timestamp.valueOf(value.toString()));
        }else if (value instanceof Character) {
            stmt.setString(index, value.toString());
        } else {
            stmt.setObject(index, value);
        }
    }
    public static String transferToString(Object value, String type){
        return transferToString(value, type, -1);
    }
    public static String transferToString(Object value, String type, int precision){
        if (value == null){
            return "";
        }
        String result = "";
        if (type.equalsIgnoreCase("Date")) {
            if ((value instanceof java.util.Date) || (value instanceof Timestamp)) {
                try {
                    SimpleDateFormat DATA_FORMAT_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
                    result = DATA_FORMAT_yyyyMMdd.format(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "";
                }
            }else{
                result = value.toString();
            }
        }else if (type.equalsIgnoreCase("Time")) {
            if ((value instanceof java.util.Date) || (value instanceof Time) || (value instanceof Timestamp)) {
                try {
                    SimpleDateFormat DATA_FORMAT_HHmmss = new SimpleDateFormat("HH:mm:ss");
                    result = DATA_FORMAT_HHmmss.format(value);
                }catch (Exception e) {
                    e.printStackTrace();
                    result = "";
                }
            }else{
                result = value.toString();
            }
        }else if (type.equalsIgnoreCase("DateTime")) {
            if ((value instanceof java.util.Date) || (value instanceof Timestamp)) {
                try {
                    SimpleDateFormat DATA_FORMAT_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    result = DATA_FORMAT_yyyyMMddHHmmss.format(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "";
                }
            } else{
                result = value.toString();
            }
        }else if ((type.equalsIgnoreCase("Double")) || (type.equalsIgnoreCase("Float"))) {
            NumberFormat nf = NumberFormat.getInstance();
            if (precision >= 0) {
                try {
                    nf.setMaximumFractionDigits(precision);
                    nf.setGroupingUsed(false);
                    result = nf.format(nf.parse(value.toString()).doubleValue());
                }catch (Exception ex){
                    ex.printStackTrace();
                    result = value.toString();
                }
            }else{
                result = value.toString();
            }
        }else {
            result = value.toString();
        }
        return result;
    }
    public static Object transfer(Object value, String type){
        String msg;
        try{
            if (value == null){ return null;}
            if ((value instanceof String) && (value.toString().trim().equals(""))) {
                if ("String".equalsIgnoreCase(type)) {
                    return value;
                }
                return null;
            }
            if ((type.equalsIgnoreCase("Short")) || (type.equalsIgnoreCase("short"))) {
                if (value instanceof Short) {
                    return value;
                }
                return new Short(new BigDecimal(value.toString()).shortValue());
            }
            if ((type.equalsIgnoreCase("Integer")) || (type.equalsIgnoreCase("int"))) {
                if (value instanceof Integer) {
                    return value;
                }
                return new Integer(new BigDecimal(value.toString()).intValue());
            }
            if ((type.equalsIgnoreCase("Char")) || (type.equalsIgnoreCase("char"))) {
                if (value instanceof Character) {
                    return value;
                }
                return new Character(value.toString().charAt(0));
            }
            if ((type.equalsIgnoreCase("Long")) || (type.equalsIgnoreCase("long"))) {
                if (value instanceof Long) {
                    return value;
                }
                return new Long(new BigDecimal(value.toString()).longValue());
            }
            if (type.equalsIgnoreCase("String")) {
                if (value instanceof String) {
                    return value;
                }
                return value.toString(); }
            if (type.equalsIgnoreCase("Date")) {
                if (value instanceof java.sql.Date){
                    return value;
                }
                if (value instanceof Timestamp) {
                    return new java.sql.Date(((Timestamp) value).getTime());
                }
                String tmpstr = value.toString().replace('/', '-');
                SimpleDateFormat DATA_FORMAT_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
                return new java.sql.Date(DATA_FORMAT_yyyyMMdd.parse(tmpstr).getTime());
            }

            if (type.equalsIgnoreCase("Time")) {
                if (value instanceof Time){
                    return value;
                }
                if (value instanceof Timestamp) {
                    return new Time(((Timestamp) value).getTime());
                }
                SimpleDateFormat DATA_FORMAT_HHmmss = new SimpleDateFormat("HH:mm:ss");
                return new Time(DATA_FORMAT_HHmmss.parse(value.toString()).getTime());
            }
            if (type.equalsIgnoreCase("DateTime")) {
                if (value instanceof Timestamp){
                    return value;
                }
                if (value instanceof java.util.Date){
                    return new Timestamp(((java.util.Date)value).getTime());
                }
                SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String tmpstr = value.toString();
                if (tmpstr.trim().length() <= 10){
                    tmpstr = tmpstr + " 00:00:00";
                }
                return new Timestamp(a.parse(tmpstr).getTime());
            }
            if ((type.equalsIgnoreCase("Double")) || (type.equalsIgnoreCase("double"))) {
                if (value instanceof Double) {
                    return value;
                }
                return new Double(new BigDecimal(value.toString()).doubleValue()); }
            if ((type.equalsIgnoreCase("Float")) || (type.equalsIgnoreCase("float"))) {
                if (value instanceof Float) {
                    return value;
                }
                return new Float(new BigDecimal(value.toString()).floatValue()); }
            if ((type.equalsIgnoreCase("Byte")) || (type.equalsIgnoreCase("byte"))) {
                if (value instanceof Byte) {
                    return value;
                }
                return new Byte(new BigDecimal(value.toString()).byteValue()); }
            if ((type.equalsIgnoreCase("Boolean")) || (type.equalsIgnoreCase("boolean"))) {
                if (value instanceof Boolean){
                    return value;
                }
                if (value instanceof Number) {
                    if (((Number)value).doubleValue() > 0.0D) {
                        return new Boolean(true);
                    }
                    return new Boolean(false);
                }
                if (value instanceof String) {
                    if ((((String)value).equalsIgnoreCase("true")) || (((String)value).equalsIgnoreCase("y"))) {
                        return new Boolean(true);
                    }
                    return new Boolean(false);
                }
            }
        }catch (Exception e) {
            msg ="Cannot transform "+value.toString()+" to type "+type;
            throw new RuntimeException(msg, e);
        }
        return value;
    }
    public static String getAsString(Object obj){
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    public static short getAsShort(Object obj) {
        if (obj == null){
            return 0;
        }
        if (obj instanceof Number) {
            return ((Number)obj).shortValue();
        }
        return ((Short)transfer(obj, Short.class)).shortValue();
    }

    public static int getAsInt(Object obj) {
        if (obj == null){
            return 0;
        }
        if (obj instanceof Number) {
            return ((Number)obj).intValue();
        }
        return ((Integer)transfer(obj, Integer.class)).intValue();
    }

    public static long getAsLong(Object obj) {
        if (obj == null){
            return 0L;
        }
        if (obj instanceof Number) {
            return ((Number)obj).longValue();
        }
        return ((Long)transfer(obj, Long.class)).longValue();
    }

    public static double getAsDouble(Object obj) {
        if (obj == null){
            return 0.0D;
        }
        if (obj instanceof Number) {
            return ((Number)obj).doubleValue();
        }
        return ((Double)transfer(obj, Double.class)).doubleValue();
    }

    public static float getAsFloat(Object obj) {
        if (obj == null){
            return 0.0F;
        }
        if (obj instanceof Number) {
            return ((Number)obj).floatValue();
        }
        return ((Float)transfer(obj, Float.class)).floatValue();
    }

    public static byte getAsByte(Object obj) {
        if (obj == null){
            return 0;
        }
        if (obj instanceof Number) {
            return ((Number)obj).byteValue();
        }
        return ((Byte)transfer(obj, Byte.class)).byteValue();
    }

    public static boolean getAsBoolean(Object obj) {
        if (obj == null){
            return false;
        }
        if (obj instanceof Boolean) {
            return ((Boolean)obj).booleanValue();
        }
        return ((Boolean)transfer(obj, Boolean.class)).booleanValue();
    }

    public static char getAsChar(Object obj) {
        if (obj == null){
            return '\0';
        }
        if (obj instanceof Character){
            return ((Character)obj).charValue();
        }
        if ((obj instanceof String) && (((String)obj).length() == 1)) {
            return ((String)obj).charAt(0);
        }
        return ((Character)transfer(obj, Character.class)).charValue();
    }

    public static java.sql.Date getAsDate(Object obj) {
        if (obj == null){
            return null;
        }
        if (obj instanceof java.sql.Date){
            return ((java.sql.Date)obj);
        }
        if (obj instanceof Timestamp){
            return new java.sql.Date(((Timestamp)obj).getTime());
        }
        String msg ="Cannot transform "+obj.toString()+" to type "+obj.getClass().getName();
        throw new RuntimeException(msg);
    }

    public static Time getAsTime(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Time) {
            return ((Time) obj);
        }
        if (obj instanceof Timestamp){
            return new Time(((Timestamp)obj).getTime());
        }
        String msg ="Cannot transform "+obj.toString()+" to type "+obj.getClass().getName();
        throw new RuntimeException(msg);
    }

    public static Timestamp getAsDateTime(Object obj){
        if (obj == null) {
            return null;
        }
        if (obj instanceof Timestamp) {
            return ((Timestamp) obj);
        }
        if (obj instanceof java.sql.Date){
            return new Timestamp(((java.sql.Date)obj).getTime());
        }
        String msg ="Cannot transform "+obj.toString()+" to type "+obj.getClass().getName();
        throw new RuntimeException(msg);
    }

    public static Object transfer(Object value, Class type){
        SimpleDateFormat a;
        String msg;
        try {
            if (value == null){ return null;}
            if ((value instanceof String) && (value.toString().trim().equals(""))) {
                if (String.class.equals(type)) {
                    return value;
                }
                return null;
            }

            if ((type.equals(Short.class)) || (type.equals(Short.TYPE))) {
                if (value instanceof Short) {
                    return value;
                }
                return new Short(new BigDecimal(value.toString()).shortValue());
            }
            if ((type.equals(Integer.class)) || (type.equals(Integer.TYPE))) {
                if (value instanceof Integer) {
                    return value;
                }
                return new Integer(new BigDecimal(value.toString()).intValue());
            }
            if ((type.equals(Character.class)) || (type.equals(Character.TYPE))) {
                if (value instanceof Character) {
                    return value;
                }
                return new Character(value.toString().charAt(0));
            }
            if ((type.equals(Long.class)) || (type.equals(Long.TYPE))) {
                if (value instanceof Long) {
                    return value;
                }
                return new Long(new BigDecimal(value.toString()).longValue());
            }
            if (type.equals(String.class)) {
                if (value instanceof String) {
                    return value;
                }
                return value.toString();
            }
            if (type.equals(java.sql.Date.class)) {
                if (value instanceof java.sql.Date) {
                    return value;
                }
                if (value instanceof java.util.Date) {
                    return new java.sql.Date(((java.util.Date) value).getTime());
                }
                a = new SimpleDateFormat("yyyy-MM-dd");
                return new java.sql.Date(a.parse(value.toString()).getTime());
            }
            if (type.equals(Time.class)) {
                if (value instanceof Time){
                    return value;
                }
                if (value instanceof java.util.Date){
                    return new Time(((java.util.Date)value).getTime());
                }
                a = new SimpleDateFormat("HH:mm:ss");
                return new Time(a.parse(value.toString()).getTime());
            }
            if (type.equals(Timestamp.class)) {
                if (value instanceof Timestamp) {
                    return value;
                }
                if (value instanceof java.util.Date) {
                    return new Timestamp(((java.util.Date) value).getTime());
                }
                a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String tmpstr = value.toString();
                if (tmpstr.trim().length() <= 10) {
                    tmpstr = tmpstr + " 00:00:00";
                }
                return new Timestamp(a.parse(tmpstr).getTime());
            }
            if ((type.equals(Double.class)) || (type.equals(Double.TYPE))) {
                if (value instanceof Double) {
                    return value;
                }
                return new Double(new BigDecimal(value.toString()).doubleValue()); }
            if ((type.equals(Float.class)) || (type.equals(Float.TYPE))) {
                if (value instanceof Float) {
                    return value;
                }
                return new Float(new BigDecimal(value.toString()).floatValue()); }
            if ((type.equals(Byte.class)) || (type.equals(Byte.TYPE))) {
                if (value instanceof Byte) {
                    return value;
                }
                return new Byte(new BigDecimal(value.toString()).byteValue()); }
            if ((type.equals(Boolean.class)) || (type.equals(Boolean.TYPE))) {
                if (value instanceof Boolean) {
                    return value;
                }
                if (value instanceof Number) {
                    if (((Number)value).doubleValue() > 0.0D) {
                        return new Boolean(true);
                    }
                    return new Boolean(false);
                }
                if (value instanceof String) {
                    if ((((String)value).equalsIgnoreCase("true")) || (((String)value).equalsIgnoreCase("y"))) {
                        return new Boolean(true);
                    }
                    return new Boolean(false);
                }
            }
        }catch (Exception e){
            msg ="Cannot transform "+value.toString()+" to type "+type;
            throw new RuntimeException(msg, e);
        }
        return value;
    }

    public static JSONObject stringtoJsonObj(String jasonStr) {
        JSONArray array = JSONArray.fromObject(jasonStr);
        return array.size()>0?JSONObject.fromObject(array.get(0)):null;
    }

    public static BigDecimal getAsBigDecimal(Object obj) {
        if (obj == null){
            return new BigDecimal("0");
        }
        return new BigDecimal(obj.toString());
    }
    //base64 加密
    public static String getBase64Encode(String strs){
        try {
            return new String(Base64.encodeBase64(strs.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    //base64 解密
    public static String getBase64Decode(String strs){
        try {
            return new String(Base64.decodeBase64(strs),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取随机（0~9）位不同的随机数
    public static String getbetContent(){
        int randomNum=(int)(Math.random()*10); //获取0-9随机数

        int[] array ={0,1,2,3,4,5,6,7,8,9};

        Random rand = new Random();

        for (int i = 10; i > 1; i--) {

            int index =rand.nextInt(i);

            int tmp =array[index];

            array[index] = array[i - 1];

            array[i - 1] = tmp;

        }
        int result = 0;
        String resultStr="";
        for(int i = 0; i <= randomNum; i++){
        resultStr +=getAsString(result * 10 + array[i]);
        }
        return resultStr;
    }

}
