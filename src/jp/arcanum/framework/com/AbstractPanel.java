package jp.arcanum.framework.com;

import java.io.Serializable;

import jp.arcanum.framework.page.AbstractPage;
import jp.arcanum.framework.util.Util;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class AbstractPanel extends Panel{


	public AbstractPanel(String id){
		super(id);
	}


	public void error(Serializable message, AjaxRequestTarget target, Form<?> form){
		//warn(message);
		info(message);


		AbstractPage page = (AbstractPage)getPage();

		if(target!=null)target.add(page.getFeedbackPanel());
	}

	public void setResponsePage(String url, PageParameters params){

		String clazzstr = ArUtil.getProperty("page." + url + ".class");
		Class clazz = ArUtil.loadClass(clazzstr);
		if(clazz == null){
			throw new RuntimeException(url + " に対応するクラスがありません");
		}

		if(params!=null){
			setResponsePage(clazz, params);
		}
		else{
			setResponsePage(clazz);
		}

	}

	public void setResponsePage(String url){
		setResponsePage(url, null);
	}



	public void redirectWithRestart(String niceurl){
		throw new RestartResponseException(Util.getNiceURLtoClass(niceurl));
	}

	public void redirectWithRestart(Class clazz){
		throw new RestartResponseException(clazz);
	}


	public String getDomain(){
		return getRequest().getUrl().getHost();
	}


	public AbstractPage getRootPage(){
		return (AbstractPage) getPage();
	}

}
