package com.dc.f01.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpUtils {

	private static Log log = LogFactory.getLog(HttpUtils.class);

    private HttpUtils(){}
    public static final String CHARSET_UTF8 = "UTF8";

    public static final String CHARSET_GBK = "GBK";

    public static final String USER_INFO = "USER_INFO";

    public static ServletRequest getCurServletRequest(){
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }
    private static HttpSession getCurHttpSession(){
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession(false);
    }
    private static HttpSession getCurHttpSession(boolean b){
        //如果有session返回,没有返回null
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession(b);
    }
  
    
 
   
    public static HttpSession getCurHSession() throws Exception {
        return getCurHttpSession();
    }
    public static HttpSession getCurHSession(boolean b) throws Exception {
        return getCurHttpSession(b);
    }
    /**
     * 从session中取类型为string的值
     * @param paraName
     * @return
     * @throws Exception
     */
    public static String getStrBySession(String paraName) throws Exception {
        Object obj = getObject(getCurHttpSession(),paraName,CHARSET_UTF8);
        if(obj != null
                && (obj instanceof java.lang.String)){
            return obj.toString();
        }
        return null;
    }
    public static void setToSession(String paraName,Object _val){
        HttpSession curSession = getCurHttpSession();
        if(curSession != null){
            curSession.setAttribute(paraName,_val);
        }
    }
    public static void setToSession(HttpSession _rs, String paraName,Object _val){
        if(_rs!=null){
            _rs.setAttribute(paraName,_val);
        }else{
            setToSession(paraName,_val);
        }
    }
    public static void setToRequest(String paraName,Object _val){
        ServletRequest curObj = getCurServletRequest();
        if(curObj != null){
            curObj.setAttribute(paraName,_val);
        }
    }
    public static void setToRequest(HttpServletRequest _rq, String paraName,Object _val){
        if(_rq == null){
            _rq =  (HttpServletRequest)getCurServletRequest();
        }
        if(_rq.isRequestedSessionIdValid()){
            _rq.setAttribute(paraName,_val);
        }
    }
    public static void removeAttribute(HttpServletRequest _rq, String paraName){
        if(_rq != null&&_rq.isRequestedSessionIdValid()){
            _rq.removeAttribute(paraName);
        }
    }
    public static void removeAttribute(String paraName){
            getCurServletRequest().removeAttribute(paraName);
    }
    public static void removeAttrBySession(String paraName){
        HttpSession curSession = getCurHttpSession();
        if(curSession != null){
            curSession.removeAttribute(paraName);
        }
    }
    public static Object getObject(HttpSession session, String paraName){
        return getObject(session,paraName,CHARSET_UTF8);
    }
    private static Object getObject(HttpSession session, String paraName, String pCharset) {
        Object result = null;
        try{
            if(session != null){
                result = session.getAttribute(paraName);
                if (result != null) {
                    result = new String(((String) result).getBytes("ISO-8859-1"), pCharset);
                }
                if(result == null || result.toString().equals("")){
                    result = null;
                }
            }
        }catch (Exception e){
            log.error(e);
            result = null;
        }

        return result;
    }
    public static Object getObject(ServletRequest request, String paraName) {
        return getObject(request,paraName,CHARSET_UTF8);
    }
    private static Object getObject(ServletRequest request, String paraName, String pCharset) {
        Object result = null;
        try{
            result = request.getAttribute(paraName);
            if (result == null) {
                result = request.getParameter(paraName);
                if (result != null && pCharset != null) {
                    result = new String(((String) result).getBytes("ISO-8859-1"), pCharset);
                }
            }
            if(result == null || result.toString().equals("")){
                result = null;
            }
        }catch (Exception e){
            log.error(e);
            result = null;
        }

        return result;
    }
    public static Object getAsObject(String paraName, String pCharset) {
        return getObject(getCurServletRequest(), paraName,pCharset);
    }
   
    public static String getQueryString(){
    	ServletRequest request = getCurServletRequest();
    	if(request != null){
    		 Map<String, String[]> params = getCurServletRequest().getParameterMap(); 
    		 StringBuffer queryString = new StringBuffer();  
    		 if(params.keySet() == null || params.keySet().size() <= 0){return null;}
             for (String key : params.keySet()) {  
                 String[] values = params.get(key);  
                 for (int i = 0; i < values.length; i++) {  
                     String value = values[i];  
                     queryString.append(key).append("=").append(value).append("&");
                 }  
             }
             return queryString.deleteCharAt(queryString.length()-1).toString();
    	}
    	return null;
    }
    
    public static Map<String,String[]> getRequestParamMap(){
    	ServletRequest request = getCurServletRequest();
    	if(request != null){
    		 Map<String, String[]> params = request.getParameterMap();
    		return params;
    	}
    	return null;
    }

    /**
     * 同一个key有多个参数取第一个
     * @return
     */
    public static Map<String,Object> getRequestFirstParamMap(){
        ServletRequest request = getCurServletRequest();
        if(request != null){
            Map params = request.getParameterMap();
            Map rt = null;
            if(params != null && params.size() > 0){
                rt = new HashMap();
                for (Object _obj : params.entrySet() ){
                    Map.Entry entry = (Map.Entry)_obj;
                    String _key = (String)entry.getKey();
                    String[] _val = (String[])entry.getValue();
                    if(_val != null && _val.length > 0){
                        rt.put(_key,_val[0]);
                    }
                }
            }
            return rt;
        }
        return null;
    }

    
    public static String getContextPath(){
        return ((HttpServletRequest)getCurServletRequest()).getContextPath();
    }
    public static String getContextPath(PageContext pageContext){
        return ((HttpServletRequest)pageContext.getRequest()).getContextPath();
    }
    public static String getContextPath(ServletRequest servletRequest){
        return ((HttpServletRequest)servletRequest).getContextPath();
    }
    public static String getContextPath(HttpServletRequest servletRequest){
        return servletRequest.getContextPath();
    }
    public static void setResponse(HttpServletResponse response,String charEncode,String contentType) {
        response.setCharacterEncoding(charEncode);//通知response以UTF-8发送
        response.setContentType(contentType);//设置浏览器以UTF-8打开
        response.setHeader("Content-type", contentType);
    }
    public static void setResponse_UTF8_HTML(HttpServletResponse response) {
        setResponse(response,CHARSET_UTF8,"text/html;charset=UTF-8");
    }
    public static void setResponse_GBK_HTML(HttpServletResponse response) {
        setResponse(response,CHARSET_GBK,"text/html;charset=GBK");
    }
  
    public static void showInfo(HttpServletResponse response, String message) {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void showInfoByUTF8(HttpServletResponse response, String message) {
        setResponse_UTF8_HTML(response);
        showInfo(response,message);
    }
 
    public static String getRequestIp(){
        return getRequestIp((HttpServletRequest) getCurServletRequest());
    }
    public static String getRequestIp(HttpServletRequest _rq){
    	String ip = _rq.getHeader("X-Forwarded-For");
	    //log.info("客户端请求时IP>> X-Forwarded-For："+ip);
	    
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
	    	ip = _rq.getHeader("X-Real-IP");
	    	//log.info("客户端请求时IP>> X-Real-IP："+_rq.getHeader("X-Real-IP"));
	    } 
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
	    	ip = _rq.getHeader("Proxy-Client-IP");
	    	//log.info("客户端请求时IP>> Proxy-Client-IP："+_rq.getHeader("Proxy-Client-IP"));
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
	        ip = _rq.getRemoteAddr();
	        //log.info("客户端请求时IP>> request.getRemoteAddr："+_rq.getRemoteAddr());
	    }    
	    return ip;
    }
	/**
	 * 
	 * @param url
	 * @param timeOut
	 * @return
	 */
	public static String get(String url, int timeOut) {
		return get(url, null, null, timeOut, "utf-8");
	}

	/**
	 * 
	 * @param url
	 * @param requestParameters
	 * @param timeOut
	 * @return
	 */
	public static String get(String url, Map<String, String> requestParameters,
			int timeOut, String charset) {
		List<NameValuePair> params = null;
		if (requestParameters != null) {
			params = new ArrayList<NameValuePair>();
			Iterator<Map.Entry<String, String>> it = requestParameters
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				params.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		return get(url, params, null, timeOut, charset);
	}

	public static String get(String[] credentials, String url, int timeOut) {
		String result = null;
		String user = credentials[0];
		String pwd = credentials[1];
		try {
			CredentialsProvider provider = new BasicCredentialsProvider();
			UsernamePasswordCredentials credential = new UsernamePasswordCredentials(
					user, pwd);
			provider.setCredentials(AuthScope.ANY, credential);
			HttpClient client = HttpClientBuilder.create()
					.setDefaultCredentialsProvider(provider).build();
			HttpResponse responce = client.execute(new HttpGet(url));
			// get http status code
			int resStatu = responce.getStatusLine().getStatusCode();
			if (resStatu == HttpStatus.SC_OK) {
				// get result data
				HttpEntity entity = responce.getEntity();
				result = EntityUtils.toString(entity);
			} else {
				log.error(url + ": resStatu is " + resStatu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param url
	 * @param timeOut
	 * @return
	 */
	public static String get(String url, Map<String, String> headers,int timeOut) {
		return get(url, null, headers, timeOut, "utf-8");
	}
	
	/**
	 * 发送 get请求
	 */
	public static String get(String url, List<NameValuePair> params,
			Map<String, String> headers, int timeOut, String charset) {
		String reStr = null;
		HttpClient httpClient = HttpClients.createDefault();
		URIBuilder uri = new URIBuilder();
		uri.setPath(url);
		if (params != null)
			uri.addParameters(params);
		try {
			HttpGet httpget = new HttpGet(uri.build());
			 //设置头部
	        if(null!=headers)httpget.setHeaders(assemblyHeader(headers));
			
			// set Timeout
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(timeOut)
					.setConnectTimeout(timeOut).setSocketTimeout(timeOut)
					.build();
			httpget.setConfig(requestConfig);
			// get responce
			HttpResponse responce = httpClient.execute(httpget);
			// get http status code
			int resStatu = responce.getStatusLine().getStatusCode();
			if (resStatu == HttpStatus.SC_OK) {
				// get result data
				HttpEntity entity = responce.getEntity();
				if (entity!= null && entity.isStreaming()) {
					InputStream instream = entity.getContent();
					String line = null;
					BufferedReader reader = new BufferedReader(new InputStreamReader(instream,charset));
					StringBuffer sb = new StringBuffer();
					try {
						while ((line = reader.readLine()) != null) {
							sb.append(line + "\n");
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						EntityUtils.consume(entity);
						try {
							instream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					reStr = sb.toString();
				} else {
					log.error(uri.toString() + ": resStatu is " + resStatu);
				}
			}
		}

		catch (ConnectionPoolTimeoutException e) {
			log.error(
					"http get throw ConnectionPoolTimeoutException(wait time out)",
					e);
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
			log.error("http get throw ConnectTimeoutException", e);
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			log.error("http get throw SocketTimeoutException", e);
			e.printStackTrace();
		} catch (Exception e) {
			log.error("http get throw Exception", e);
			e.printStackTrace();
		}
		return reStr;
	}

	public static String post(String url) {
		return post(null, null,null, null, url, "utf-8", 60 * 1000);
	}

	public static String post(String url, int timeOut) {
		return post(null,null, null, null, url, "utf-8", timeOut);
	}

	public static String post(Map<String, String> headers, String url) {
		return post(null, null,null, headers, url, "utf-8", 60 * 1000);
	}

	public static String post(Map<String, String> headers, String url,
			int timeOut) {
		return post(null, null,null, headers, url, "utf-8", timeOut);
	}
	
	public static String post(Map<String, String> headers,Map<String, String> params, String url,
			int timeOut) {
		return post(null, null,headers,params,url,"utf-8", timeOut);
	}

	public static String post(String[] credentials, String url, int timeOut) {
		return post(credentials, url, null, null, timeOut);
	}

	/**
	 * 
	 * @param credentials
	 * @param url
	 * @param headers
	 * @param requestParameters
	 * @param
	 * @return
	 */
	public static String post(String[] credentials, String url,
			Map<String, String> headers, Map<String, String> requestParameters,
			int timeOut) {
		String user = credentials[0];
		String pwd = credentials[1];
		return post(user, pwd,null, null, url, "utf-8", timeOut);
	}

	 //这是组装头部
    public static Header[] assemblyHeader(Map<String,String> headers){
        Header[] allHeader= new BasicHeader[headers.size()];
        int i  = 0;
        for (String str :headers.keySet()) {
            allHeader[i] = new BasicHeader(str,headers.get(str));
            i++;
        }
        return allHeader;
    }
    
	/**
	 * post method
	 * 
	 * @param headers
	 * @param url
	 * @param unicode
	 * @param timeOut
	 * @return
	 */
	public static String post(String user, String pwd,Map<String, String> headers,
			Map<String, String> parameters, String url, String unicode, int timeOut) {
		String result = null;
		// HttpPost连接对象
		HttpPost httpRequset = new HttpPost(url);
		 //设置头部
        if(null!=headers)httpRequset.setHeaders(assemblyHeader(headers));
		// set Timeout
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(timeOut)
				.setConnectTimeout(timeOut).setSocketTimeout(timeOut)
				// .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
				// .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM,
				// AuthSchemes.DIGEST))
				.build();
		httpRequset.setConfig(requestConfig);
		// 使用NameValuePair来保存要传递的Post参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (parameters != null && !parameters.isEmpty()) {
			Iterator<Map.Entry<String, String>> it = parameters.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				params.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}

		try {
			// 设置字符集
			HttpEntity httpentity = new UrlEncodedFormEntity(params, unicode);
			// 请求httpRequset
			httpRequset.setEntity(httpentity);
			// 取得HttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			if (user != null && pwd != null) {
				AuthScope authScope = new AuthScope(AuthScope.ANY_HOST,
						AuthScope.ANY_PORT, AuthScope.ANY_REALM);
				UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
						user, pwd);
				CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(authScope, creds);
				httpClient.setCredentialsProvider(credentialsProvider);
				localContext.setAttribute(ClientContext.CREDS_PROVIDER,
						credentialsProvider);
			}
			// 取得HttpResponse
			HttpResponse httpResponse = httpClient.execute(httpRequset,
					localContext);
			int resStatu = httpResponse.getStatusLine().getStatusCode();
			if (resStatu == HttpStatus.SC_OK) {
				// get result data
				// HttpEntity entity = httpResponse.getEntity();
				// result = EntityUtils.toString(entity);
				HttpEntity entity = httpResponse.getEntity();
				InputStream instream = entity.getContent();
				String line = null;
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(instream));
				StringBuffer sb = new StringBuffer();
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					EntityUtils.consume(entity);
					try {
						instream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				result = sb.toString();
			} else {
				log.error(url + ": resStatu is " + resStatu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String urlEncode(String url,String encode){ 
		String str = null;
		try {
			str = URLEncoder.encode(url, encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static void showSuccessMsg(HttpServletResponse response,String msg,String redirectPath){
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"mainpage\">");
		sb.append("<div class=\"topbox\">");
		sb.append("<ul class=\"toptitle\"><li class=\"iconqx topicon\">首页</li><li class=\"left5px\">&gt;</li><li class=\"left5px\">操作提示</li></ul>");
		sb.append("</div>");
		sb.append("<div class=\"commonbg\">");
		sb.append("<span class=\"notickicon\">操作提示</span>");
		sb.append("<ul class=\"tipbox\"><li class=\"tipsud\"></li><li>"+msg+",<span id='_time'>3</span>秒后自动跳转!</li></ul>");
		sb.append("</div>");
		sb.append("</div>");
		sb.append("<script type=\"text/javascript\" language=\"javascript\">");
		sb.append("window.setInterval(function () {");
		sb.append("var time = $(\"#_time\").text();");
		sb.append("time = parseInt(time);");
		sb.append("time = time - 1;");
		sb.append("if (time >0) {");
		sb.append("$(\"#_time\").text(time);");
		sb.append("} else {");
		sb.append("window.location = '"+ redirectPath +"';"); 
		sb.append("}");
		sb.append("},2000);");
		sb.append("</script> ");
		showInfoByUTF8(response,sb.toString());
	}
	
	public static void showErrorMsg(HttpServletResponse response,String msg,String redirectPath){
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"mainpage\">");
		sb.append("<div class=\"topbox\">");
		sb.append("<ul class=\"toptitle\"><li class=\"iconqx topicon\">首页</li><li class=\"left5px\">&gt;</li><li class=\"left5px\">操作提示</li></ul>");
		sb.append("</div>");
		sb.append("<div class=\"commonbg\">");
		sb.append("<span class=\"notickicon\">操作提示</span>");
		sb.append("<ul class=\"tipbox\"><li class=\"tipnonsud\"></li><li>"+msg+",<span id= '_time'>3</span>秒后自动跳转!</li></ul>");
		sb.append("</div>");
		sb.append("</div>");
		sb.append("<script type=\"text/javascript\" language=\"javascript\">");
		sb.append("window.setInterval(function () {");
		sb.append("var time = $(\"#_time\").text();");
		sb.append("time = parseInt(time);");
		sb.append("time = time - 1;");
		sb.append("if (time >0) {");
		sb.append("$(\"#_time\").text(time);");
		sb.append("} else {");
		sb.append("window.location = '"+ redirectPath +"';"); 
		sb.append("}");
		sb.append("},2000);");
		sb.append("</script> ");
		showInfoByUTF8(response,sb.toString());
	}
	
	
	public static void errorAlert(HttpServletResponse response,String msg,String redirectPath){
		showInfoByUTF8(response,"<script>alert('"+ msg +"'); window.location.href='"+redirectPath+"'</script>");
	}
	
	public static void errorAlert(HttpServletResponse response,String msg){
		showInfoByUTF8(response,"<script>alert('"+ msg +"');");
	}
	
	public static void main(String[] args) throws
            IOException {

		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				"admin", "NbS..Ticc25+");
		provider.setCredentials(AuthScope.ANY, credentials);
		HttpClient client = HttpClientBuilder.create()
				.setDefaultCredentialsProvider(provider).build();

		HttpResponse response = client.execute(new HttpGet(
				"http://vpn.23zv.com/review.php?lotteryId=1"));
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println(EntityUtils.toString(response.getEntity()));
	}
    /**
     * xml转换成JavaBean
     * @param xml
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T converyToJavaBean(String xml, Class<T> c) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
    /**
     * xml转换成JavaBean
     * @param URL
     * @return
     */
    public static String postWsClient(String URL){
        String result = null;
        try {
            java.net.URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            String input = "";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            InputStream inStream = conn.getInputStream();
            byte[] data = StreamTool.readInputStream(inStream);
            result = new String(data, "UTF-8");
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getAsInt(String paraName) {
        return DataUtil.getAsInt(DataUtil.transfer(getObject(getCurServletRequest(), paraName), "Integer"));
    }

    // 获取token
    public static String getToken(String apiurl, String appid, String secret)
    {
        String turl = String.format(
                "%s?grant_type=client_credential&appid=%s&secret=%s", apiurl,
                appid, secret);
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(turl);
        JsonParser jsonparer = new JsonParser();// 初始化解析json格式的对象
        String result = null;
        try
        {
            HttpResponse res = client.execute(get);
            String responseContent = null; // 响应内容
            HttpEntity entity = res.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            JsonObject json = jsonparer.parse(responseContent)
                    .getAsJsonObject();
            // 将json字符串转换为json对象
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                if (json.get("errcode") != null)
                {// 错误时微信会返回错误码等信息，{"errcode":40013,"errmsg":"invalid appid"}
                }
                else
                {// 正常情况下{"access_token":"ACCESS_TOKEN","expires_in":7200}
                    result = json.get("access_token").getAsString();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // 关闭连接 ,释放资源
            client.getConnectionManager().shutdown();
            return result;
        }
    }
    // 获取Ticket
    public static String getTicket(String apiurl, String token)
    {
        String turl = String.format(
                "%s?access_token=%s&type=jsapi", apiurl,
                token);
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(turl);
        JsonParser jsonparer = new JsonParser();// 初始化解析json格式的对象
        String result = null;
        try
        {
            HttpResponse res = client.execute(get);
            String responseContent = null; // 响应内容
            HttpEntity entity = res.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            JsonObject json = jsonparer.parse(responseContent)
                    .getAsJsonObject();
            // 将json字符串转换为json对象
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                result = json.get("ticket").getAsString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // 关闭连接 ,释放资源
            client.getConnectionManager().shutdown();
            return result;
        }
    }
}
