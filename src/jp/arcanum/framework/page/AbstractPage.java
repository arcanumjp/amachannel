package jp.arcanum.framework.page;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import jp.arcanum.framework.OpenGraph;
import jp.arcanum.framework.com.ArUtil;
import jp.arcanum.framework.component.MyFeedbackPanel;
import jp.arcanum.framework.dao.DBAccess;
import jp.arcanum.framework.util.Util;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class AbstractPage extends WebPage{


	private Label _headscript = new Label("headscript", new Model(""));
	{
		_headscript.setEscapeModelStrings(false);
	}


	/**
	 * ログイン情報パネル
	 */
	private Panel _logininfo;	// = new LoginInfoPanel("logininfo");

	/**
	 * app.propertieseで設定される
	 */
	public static Class LOGININFO_PANEL;



	protected static final AbstractMenu _MENU_SEP = new AbstractMenu() {

		@Override
		public String getMenuName() {
			return "  ";
		}

		@Override
		public boolean isSeparator() {
			return true;
		}


	};

	private Form _form = new Form("form");

	protected void addForm(Component comp){
		_form.add(comp);
	}

	/**
	 * メニューリスト
	 */
	private List<AbstractMenu> _menuitemlist = new ArrayList<AbstractMenu>();
	public void addMenu(AbstractMenu menuitem){
		_menuitemlist.add(menuitem);
	}
	private AbstractMenu _activemenu = null;
	protected void setActiveMenu(AbstractMenu activemenu){
		_activemenu = activemenu;
	}

	private ListView<AbstractMenu> _menulist = new ListView<AbstractMenu>("menulist", _menuitemlist) {
		@Override
		protected void populateItem(ListItem<AbstractMenu> listitem) {

			final AbstractMenu item = listitem.getModelObject();

			final WebMarkupContainer liitem = new WebMarkupContainer("liitem");
			if(item == _activemenu){
				liitem.add(new AttributeModifier("class", "active"));
			}

			listitem.add(liitem);
			listitem.setRenderBodyOnly(true);

			Link menuitem = new Link("menuitem") {
				@Override
				public void onClick() {
					_activemenu = item;
					item.onClick();
				}

				@Override
				protected void onConfigure() {
					setVisible(item.getVisible());
				}




			};
			liitem.add(menuitem);
			Label menuitemlabel = new Label("menuitemlabel", new Model(item.getMenuName()));
			if(item.isSeparator()){
				menuitem.add(new AttributeModifier("disabled", "true"));
			}
			menuitem.add(menuitemlabel);

		}
	};

	/**
	 * メッセージ
	 */
	//FeedbackPanel _feedback = new FeedbackPanel("feedback");
	private MyFeedbackPanel _feedback = new MyFeedbackPanel("feedback");
	public MyFeedbackPanel getFeedbackPanel(){
		return _feedback;
	}


	Label _title = new Label("title", new Model(""));
	public String getTitleTagBody(){
		return (String) _title.getDefaultModelObject();
	}
	public void setTitleTagBody(String body){
		_title.setDefaultModelObject(body);
	}


	public Component getComponent(String id){
		return _form.get(id);
	}


	// Open graph protocol
	protected WebMarkupContainer _ogtitle 			= new WebMarkupContainer("ogtitle");
	protected WebMarkupContainer _ogtype 			= new WebMarkupContainer("ogtype");
	protected WebMarkupContainer _ogdescription 	= new WebMarkupContainer("ogdescription");
	protected WebMarkupContainer _ogurl 			= new WebMarkupContainer("ogurl");
	protected WebMarkupContainer _ogimage 			= new WebMarkupContainer("ogimage");
	protected WebMarkupContainer _ogsite_name 		= new WebMarkupContainer("ogsite_name");
	protected WebMarkupContainer _ogemail 			= new WebMarkupContainer("ogemail");
	protected WebMarkupContainer _ogphone_number	= new WebMarkupContainer("ogphone_number");

	public abstract OpenGraph getOpenGraph();


	public AbstractPage(){

		add(_headscript);

		if(!ArUtil.getProperty("app.dbname").equals("")){
			Connection con = DBAccess.getConnection();

		}


		// アプリケーション名の設定
		String appname = ArUtil.getProperty("app.name");
		Label prjname = new Label("projectname", new Model(appname));
		add(prjname);

		//<head>タグ内の<title>タグの内容を変える
		_title.setDefaultModelObject(appname);
		_title.setRenderBodyOnly(true);
		add(_title);


		// オープングラフ系\
		add(_ogtitle);
		add(_ogtype);
		add(_ogdescription);
		add(_ogurl);
		add(_ogimage);
		add(_ogsite_name);
		add(_ogemail);
		add(_ogphone_number);

		OpenGraph ogobj = getOpenGraph();
		if(ogobj!=null){
			_ogtitle.add(new AttributeModifier("content", ogobj.getOgTitle()));
			_ogtype.add(new AttributeModifier("content", ogobj.getOgType()));
			_ogdescription.add(new AttributeModifier("content", ogobj.getOgDescription()));
			_ogurl.add(new AttributeModifier("content", ogobj.getOgUrl()));
			_ogimage.add(new AttributeModifier("content", ogobj.getOgImage()));
			_ogsite_name.add(new AttributeModifier("content", ogobj.getOgSiteName()));
			_ogemail.add(new AttributeModifier("content", ogobj.getOgEMail()));
			_ogphone_number.add(new AttributeModifier("content", ogobj.getOgPhoneNumber()));
		}


		// メニューリスト
		add(_menulist);


		// ログイン者情報
		if(LOGININFO_PANEL!=null){
			try {
				Class[] typelist = {String.class};
				Constructor constructor = LOGININFO_PANEL.getConstructor(typelist);

				String[] args = {"logininfo"};
				_logininfo = (Panel)constructor.newInstance(args);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
		else{
			_logininfo = new LoginInfoPanel("logininfo");
		}
		add(_logininfo);

		// メッセージ
		_feedback.setOutputMarkupId(true);
		_form.add(_feedback);

		// 唯一のフォームを追加
		add(_form);

	}


	public void error(Serializable message, Component comp){
		comp.info(message);
	}

	public void error(Serializable message, AjaxRequestTarget target, Form<?> form){
		//warn(message);
		info(message);

		if(target!=null)target.add(_feedback);
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
}
