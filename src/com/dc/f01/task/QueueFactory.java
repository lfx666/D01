package com.dc.f01.task;

public class QueueFactory {

	private static DxQueuePool<String> pool;

	public static DxQueuePool<String> create() {
		if(pool == null){
			pool = new DxQueuePool<String>();
		}
		return (DxQueuePool<String>) pool;
	}
	
	

}
