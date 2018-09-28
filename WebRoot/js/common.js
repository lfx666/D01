(function($){
	var getRootPath = function (){
	    var curWwwPath=window.document.location.href;
	    var pathName=window.document.location.pathname;
	    var pos=curWwwPath.indexOf(pathName);
	    var localhostPaht=curWwwPath.substring(0,pos);
	    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	    return(localhostPaht+projectName);
	};

	function loading(){
		//获取浏览器页面可见高度和宽度
        var _PageHeight = document.documentElement.clientHeight,
            _PageWidth = document.documentElement.clientWidth;
        //计算loading框距离顶部和左部的距离（loading框的宽度为215px，高度为61px）
        var _LoadingTop = _PageHeight > 61 ? (_PageHeight - 61) / 2 : 0,
            _LoadingLeft = _PageWidth > 215 ? (_PageWidth - 215) / 2 : 0;
        //在页面未加载完毕之前显示的loading Html自定义内容
        var _LoadingHtml = '<div id="loadingDiv" style="position:absolute;left:0;width:100%;height:' + _PageHeight + 'px;top:0;background:#f3f8ff;opacity:0.8;filter:alpha(opacity=80);z-index:10000;"><div style="position: absolute; cursor1: wait; left: ' + _LoadingLeft + 'px; top:' + _LoadingTop + 'px; width: auto; height: 57px; line-height: 57px; padding-left: 50px; padding-right: 5px; background: #fff url(/static/image/loading.gif) no-repeat scroll 5px 10px; border: 2px solid #95B8E7; color: #696969; font-family:\'Microsoft YaHei\';">页面加载中，请等待...</div></div>';
       // document.write(_LoadingHtml);
	};
	
	function hideLoading(){
		var loadingMask = document.getElementById('loadingDiv');
		if(loadingMask){
			 loadingMask.parentNode.removeChild(loadingMask);
		}
	};
	$.ajaxFn = function(url,paramData,datatype,fn){
		
		$.ajax({
	        type:"POST",
	        url:url,
	        cache:false,
	        datatype: datatype,//"xml", "html", "script", "json", "jsonp", "text".
	        data:paramData,
	        contentType: "application/x-www-form-urlencoded; charset=utf-8", 
	        beforeSend:function(){
	        	//loading();
	        },
	        success:function(data,st,XMLHttpRequest){
	        	return fn(data);
	        },
	        complete: function(XMLHttpRequest,textStatus){
	        	//hideLoading();
	        	var sessionStatus = XMLHttpRequest.getResponseHeader('sessionstatus');
	        	if(sessionStatus == 'timeout'||sessionStatus == 'relogin') {
	        		/*layer.alert('您session已过期, 请重新登录.',function(){
                        top.location.href = '/S01/login/showLogin.do';
                    });*/
                    alert("您session已过期, 请重新登录");
                    top.location.href = '';
	        	}
	        },
	        error: function(data){
                console.log("error");
	        }        
	     });
	};
	
})(jQuery);
$.ajaxJsonFn = (function(url,paramData,fn){
	return $.ajaxFn(url,paramData,'json',function(data){
		if(data !='' && data != undefined){
			var res = eval('(' + data + ')');
			if(res.status == true){
				return fn(res);
			}else{
				alert(res.description);
			}
			
		}
	});
});


/** 
 * 将数值四舍五入(保留2位小数)后格式化成金额形式 
 * 
 * @param num 数值(Number或者String) 
 * @return 金额格式的字符串,如'1,234,567.45' 
 * @type String 
 */  
$.formatCurrency = (function formatCurrency(num) {  
	if(num ==null||num == "undefined") num = '0';
    var num = num.toString().replace(/\$|\,/g,'');  
    if(isNaN(num))  
        num = "0";  
    sign = (num == (num = Math.abs(num)));  
    num = Math.floor(num*100+0.50000000001);  
    cents = num%100;  
    num = Math.floor(num/100).toString();  
    if(cents<10)  
    cents = "0" + cents;  
    for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)  
    num = num.substring(0,num.length-(4*i+3))+','+  
    num.substring(num.length-(4*i+3));  
    return "￥" +(((sign)?'':'-') + num + '.' + cents);  
});  

