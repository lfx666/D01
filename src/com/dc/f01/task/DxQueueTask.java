package com.dc.f01.task;

import java.util.List;

public abstract class DxQueueTask<E> {

	public abstract void run(List<E> list);
	
}
