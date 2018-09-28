package com.dc.f01.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dc.f01.interceptors.NoticeMessageHandler;


/**
 * 消息队列
 * @author Administrator
 * @param <E>
 *
 */
public class DxQueuePool<E> {

//	 //线程池  
//    @Autowired  
//    @Qualifier("taskExecutor")  
//    private TaskExecutor taskExecutor;  
    
	//执行间隔
	private int seconds = 1;
	//长度
	private int length = 3;
	
    
    private int thread;
    //结果队列
    private ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();
    
    public boolean offer(E e){
    	try{
    		return queue.offer(e);
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}
    }
    
   
    public DxQueuePool(){
    	this(1);
    }
    
    public DxQueuePool(int thread){
    	this.thread = thread;
    	queue = new ConcurrentLinkedQueue<E>();
    }
   

	public void schedule(){
    	
    	ScheduledExecutorService  es = Executors.newScheduledThreadPool(thread);
    	es.scheduleAtFixedRate(new Runnable() {
    		@Override
    		public void run() {
    			 List<E> list = new ArrayList<E>();
    	    	 while (!queue.isEmpty()) {
    	             list.add(queue.poll());
    	    	}
    	    	//取最大个数
    	    	if(list.size() > length){
    	    		list = list.subList(0, length-1);
    	    	}
    			//捕获异常，禁止停掉
    			try{
    				if(list != null && list.size() >0){
						Map map=new HashMap();
						map.put("pushList",list);
						map.put("msgType","push");
    	    			String jsonStr = JSONObject.fromObject(map).toString();
    	    			sendMessage(new TextMessage(jsonStr));
    	    		}
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    	}, 0,seconds, TimeUnit.SECONDS);
    	
    }
	
	private void sendMessage(TextMessage message){
		Map<Long, WebSocketSession> users = NoticeMessageHandler.getOnline();
		if(users == null || users.size() <= 0){
    		return;
    	}
		Collection<WebSocketSession> collection = users.values();
    	if(collection != null && !collection.isEmpty()){
	        for (WebSocketSession user : collection) {
	        	synchronized(user) {
		            if (user.isOpen()) {
		                try {
		                    user.sendMessage(message);
		                } catch (Exception e) {
		                	
		                }
		            }
	        	}
	        }
    	}
	}
    

     
}