$.format = function (source, params) {
    if (arguments.length == 1)
        return function () {
            var args = $.makeArray(arguments);
            args.unshift(source);
            return $.format.apply(this, args);
        };
    if (arguments.length > 2 && params.constructor != Array) {
        params = $.makeArray(arguments).slice(1);
    }
    if (params.constructor != Array) {
        params = [params];
    }
    $.each(params, function (i, n) {
    	if(typeof(n) == "undefined"){n='';}
        source = source.replace(new RegExp("\\{" + i + "\\}", "g"), n);
    });
    return source;
};

/**
 * This jQuery plugin displays pagination links inside the selected elements.
 *
 * @author Gabriel Birke (birke *at* d-scribe *dot* de)
 * @version 1.2
 * @param {int} maxentries Number of entries to paginate
 * @param {Object} opts Several options (see README for documentation)
 * @return {Object} jQuery Object
 */
jQuery.fn.pagination = function(maxentries, opts){
    opts = jQuery.extend({
        items_per_page:10,
        num_display_entries:10,
        current_page:0,
        num_edge_entries:0,
        link_to:"#",
        //update firstPage、lastPage
        first_text: "首页",
        last_text: "尾页",
        //update
        prev_text:"Prev",
        next_text:"Next",
        ellipse_text:"...",
        prev_show_always:true,
        next_show_always:true,
        callback:function(){return false;}
    },opts||{});
     
    return this.each(function() {
        /**
         * Calculate the maximum number of pages
         */
        function numPages() {
            return Math.ceil(maxentries/opts.items_per_page);
        }
         
        /**
         * Calculate start and end point of pagination links depending on 
         * current_page and num_display_entries.
         * @return {Array}
         */
        function getInterval()  {
            var ne_half = Math.ceil(opts.num_display_entries/2);
            var np = numPages();
            var upper_limit = np-opts.num_display_entries;
            var start = current_page>ne_half?Math.max(Math.min(current_page-ne_half, upper_limit), 0):0;
            var end = current_page>ne_half?Math.min(current_page+ne_half, np):Math.min(opts.num_display_entries, np);
            return [start,end];
        }
         
        /**
         * This is the event handling function for the pagination links. 
         * @param {int} page_id The new page number
         */
        function pageSelected(page_id, evt){
            current_page = page_id;
            drawLinks();
            var continuePropagation = opts.callback(page_id, panel);
            if (!continuePropagation) {
                if (evt.stopPropagation) {
                    evt.stopPropagation();
                }
                else {
                    evt.cancelBubble = true;
                }
            }
            return continuePropagation;
        }
         
        /**
         * This function inserts the pagination links into the container element
         */
        function drawLinks() {
            panel.empty();
            var interval = getInterval();
            var np = numPages();
            // This helper function returns a handler function that calls pageSelected with the right page_id
            var getClickHandler = function(page_id) {
                return function(evt){ return pageSelected(page_id,evt); }
            }
            // Helper function for generating a single link (or a span tag if it's the current page)
            var appendItem = function(page_id, appendopts){
                page_id = page_id<0?0:(page_id<np?page_id:np-1); // Normalize page id to sane value
                appendopts = jQuery.extend({text:page_id+1, classes:""}, appendopts||{});
                if(page_id == current_page){
                    var lnk = jQuery("<span class='current'>"+(appendopts.text)+"</span>");
                }
                else
                {
                    var lnk = jQuery("<a>"+(appendopts.text)+"</a>")
                        .bind("click", getClickHandler(page_id))
                        .attr('href', opts.link_to.replace(/__id__/,page_id));
                         
                         
                }
                if(appendopts.classes){lnk.addClass(appendopts.classes);}
                panel.append(lnk);
            }
            // update firstPage
            if (opts.first_text && (current_page > 0 || opts.prev_show_always)) {      
                appendItem(0, { text: opts.first_text, classes: "prev disabled" });           
            } 
            // update
            // Generate "Previous"-Link
            if(opts.prev_text && (current_page > 0 || opts.prev_show_always)){
                appendItem(current_page-1,{text:opts.prev_text, classes:"prev"});
            }
            // Generate starting points
            if (interval[0] > 0 && opts.num_edge_entries > 0)
            {
                var end = Math.min(opts.num_edge_entries, interval[0]);
                for(var i=0; i<end; i++) {
                    appendItem(i);
                }
                if(opts.num_edge_entries < interval[0] && opts.ellipse_text)
                {
                    jQuery("<span>"+opts.ellipse_text+"</span>").appendTo(panel);
                }
            }
            // Generate interval links
            for(var i=interval[0]; i<interval[1]; i++) {
                appendItem(i);
            }
            // Generate ending points
            if (interval[1] < np && opts.num_edge_entries > 0)
            {
                if(np-opts.num_edge_entries > interval[1]&& opts.ellipse_text)
                {
                    jQuery("<span>"+opts.ellipse_text+"</span>").appendTo(panel);
                }
                var begin = Math.max(np-opts.num_edge_entries, interval[1]);
                for(var i=begin; i<np; i++) {
                    appendItem(i);
                }
                 
            }
            // Generate "Next"-Link
            if(opts.next_text && (current_page < np-1 || opts.next_show_always)){
                appendItem(current_page+1,{text:opts.next_text, classes:"next"});
            }
            // update lastPage
            if (opts.last_text && (current_page < np - 1 || opts.next_show_always)) {            
                 appendItem(np, { text: opts.last_text, classes: "prev disabled" });          
            }
            // update
        }
         
        // Extract current_page from options
        var current_page = opts.current_page;
        // Create a sane value for maxentries and items_per_page
        maxentries = (!maxentries || maxentries < 0)?1:maxentries;
        opts.items_per_page = (!opts.items_per_page || opts.items_per_page < 0)?1:opts.items_per_page;
        // Store DOM element for easy access from all inner functions
        var panel = jQuery(this);
        // Attach control functions to the DOM element 
        this.selectPage = function(page_id){ pageSelected(page_id);}
        this.prevPage = function(){ 
            if (current_page > 0) {
                pageSelected(current_page - 1);
                return true;
            }
            else {
                return false;
            }
        }
        this.nextPage = function(){ 
            if(current_page < numPages()-1) {
                pageSelected(current_page+1);
                return true;
            }
            else {
                return false;
            }
        }
        // When all initialisation is done, draw the links
        drawLinks();
        // call callback function
        //opts.callback(current_page, this);
    });
};
jQuery.fn.loadHtml = (function(url,callback){
	$(this).load(url,callback);
});

