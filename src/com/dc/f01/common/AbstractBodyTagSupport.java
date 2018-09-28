package com.dc.f01.common;

import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class AbstractBodyTagSupport extends BodyTagSupport{
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private static transient Log log = LogFactory.getLog(AbstractBodyTagSupport.class);
	    private String codeTable;//资源类别
	    private String lableKey;//code
	    private String name;
	    private String onClick;
		private String onBlur;
		private String style;
		private String value;//code
		private String readOnly;//只读
		private String required;
		private String classCss;
		private String onfoucs;
		private String onChange;
		private String onMouseover;
		private String onMouseout;

	   
	    public String getClassCss() {
			return classCss;
		}
		public void setClassCss(String classCss) {
			this.classCss = classCss;
		}


		protected String getElementId(){
			return "idx_"+ this.name;
		}
	    

	    public String getReadOnly() {
			return readOnly;
		}

		public void setReadOnly(String readOnly) {
			this.readOnly = readOnly;
		}

		public String getCodeTable() {
	        return codeTable;
	    }

	    public void setCodeTable(String codeTable) {
	        this.codeTable = codeTable;
	    }
	    
	    
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}


		public String getLableKey() {
			return lableKey;
		}

		public void setLableKey(String lableKey) {
			this.lableKey = lableKey;
		}

		

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getOnClick() {
			return onClick;
		}

		public void setOnClick(String onClick) {
			this.onClick = onClick;
		}

		public String getOnBlur() {
			return onBlur;
		}

		public void setOnBlur(String onBlur) {
			this.onBlur = onBlur;
		}
		
		public String getOnMouseover() {
			return onMouseover;
		}
		public void setOnMouseover(String onMouseover) {
			this.onMouseover = onMouseover;
		}
		public String getOnMouseout() {
			return onMouseout;
		}
		public void setOnMouseout(String onMouseout) {
			this.onMouseout = onMouseout;
		}
		public String getStyle() {
			return style;
		}

		public void setStyle(String style) {
			this.style = style;
		}

		
	    public void clear(){
	        setCodeTable("");
	        setId("");
	        setLableKey("");
	        setName("");
	        setStyle("");
	        setValue("");
	        setOnBlur("");
	        setOnClick("");
	      
	    }
	    
	    
		public String getRequired() {
			return required;
		}

		public void setRequired(String required) {
			this.required = required;
		}


		public String getOnfoucs() {
			return onfoucs;
		}


		public void setOnfoucs(String onfoucs) {
			this.onfoucs = onfoucs;
		}
		public String getOnChange() {
			return onChange;
		}
		public void setOnChange(String onChange) {
			this.onChange = onChange;
		}

}
