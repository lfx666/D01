package com.dc.f01.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
 
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.dc.f01.utils.StringTools;


 


public class FileHelper {

	public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();    
    
    private FileHelper(){}    
    static{    
        getAllFileType();  //初始化文件类型信息    
    }    
	private static void getAllFileType()    
    {    
        FILE_TYPE_MAP.put("jpg", "FFD8FF"); //JPEG (jpg)    
        FILE_TYPE_MAP.put("png", "89504E47");  //PNG (png)    
        FILE_TYPE_MAP.put("gif", "47494638");  //GIF (gif)    
        FILE_TYPE_MAP.put("tif", "49492A00");  //TIFF (tif)    
        FILE_TYPE_MAP.put("bmp", "424D"); //Windows Bitmap (bmp)    
        FILE_TYPE_MAP.put("dwg", "41433130"); //CAD (dwg)    
        FILE_TYPE_MAP.put("html", "68746D6C3E");  //HTML (html)    
        FILE_TYPE_MAP.put("rtf", "7B5C727466");  //Rich Text Format (rtf)    
        FILE_TYPE_MAP.put("xml", "3C3F786D6C");    
        FILE_TYPE_MAP.put("zip", "504B0304");    
        FILE_TYPE_MAP.put("rar", "52617221");    
        FILE_TYPE_MAP.put("psd", "38425053");  //Photoshop (psd)    
        FILE_TYPE_MAP.put("eml", "44656C69766572792D646174653A");  //Email [thorough only] (eml)    
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F");  //Outlook Express (dbx)    
        FILE_TYPE_MAP.put("pst", "2142444E");  //Outlook (pst)    
        FILE_TYPE_MAP.put("xls", "D0CF11E0");  //MS Word    
        FILE_TYPE_MAP.put("doc", "D0CF11E0");  //MS Excel 注意：word 和 excel的文件头一样    
        FILE_TYPE_MAP.put("mdb", "5374616E64617264204A");  //MS Access (mdb)    
        FILE_TYPE_MAP.put("wpd", "FF575043"); //WordPerfect (wpd)     
        FILE_TYPE_MAP.put("eps", "252150532D41646F6265");    
        FILE_TYPE_MAP.put("ps", "252150532D41646F6265");    
        FILE_TYPE_MAP.put("pdf", "255044462D312E");  //Adobe Acrobat (pdf)    
        FILE_TYPE_MAP.put("qdf", "AC9EBD8F");  //Quicken (qdf)    
        FILE_TYPE_MAP.put("pwl", "E3828596");  //Windows Password (pwl)    
        FILE_TYPE_MAP.put("wav", "57415645");  //Wave (wav)    
        FILE_TYPE_MAP.put("avi", "41564920");    
        FILE_TYPE_MAP.put("ram", "2E7261FD");  //Real Audio (ram)    
        FILE_TYPE_MAP.put("rm", "2E524D46");  //Real Media (rm)    
        FILE_TYPE_MAP.put("mpg", "000001BA");  //    
        FILE_TYPE_MAP.put("mov", "6D6F6F76");  //Quicktime (mov)    
        FILE_TYPE_MAP.put("asf", "3026B2758E66CF11"); //Windows Media (asf)    
        FILE_TYPE_MAP.put("mid", "4D546864");  //MIDI (mid)    
    }    
	
	public final static String getFileTypeByFile(File file)    
    {    
        String filetype = null;    
        byte[] b = new byte[50];    
        try    
        {    
            InputStream is = new FileInputStream(file);    
            is.read(b);    
            filetype = getFileTypeByStream(b);    
            is.close();    
        }    
        catch (FileNotFoundException e)    
        {    
            e.printStackTrace();    
        }    
        catch (IOException e)    
        {    
            e.printStackTrace();    
        }    
        return filetype;    
    }    
	
	public final static String getFileTypeByStream(byte[] b)    
    {    
        String filetypeHex = String.valueOf(getFileHexString(b));    
        Iterator<Entry<String, String>> entryiterator = FILE_TYPE_MAP.entrySet().iterator();    
        while (entryiterator.hasNext()) {    
            Entry<String,String> entry =  entryiterator.next();    
            String fileTypeHexValue = entry.getValue();    
            if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {    
                return entry.getKey();    
            }    
        }    
        return null;    
    }    
	