/*
 * 把当前页放入form表单中
 */
function reCurPage(requestForm){
	var curPage = $("#curPage").val();
    var perNum = $("#perNum").val();
    $("#"+requestForm).append("<input type='hidden' name='perNum' value= '"+perNum+"'>");
	if(curPage != null && curPage !=''){
		$("#"+requestForm).append("<input type='hidden' name='curPage' value= '"+curPage+"'>");
	}
}
function pageInit(fn) {
    var totalCount = $("#hideTotalCount").val();
    $("#Pagination").pagination(parseInt(totalCount), {
      items_per_page: 10,
      //current_page: 1,//当前选中的页面默认是0，表示第1页
      num_edge_entries: 2,//两侧显示的首尾分页的条目数,默认为0，好像是尾部显示的个数
      num_display_entries: 2,//连续分页主体部分显示的分页条目数,默认是10
      link_to: "javascript:void(0)",//分页的链接
      prev_text: "上一页",
      next_text: "下一页",
      prev_show_always: true,
      next_show_always: true,
      callback: function(data){
    	  return fn(data);
      }
    });
  }

/*----------------------------taglib use start----------------------------*/
function _getListPage(page){
	   var frm = document.getElementById("page_form");
	   document.getElementById("curPage").value = page ;
	   frm.method = 'POST';
	   frm.submit();      
}
function _VIDBTabTag_init(h_f_itemSrc,h_f_itemId,fId) {
    __VIDBTabTag_setItemPage(h_f_itemSrc,h_f_itemId);
    __VIDBTabTag_setItemPageCss(h_f_itemId,fId);
    //设置超出显示上下页
    var its = document.getElementById("VIDBTabTag_ITEMS_"+fId).offsetWidth ;
    var sc = document.getElementById("VIDBTabTag_SBAR_"+fId).offsetWidth ;
    // alert(its - sc > 0);
    if(its - sc > 0){
        document.getElementById("VIDBTabTag_UD_LEFT_"+fId).style.display='block';
        document.getElementById("VIDBTabTag_UD_RIGHT_"+fId).style.display='block';
    }else{
        document.getElementById("VIDBTabTag_UD_LEFT_"+fId).style.display='none';
        document.getElementById("VIDBTabTag_UD_RIGHT_"+fId).style.display='none';
    }
    //设置内容区的高度
    var vch = document.getElementById("VIDBTabTag_CONTENT_"+fId).clientHeight;
    if(vch) {
        if (vch<=200||vch>=1500) {
            //alert(document.getElementById("VIDBTabTag_CONTENT_" + fId).style.height);
            document.getElementById("VIDBTabTag_CONTENT_" + fId).style.height = document.body.clientHeight - 50;
        }
    }
}
function _VIDBTabTag_items_scroll(scrollId,type){
    if(type=="A"){
        document.getElementById(scrollId).scrollLeft=(document.getElementById(scrollId).scrollLeft+90);
    }else{
        document.getElementById(scrollId).scrollLeft=(document.getElementById(scrollId).scrollLeft-90);
    }
}
function _VIDBTabTag_changeItemPage(itemSrc,itemId,fId) {
    if(itemSrc!=null){
        //alert(itemSrc);
        __VIDBTabTag_setItemPage(itemSrc,itemId);
        __VIDBTabTag_setItemPageCss(itemId,fId);
    }
}
function __VIDBTabTag_setItemPage(itemSrc,itemId){
    var frameId = "VIDBTabTag_PAGEFRAMEID_";
    if(itemSrc==undefined||itemSrc==null||itemSrc=="null"){
        itemSrc="javascript:void(0)";
    }
    if(itemId!=undefined&&itemId!=null&&itemId!="null"){
        //frameId+=itemId;
    }
    var fFrame = document.getElementById(frameId)||null;
    if(fFrame!=null){
        fFrame.src = '';
        fFrame.src = itemSrc;
    }
}
function __VIDBTabTag_setItemPageCss(cur_ItemId,fId){
    var parDivs = document.getElementsByTagName("div");
    if(parDivs!=undefined && parDivs != null){
        var childs = parDivs;
        if(childs!=undefined && childs.length > 0){
            var selItemId = "VIDBTabTag_ITEM_"+fId+"_"+cur_ItemId;
            for(var _ix in childs){
                // alert(childs[_ix].id);
                var curNode = childs[_ix];
                if(curNode!=undefined && curNode.id!=undefined && curNode.id.indexOf("VIDBTabTag_ITEM_"+fId) >= 0){
                    if (curNode.id.indexOf(selItemId) >= 0){
                        curNode.className = "tab-item-panel-select";
                    }else{
                        curNode.className = "tab-item-panel";
                    }
                }
            }
        }
    }
}

