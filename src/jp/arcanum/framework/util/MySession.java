package jp.arcanum.framework.util;



import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;


public class MySession extends WebSession{

	/**
	 * コンストラクタ
	 * @param request リクエスト情報
	 */
	public MySession(Request request){
		super(request);
	}

	private List _searchlist = new ArrayList();
	public List getSearchList(){
		return _searchlist;
	}
	public void setSearchList(List list){
		_searchlist = list;
	}

}
