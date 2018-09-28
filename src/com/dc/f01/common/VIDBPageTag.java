package com.dc.f01.common;

import java.util.Map;

import javax.servlet.jsp.JspException;

public class VIDBPageTag extends AbstractBodyTagSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VIDBPager page;
	
	private String url;
	
	
    public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public VIDBPager getPage() {
		return page;
	}
	public void setPage(VIDBPager page) {
		this.page = page;
	}

	@Override
    public int doStartTag() throws JspException {
        StringBuffer str = new StringBuffer();
        str.append("<table class='tb-foot-body' >" +
                "       <tr>" +
                "           <td style='height:1px'></td>" +
                "       </tr>" +
                "       <tr>" +
                "           <td>");
        str.append("<form id ='page_form' action=\"").append(url).append("\">");
        String first = "首页";
        String pre = "上一页";
        String next = "下一页";
        String end = "尾页";
        String  theFirst = "第";
        String pages = "页";
        String to = "跳转至";
            str.append("<div class='tb-foot-td-div' >");
                //str.append("<ul>");
                if (page.getPageIndex() == 1) {
                    //str.append("<li>").append(first).append("</li>").append("&nbsp;").append("<li>").append(pre).append("</li>").append("&nbsp;");
                    str.append("<label>").append(first).append("</label>").append("&nbsp;&nbsp;").append("<label>").append(pre).append("</label>").append("&nbsp;&nbsp;");
                } else {
                    str.append("<label><a href='javascript:void(0)' onclick='_getListPage(1)'>").append("").append(first).append("").append("</a></label>&nbsp;&nbsp;");
                    str.append("<label><a href='javascript:void(0)' onclick='_getListPage(" + (page.getPageIndex() - 1) + ")'>").append("").append(pre).append("").append("</a></label>&nbsp;&nbsp;");

                }
                if (page.getPageIndex().equals(page.getPageCount()) || page.getPageCount() < 2) {
                    str.append("<label>").append(next).append("</label>").append("&nbsp;&nbsp;").append("<label>").append(end).append("</label>").append("&nbsp;&nbsp;");
                } else {
                    str.append("<label><a href='javascript:void(0)' onclick='_getListPage(" + (page.getPageIndex() + 1) + ")'  >").append("").append(next).append("").append("</a></label>&nbsp;&nbsp;");
                    str.append("<label><a href='javascript:void(0)'  onclick='_getListPage(" +  page.getPageCount() + ")'>").append("").append(end).append("").append("</a></label>&nbsp;&nbsp;");
                }
                str.append("<label>").append(to).append(":</label>&nbsp;&nbsp;").append("").append("<select onchange='_getListPage(this.value)'  onkeyup='this.blur();this.focus();' >");
                for(int i=1;i<=page.getPageCount();i++){
                    str.append("<option value='" + i + "' " + (i == page.getPageIndex() ? "selected" : "") + ">").append(theFirst).append(i).append(pages).append("</option>");
                }
                str.append("</select>").append("");

            str.append("</div>");
        Map<String,String[]> params = page.getParamMap();
            if(params.keySet() != null || params.keySet().size() > 0){
            for (String key : params.keySet()) {
                String[] values = params.get(key);
                    for(int i = 0; i < values.length; i++) {
                        String value = values[i];
                        if(!"curPage".equals(key)){
                             str.append("<input type='hidden' name='").append(key).append("' value='").append(value).append("'/>");
                        }
                    }
            }
        }
		str.append("<input type ='hidden' id = 'curPage' name = 'curPage' value ='").append(page.getPageIndex()).append("'/>");
        str.append("</form>");
        str.append("    </td>" +
                "   </tr>" +
                "</table>");//tb-foot-body
        try {
            if (str.length()>0) {
                pageContext.getOut().write(new String(str));
            }
        } catch (Exception e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

}