/*----------------------------taglib use end----------------------------*/
/*----------------------------page use i18n res start----------------------------*/
var _pageBase = "__g_i18n_pageBase_Resource__";
function _I18nMsg(code,resType,params,codeTable){
    if (arguments.length == 0) {
        return "ERROR:Please access at least one parameter!";
    }
    if (code == undefined || code == null || code == ""
        || resType == undefined || resType == null || resType == "") {
        return "ERROR:code,resType parameter values ​​must be passed correctly!";
    }
    var _key = codeTable + '_' + code;
    if(codeTable == undefined || codeTable == null || codeTable == ""){_key = code;}
    var _rtVal = _key;
    _rtVal = eval(resType + "." + _key);
    if (_rtVal == null || _rtVal == undefined) {
        _rtVal = "ERROR:The JSON-KEY:[ "+resType+"."+_key+" ] value does not match the corresponding value!";
    }
    if (params != undefined && params != null && params.length > 0) {
        for (var i = 0; i < params.length; i++) {
            _rtVal = _rtVal.replace(new RegExp("\\{" + i + "\\}", 'g'), params[i]);
        }
    }
    return g_decode_html(_rtVal);
}
function g_I18nMsg_pageBase(code,params){
    return _I18nMsg(code,_pageBase,params,null);
}
/*----------------------------page use i18n res end----------------------------*/
/**
 * 判断当前页面是否在主页面中
 * @returns {boolean}
 */
