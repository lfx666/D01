package com.dc.f01.utils;

import com.google.gson.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: JsonUtil
 * @Description: 

 */

public class JsonTools {

	/**
	 * @Title: objectToJson
	 * @Description: TODO(任意对象转成JSON键值对)
	 * @param @param object
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String objectToJson(Object object) {
		StringBuilder json = new StringBuilder();
		if (object == null) {
			json.append("\'\'");
		} else if (object instanceof String) {
			json.append("\'").append((String) object).append("\'");
		} else if (object instanceof Integer) {
			json.append("").append(String.valueOf(object)).append("");
		} else if(object instanceof HashMap){
            json.append(mapToJson((HashMap)object));
        } else {
			json.append(beanToJson(object));
		}
		return json.toString();
	}

	/**
	 * @Title: beanToJson
	 * @Description: TODO(传入一个对象转化为JSON串)
	 * @param @param bean
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String beanToJson(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = objectToJson(props[i].getName());
					String value = objectToJson(props[i].getReadMethod()
							.invoke(bean));
					if (value != null && !"\"\"".equals(value)) {
						json.append(name);
						json.append(":");
						json.append(value);
						json.append(",");
					}
				} catch (Exception e) {
					System.out.print("错误");
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @Title: listToJson
	 * @Description: TODO(传入一个list对象转化为JSON串)
	 * @param @param list
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String listToJson(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String listToJson2(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(beanToJson2(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String objectToJson2(Object object) {
		StringBuilder json = new StringBuilder();
		if (object == null) {
			json.append("\"\"");
		} else if (object instanceof String) {
			json.append("\"").append((String) object).append("\"");
		} else if (object instanceof Integer) {
			json.append("").append(String.valueOf(object)).append("");
		} else {
			json.append(beanToJson(object));
		}
		return json.toString();
	}

	public static String beanToJson2(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = props[i].getName();
					String value = objectToJson2(props[i].getReadMethod()
							.invoke(bean));
					if (value != null && !"\"\"".equals(value)) {
						json.append(name);
						json.append(":");
						json.append(value);
						json.append(",");
					}
				} catch (Exception e) {
					System.out.print("错误");
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * 不管参数有无值，都显示参数名
	 * 
	 * @param list
	 * @return
	 */
	public static String listToJson3(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(beanToJson3(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * 不管参数有无值，都显示参数名
	 * 
	 * @param bean
	 * @return
	 */
	public static String beanToJson3(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = props[i].getName();
					String value = objectToJson2(props[i].getReadMethod()
							.invoke(bean));
					// if (value!=null&&!"\"\"".equals(value))
					// {
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
					// }
				} catch (Exception e) {
					System.out.print("错误");
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * output json  
	 * @param obj
	 * @param response
	 */
	public static void outPutJson(Object obj, HttpServletResponse response) {
		ServletOutputStream ouputStream = null;
		try {
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(Timestamp.class,
							new TimestampTypeAdapter())
					.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			String message = gson.toJson(obj);
			response.setContentType("text/html;charset=utf-8");
			ouputStream = response.getOutputStream();
			byte[] bytes = message.getBytes("UTF-8");// 设置编码方式
			ouputStream.write(bytes, 0, bytes.length);
			ouputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ouputStream != null) {
				try {
					ouputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	}
    public static String mapToJson(Map<String,Object> _mp){
        StringBuilder rt = new StringBuilder();
        if(_mp != null && _mp.size() > 0){
            rt.append("{");
            for(Object _et:_mp.entrySet()){
                Map.Entry entry = (Map.Entry)_et;
                String _code = (String)entry.getKey();
                Object _val = entry.getValue();

                if(_val instanceof ArrayList){
                    rt.append(_code).append(":").append(_val).append(",");
                }else{
                    rt.append(_code).append(":").append("'").append(_val).append("'").append(",");
                }
            }
            rt.setCharAt(rt.length()-1,'}');
        }else{
            rt.append("{}");
        }
        return rt.toString();
    }
}

class TimestampTypeAdapter implements JsonSerializer<Timestamp> {
	private final DateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	public JsonElement serialize(Timestamp src, Type arg1,
			JsonSerializationContext arg2) {
		String dateFormatAsString = format.format(new Date(src
				.getTime()));
		return new JsonPrimitive(dateFormatAsString);
	}
}
