package com.dc.f01.common;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageHelper {

	 public static final boolean isImage(File file){  
	        boolean flag = false;  
	        try  
	        {  
	            BufferedImage bufreader = ImageIO.read(file);  
	            int width = bufreader.getWidth();  
	            int height = bufreader.getHeight();  
	            if(width==0 || height==0){  
	                flag = false;  
	            }else {  
	                flag = true;  
	            }  
	        }  
	        catch (IOException e)  
	        {  
	            flag = false;  
	        }catch (Exception e) {  
	            flag = false;  
	        }  
	        return flag;  
	    }  
	 
	public static String getImageType(File f) {
		if (isImage(f))  
        {  
            try  
            {  
                ImageInputStream iis = ImageIO.createImageInputStream(f);  
                Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);  
                if (!iter.hasNext())  
                {  
                    return null;  
                }  
                ImageReader reader = iter.next();  
                iis.close();  
                return reader.getFormatName();  
            }  
            catch (IOException e)  
            {  
                return null;  
            }  
            catch (Exception e)  
            {  
                return null;  
            }  
        }  
        return null;  
	}

	
	public static void resizeImage(String path, String resizePath,int w,int h) throws IOException {
		  double wr=0,hr=0;  
	        File srcFile = new File(path);  
	        File destFile = new File(resizePath);  
	        BufferedImage bufImg = ImageIO.read(srcFile);  
	        @SuppressWarnings("static-access")
			Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);  
	        wr=w*1.0/bufImg.getWidth();  
	        hr=h*1.0 / bufImg.getHeight();  
	        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);  
	        Itemp = ato.filter(bufImg, null);  
	        try {  
	            ImageIO.write((BufferedImage) Itemp,resizePath.substring(resizePath.lastIndexOf(".")+1), destFile);  
	        } catch (Exception ex) {  
	            ex.printStackTrace();  
	        }  
		
	}
	
	
	public static void main(String[]args) throws IOException{
		String path = "d:\\temp\\ftp\\" + "1.png";  
        String resizePath = "d:\\temp\\ftp\\resize\\"  
                + "2.png";  
         File f = new File(path);
		  if (ImageHelper.isImage(f))  
	            ImageHelper.resizeImage(path, resizePath, 800, 600);  
	}

	

}