	public final static String getFileHexString(byte[] b)    
    {    
        StringBuilder stringBuilder = new StringBuilder();    
        if (b == null || b.length <= 0)    
        {    
            return null;    
        }    
        for (int i = 0; i < b.length; i++)    
        {    
            int v = b[i] & 0xFF;    
            String hv = Integer.toHexString(v);    
            if (hv.length() < 2)    
            {    
                stringBuilder.append(0);    
            }    
            stringBuilder.append(hv);    
        }    
        return stringBuilder.toString();    
    }    
	
	/**
     * 统计文件（隐藏）
     */
    private final static String STATISTIC_FILE = ".cnt";
     
    private final static int MAX_FILE_CNT_PER_FOLDER=30000;
    
    public static File save(byte[] data, String savePath) {
        File file = null;
        try {
            file = new File(savePath);
            FileUtils.writeByteArrayToFile(file, data);

        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }
    
    /**
     * 保存文件
     * 
     * @param data
     * @param savePath
     * @param fileName
     * @return
     */
    public static File saveFile(byte[] data, String savePath,String fileName) {
        File file = null;
    	String retFileName="";
        if (!StringUtils.endsWith(savePath, "/")) {
            savePath += "/";
        }
        try {
             
            FileOperate fileOperate = new FileOperate(savePath + STATISTIC_FILE);
            int iFiles = StringTools.obj2Int(fileOperate
                    .readData(CNT_FILE_STRUCT.FILE_CNT_KEY));
            int iFolder = StringTools.obj2Int(fileOperate
                    .readData(CNT_FILE_STRUCT.FOLDER_CNT_KEY));
            if(iFolder>MAX_FILE_CNT_PER_FOLDER){
                throw new Exception("文件目录已经满了，无法再写");
            }
            int iFolderName=iFiles/MAX_FILE_CNT_PER_FOLDER+1;
             
            retFileName=iFolderName+"/"+fileName;
            file = new File(savePath+retFileName);
            FileUtils.writeByteArrayToFile(file, data);
            //保存文件后处理
            iFiles+=1;
            fileOperate.writeData(CNT_FILE_STRUCT.FILE_CNT_KEY, iFiles+"");
            fileOperate.writeData(CNT_FILE_STRUCT.FOLDER_CNT_KEY, iFolderName+"");
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }
 
    /**
     * 删除文件
     * @param filePath
     * @param fileName
     * @return
     */
    public static boolean delFile(String filePath,String fileName){
        if (!StringUtils.endsWith(filePath, "/")) {
            filePath += "/";
        }
         
        FileOperate fileOperate = new FileOperate(filePath + STATISTIC_FILE);
        File delFile=new File(filePath+fileName);
        if(delFile.exists()){
            delFile.delete();
            int iFile=StringTools.obj2Int(fileOperate.readData(CNT_FILE_STRUCT.FILE_CNT_KEY));
            fileOperate.writeData(CNT_FILE_STRUCT.FILE_CNT_KEY, ""+(iFile-1));
        }
        return true;
    }
     
    class CNT_FILE_STRUCT {
        public static final String FILE_CNT_KEY = "file_count";
        public static final String FOLDER_CNT_KEY = "folder_count";
    }
}
class FileOperate {
    /**
     * 指定property文件
     */
    private String PROPERTY_FILE;
 
    public FileOperate(String property_file) {
        super();
        PROPERTY_FILE = property_file;
    }
 
    /**
     * 根据Key 读取Value
     * 
     * @param key
     * @return
     */
    public String readData(String key) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(
                    PROPERTY_FILE));
            props.load(in);
            in.close();
            String value = props.getProperty(key);
            return value;
        } catch (Exception e) {
            return null;
        }
    }
 
    /**
     * 修改或添加键值对 如果key存在，修改 反之，添加。
     * 
     * @param key
     * @param value
     */
    public void writeData(String key, String value) {
        Properties prop = new Properties();
        try {
            java.io.File file = new java.io.File(PROPERTY_FILE);
            if (!file.exists())
                file.createNewFile();
            InputStream fis = new FileInputStream(file);
            prop.load(fis);
            fis.close();// 一定要在修改值之前关闭fis
            OutputStream fos = new FileOutputStream(PROPERTY_FILE);
            prop.setProperty(key, value);
            prop.store(fos, "Update '" + key + "' value");
            fos.close();
        } catch (IOException e) {
            System.err.println("Visit " + PROPERTY_FILE + " for updating "
                    + value + " value error");
        }
    }
}

