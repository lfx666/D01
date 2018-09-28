package com.dc.f01.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message implements Serializable{

	private static final long serialVersionUID = 3260855400984098764L;
	
	private Boolean status = false;
	private String  description;
	private String  resultCode = "0";

    private Map<String, Object> data = new HashMap<>();

	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
        _put("status",status);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
        _put("description",description);
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
        _put("resultCode",resultCode);
	}

    public void setSuccessMark(){
        this.status=true;
        if(description==null||description.isEmpty()){
            description = " execution success !";
        }
    }
    public Map<String, Object> getData() {
        if(!data.containsKey("status")){
            _put("status",status);
        }
        /*if(!status&&description==null){
            _put("description","System exception or status is not set successfully");
        }*/
        return data;
    }
    private void _put(String key,Object val){
        data.put(key,val);
    }
    public void addMap(String key,Map lt){
        _put(key, lt);
    }
    public void addList_Map(String key,List<? extends Map> lt){
        _put(key, lt);
    }
    public void put(String key,Object val){
        _put(key, val);
    }
    public void addPageDBGridData(List<Map> obj){
        _put("DBGridData", obj);
    }
    public void addPageTotalNum(Object val){
        _put("TotalNum",  val);
    }
    public void addList_Str(String key,List<String> lt){
        _put(key,lt);
    }
}
