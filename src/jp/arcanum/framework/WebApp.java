package jp.arcanum.framework;

import java.lang.reflect.Constructor;
import java.util.StringTokenizer;

import jp.arcanum.framework.com.ArUtil;
import jp.arcanum.framework.dao.AbstractSqlExecutable;
import jp.arcanum.framework.dao.DBAccess;
import jp.arcanum.framework.page.AbstractPage;
import jp.arcanum.framework.page.internal.InternalPage;
import jp.arcanum.framework.page.sessout.SessionOutPage;
import jp.arcanum.framework.util.MySession;

import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.slf4j.LoggerFactory;

public class WebApp extends WebApplication{

	boolean _init;

	@Override
	protected void init() {

		if(!_init){
			super.init();
			_init = true;
		}

		getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");


		// 各種共通的なフラグ
		String sqlout = ArUtil.getProperty("app.sql.out");
		if(!sqlout.equals("")){
			AbstractSqlExecutable.IS_SQL_OUT = true;
		}
		else{
			AbstractSqlExecutable.IS_SQL_OUT = false;
		}

		// niceURLの設定
		String niceurls = ArUtil.getProperty("page.list");
		StringTokenizer tokens = new StringTokenizer(niceurls, ",");
		while(tokens.hasMoreTokens()){
			String token = tokens.nextToken().trim();

		// プロパティファイル上にページリストはあっても、実際のページへの設定が無い
		// 場合もありうる。その場合は設定しない
		String clazzstr = ArUtil.getProperty("page." + token + ".class");
		if(!clazzstr.equals("")){
			Class  clazz = ArUtil.loadClass(clazzstr);
			if(clazz!=null){
				if(token.equals(".")){
					LoggerFactory.getLogger(getClass()).debug("niceURL: / -->" + clazzstr);
					mountPage("/" , clazz);
				}
				else{
					LoggerFactory.getLogger(getClass()).debug("niceURL: /" + token + " -->" + clazzstr);
					mountPage("/" + token, clazz);

				}
			}
			else{
				LoggerFactory.getLogger(getClass()).debug("niceURL: /" + token + " --> page." + token + ".class がClass化できなかった");
			}
		}
		else{
			LoggerFactory.getLogger(getClass()).debug("niceURL: /" + token + " --> page." + token + ".class の設定なし");
			}

		}

        // ４０４とかの設定
        getApplicationSettings().setInternalErrorPage(InternalPage.class);
        getApplicationSettings().setPageExpiredErrorPage(SessionOutPage.class);


        // DBコネクションのリクエスト紐付け
        getRequestCycleListeners().add(DBAccess.getInstance());


        // InitInterfaceが設定されていたら、実行する
		try {
			String initinterfacestr = ArUtil.getProperty("app.init.class");
			if(!initinterfacestr.equals("")){

				Class initinterfaceclass = ArUtil.loadClass(initinterfacestr);
				if(initinterfaceclass==null){
					LoggerFactory.getLogger(getClass()).debug("app.init.class Class化できない");
				}
				else{
					InitInterface initinterface = (InitInterface) initinterfaceclass.newInstance();
					initinterface.init();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}


		// 画面右上のログイン情報パネル
		String logininfostr = ArUtil.getProperty("app.logininfo.class");
		if(!logininfostr.equals("")){
			AbstractPage.LOGININFO_PANEL = ArUtil.loadClass(logininfostr);
		}


	}

	@Override
	public RuntimeConfigurationType getConfigurationType() {


		if(ArUtil.isEmptyProperty()){
			 String proppath = getServletContext().getRealPath("WEB-INF/classes/app.properties");
			 ArUtil.readAppProperties(proppath);
		}

		String mode =ArUtil.getProperty("app.mode");
		if(mode == null || mode.equals("DEVELOPMENT")){
			return RuntimeConfigurationType.DEVELOPMENT;
		}

		return RuntimeConfigurationType.DEPLOYMENT;
	}

	@Override
	public Class<? extends Page> getHomePage() {

		String startpage = ArUtil.getProperty("page.start");
		String clazz = ArUtil.getProperty("page." + startpage + ".class");
		return ArUtil.loadClass(clazz);

	}



	/**
	 * セッションの生成（ここでnewされたセッションが、各リクエストと紐付けられる。）
	 */
	public Session newSession(Request request, Response response) {

		MySession session = null;
		try{
			String clazzstr = ArUtil.getProperty("app.session.class");
			if(clazzstr.equals("")){
				return super.newSession(request, response);
			}
			Class clazz = ArUtil.loadClass(clazzstr);
			if(clazz==null){
				return super.newSession(request, response);
			}

			Constructor[] cons = clazz.getConstructors();
			session = (MySession) cons[0].newInstance(request);
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}

		return session;

	}


}