function g_isMainWorkFlag(){
    var _tm = window.top||window.parent||undefined;
    var _tm_doc = undefined||_tm.document;
    if(_tm_doc&&_tm_doc.getElementById&&
        _tm_doc.getElementById("__MainWorkFlag")){
        //alert(document.getElementById("__MainWorkFlag"));
        return true;
    }
    return false;
}
function _setPagePer(i){
    var totalCount = parseInt(document.getElementById("totalCount").value)<1?10:parseInt(document.getElementById("totalCount").value);
    if(totalCount < i){
        i = totalCount;
        document.getElementById("curPage").value = 1 ;
    }
    var frm = document.getElementById("page_form");
    document.getElementById("perNum").value = i ;
    frm.method = 'POST';
    frm.submit();
}
/**
 * comm to TipMsgPage
 * @argument flag
 * @argument msg
 */
function g_GoToTipMsgPage(flag,msg) {
    window.location.href =_gModuleName+"/comm/commGoToSkipMsg.do?Flag="+flag+"&Msg="+msg;
}
function g_IsDigit(s){
    if(s==null) return false;
    if(s=='')return true;
    s=''+s;
    if(s.substring(0,1)=='-' && s.length>1)s=s.substring(1,s.length);
    var patrn=/^[0-9]*$/;
    if (!patrn.exec(s)) return false;
    return true;
}

