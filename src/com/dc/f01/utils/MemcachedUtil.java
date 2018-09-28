package com.dc.f01.utils;

import com.danga.MemCached.MemCachedClient;
import com.dc.f01.common.SpringContextHolder;

/**
 * 
 * @author Gary
 * 
 */
public class MemcachedUtil {

	private static MemCachedClient cachedClient;

	private MemcachedUtil() {
	}
	
	public static MemCachedClient getMemCachedClient() {
		if (cachedClient == null) {
			cachedClient = SpringContextHolder.getBean("memcachedClient");
		}
		return cachedClient;
	}

    public static boolean delete(String key) {
        return getMemCachedClient().delete(key);
    }

	public static boolean add(String key, Object value) {
		return getMemCachedClient().add(key, value);
	}

	public static boolean add(String key, Object value, Integer expire) {
		return getMemCachedClient().add(key, value, expire);
	}

	public static boolean put(String key, Object value) {
		return getMemCachedClient().set(key, value,-1);
	}

	public static boolean put(String key, Object value, Integer expire) {
		return getMemCachedClient().set(key, value, expire);
	}

	public static boolean replace(String key, Object value) {
		return getMemCachedClient().replace(key, value);
	}

	public static boolean replace(String key, Object value, Integer expire) {
		return getMemCachedClient().replace(key, value, expire);
	}

	public static Object get(String key) {
		return getMemCachedClient().get(key);
	}
	
	
}