function g_decode_html(str){
    if(str == undefined || str == null){
        return str;
    }
    var s = str.replace("&amp;", "&");
    s = s.replace("&lt;", "<");
    s = s.replace("&gt;", ">");
    s = s.replace("&apos;", "\'");
    s = s.replace("&quot;", "\"");
    return s;
}
/*团队信息*/
var openStr={};
function showTeam(merchantId,merchantName){
    $.ajaxJsonFn("ajax/merchant/showMerchantTeamInfo.do?logType=4&logDisc=查询团队信息&logFunc=商户管理",{merchantId:merchantId},function(data) {
        $(this).load("template/showMerchantTeamInfo.xml",function(dataXml){
            openStr={};
            var str = "";
            var list = data.merchantList;
            if(list){
                for(var i = 0;i<list.length;i++){
                    if(list[i].parentId=='0'){
                        var xiaji=false;//是否存在下级
                        for(var j = 0;j<list.length;j++){
                            if(list[j].parentId==list[i].merchantId)xiaji=true;
                        }
                        var siteName=list[i].siteName!=null?list[i].siteName:"";
                        var webSite=list[i].webSite!=null?list[i].webSite:"";
                        var icp=list[i].icp!=null?list[i].icp:"";
                        var contact=list[i].contact!=null?list[i].contact:"";
                        var tel=list[i].tel!=null?list[i].tel:"";
                        var mobile=list[i].mobile!=null?list[i].mobile:"";
                        var qq=list[i].qq!=null?list[i].qq:"";
                        var skype=list[i].skype!=null?list[i].skype:"";
                        var email=list[i].email!=null?list[i].email:"";
                        if(xiaji){
                            str += "<tr name='myself' id='"+list[i].merchantId+"'><td><label id='l"+list[i].merchantId+"' class='team_arrow_right'></label>"+list[i].merchantId+"</td><td>"+list[i].merchantName+"</td><td>"+$.formatCurrency(list[i].balance)+"</td><td>"+list[i].commission+"%</td><td>"+list[i].merType+"</td><td>"+list[i].agentType+"</td><td>"+list[i].paramPriv+"</td><td>"+email+"</td><td>"+siteName+"</td><td>"+webSite+"</td><td>"+icp+"</td><td>"+contact+"</td><td>"+tel+"</td><td>"+mobile+"</td><td>"+qq+"</td><td>"+skype+"</td></tr>";
                        }else{
                            str += "<tr name='myself' id='"+list[i].merchantId+"'><td><label id='l"+list[i].merchantId+"' class='team_arrow_null'></label>"+list[i].merchantId+"</td><td>"+list[i].merchantName+"</td><td>"+$.formatCurrency(list[i].balance)+"</td><td>"+list[i].commission+"%</td><td>"+list[i].merType+"</td><td>"+list[i].agentType+"</td><td>"+list[i].paramPriv+"</td><td>"+email+"</td><td>"+siteName+"</td><td>"+webSite+"</td><td>"+icp+"</td><td>"+contact+"</td><td>"+tel+"</td><td>"+mobile+"</td><td>"+qq+"</td><td>"+skype+"</td></tr>";
                        }
                    }
                }
            }
            if(str=="")str=str += "<tr ><td>"+merchantName+"</td><td>0.00</td><td>0.00</td></tr>";
            dataXml = $.format(dataXml,str);
            layer.open({
                type: 1,
                title:"商户信息",
                offset: '20%',
                move: false,
                area: ['80%', '60%'], //宽高
                content: dataXml
            });
            $("tr[name='myself']").on("click",function(){
                var classname=this.getElementsByTagName("td")[0].getElementsByTagName("label")[0].className;
                if(classname=='team_arrow_right'){
                    openStr[1]=this.id;
                    OpenTeam(data,this,1);
                }else if(classname=='team_arrow_down'){
                    this.getElementsByTagName("td")[0].getElementsByTagName("label")[0].className="team_arrow_right";
                    for(var key in openStr){
                        if(key>=1){
                            $("tr[name='"+openStr[key]+"']").remove();
                            delete(openStr[key]);
                        }
                    }
                }
            });
        });
    });
}
/*展开团队列表*/
function OpenTeam(data,Custhis,index){
    index++;
    var str = "";
    var merchant_id=Custhis.id;
    var list = data.merchantList;
    Custhis.getElementsByTagName("td")[0].getElementsByTagName("label")[0].className="team_arrow_down";
    if(list){
        for(var i = 0;i<list.length;i++){
            if(list[i].parentId==merchant_id){
                var xiaji=false;
                for(var j = 0;j<list.length;j++){
                    if(list[j].parentId==list[i].merchantId)xiaji=true;
                }
                var siteName=list[i].siteName!=null?list[i].siteName:"";
                var webSite=list[i].webSite!=null?list[i].webSite:"";
                var icp=list[i].icp!=null?list[i].icp:"";
                var contact=list[i].contact!=null?list[i].contact:"";
                var tel=list[i].tel!=null?list[i].tel:"";
                var mobile=list[i].mobile!=null?list[i].mobile:"";
                var qq=list[i].qq!=null?list[i].qq:"";
                var skype=list[i].skype!=null?list[i].skype:"";
                var email=list[i].email!=null?list[i].email:"";
                if(xiaji){
                    str += "<tr name='"+merchant_id+"' id='"+list[i].merchantId+"' position='"+index+"'><td><label  style='margin-left: "+index*20+"px' class='team_arrow_right'></label>"+list[i].merchantId+"</td><td>"+list[i].merchantName+"</td><td>"+$.formatCurrency(list[i].balance)+"</td><td>"+(list[i].commission)+'%'+"</td><td>"+list[i].merType+"</td><td>"+list[i].agentType+"</td><td>"+list[i].paramPriv+"</td><td>"+email+"</td><td>"+siteName+"</td><td>"+webSite+"</td><td>"+icp+"</td><td>"+contact+"</td><td>"+tel+"</td><td>"+mobile+"</td><td>"+qq+"</td><td>"+skype+"</td></tr>";
                }else{
                    str += "<tr name='"+merchant_id+"' id='"+list[i].merchantId+"' position='"+index+"'><td><label  style='margin-left: "+index*25+"px' class='team_arrow_null'></label>"+list[i].merchantId+"</td><td>"+list[i].merchantName+"</td><td>"+$.formatCurrency(list[i].balance)+"</td><td>"+(list[i].commission)+'%'+"</td><td>"+list[i].merType+"</td><td>"+list[i].agentType+"</td><td>"+list[i].paramPriv+"</td><td>"+email+"</td><td>"+siteName+"</td><td>"+webSite+"</td><td>"+icp+"</td><td>"+contact+"</td><td>"+tel+"</td><td>"+mobile+"</td><td>"+qq+"</td><td>"+skype+"</td></tr>";
                }
            }
        }
        $("#"+merchant_id).after(str);
        $("tr[name='"+merchant_id+"']").on("click",function(){
            var classname=this.getElementsByTagName("td")[0].getElementsByTagName("label")[0].className;
            var p=this.getAttribute("position");
            if(classname=='team_arrow_right'){
                if(openStr[p]){
                    for(var key in openStr){
                        if(key>=p){
                            if(document.getElementById(openStr[key]))document.getElementById(openStr[key]).getElementsByTagName("td")[0].getElementsByTagName("label")[0].className="team_arrow_right";
                            $("tr[name='"+openStr[key]+"']").remove();
                            delete(openStr[key]);
                        }
                    }
                    openStr[p]=this.id;
                }else{
                    openStr[p]=this.id;
                }
                OpenTeam(data,this,p);
            }else if(classname=='team_arrow_down'){
                this.getElementsByTagName("td")[0].getElementsByTagName("label")[0].className="team_arrow_right";
                for(var key in openStr){
                    if(key>=p){
                        $("tr[name='"+openStr[key]+"']").remove();
                        delete(openStr[key]);
                    }
                }
            }
        });
    }
}
//只能输入数字，含小数点,保留两位小树
twoSmallNum = function (id, fixed) {
    var str = "";
    var re = /[\D]/;
    var mark = null;
    var value = document.getElementById(id).value.replace(/^(\-)*(\d+)\.(\d\d).*$/,'$1$2.$3');
    if (re.test(value.substr(0, 1))) {
        value = "";
    }

    for (var i = 0; i < value.length; i++) {
        var isStr = value.substr(i, 1);
        if (!re.test(value)) {
            str = value;
            break;
        } else if (!re.test(isStr)) {
            str = str + isStr;
        } else if (mark == null && isStr == ".") {
            mark = true;
            str = str + isStr;
        } else {
            value = str;
            break;
        }
    }
    for (var i = 0; i < value.length - 1; i++) {
        var isZ = value.substr(i, 1);
        var isZZ = value.substr(i + 1, 1);
        if (isZ == 0 && isZZ != ".") {
            str = "";
            str = value.substr(i + 1, value.length - 1);
        } else {
            break;
        }
    }
    if (str != "" && fixed && !re.test(fixed) && fixed > 0) {
        var num = null;
        if (str.indexOf(".") != -1) {
            str = str + "0";
            num = parseFloat(str);
        } else {
            num = parseFloat(str);
        }
        document.getElementById(id).value = num;
    } else {
        document.getElementById(id).value = str;
    }
    if (9999999999.999<parseFloat(value)) {
        document.getElementById(id).value=value/10;
    }
    if (99999999999.999<parseFloat(value)) {
        document.getElementById(id).value=(value/1000).toFixed(2);
    }
    if(9999999999999 ==parseFloat(value)){
        document.getElementById(id).value= document.getElementById(id).value-0.01;
    }

};
//只能输入数字
onlyNum = function(id){
    document.getElementById(id).value=document.getElementById(id).value.replace(/\D/gi,"");
};