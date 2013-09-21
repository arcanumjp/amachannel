package jp.arcanum.framework.util;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.arcanum.framework.com.ArUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Util extends ArUtil{

	public static String APP_MODE = "";
	public static final String APP_MODE_DEVELOPMENT = "DEVELOPMENT";
	public static final String APP_MODE_DEPLOYMENT = "DEPLOYMENT";

	/**
	 *
	 */
	public static boolean IS_HONBAN_DEBUG;


	public static String editNumberWithComma(int num){
		return NumberFormat.getNumberInstance().format(num);
	}

	public static String editNumberWithComma(String str){
		return editNumberWithComma(Integer.parseInt(str));
	}

	private static final SimpleDateFormat DATE_DETAIL_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public static final String formatDateDetail(Date d){
		return DATE_DETAIL_FORMAT.format(d);
	}

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	public static final String formatDate(Date d){
		return DATE_FORMAT.format(d);
	}

	private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy/MM");
	public static final String formatYearMonth(Date d){
		return MONTH_FORMAT.format(d);
	}

	private static final SimpleDateFormat HUMAN_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	public static final String formatHumanYmdhms(Date d){
		return HUMAN_FORMAT.format(d);
	}

	public static final String formatYearMonth(String yyyymm){
		return yyyymm.substring(0,  4) + yyyymm.substring(4,6);
	}

	public static final String formatYearMonthDay(String yyyymmdd){
		return yyyymmdd.substring(0,  4) + "/" + yyyymmdd.substring(4, 6) + "/" + yyyymmdd.substring(6, 8);
	}

	public static final String formatDate(String yyyymmdd){
		return yyyymmdd.substring(6, 8);
	}

	public static final Date toDate(String yyyymmdd){

		Date ret = null;
		try {
			ret = DateFormat.getDateInstance().parse(formatYearMonthDay(yyyymmdd));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return ret;

	}

	public static boolean isNumber(String str){

		str = str.replaceAll(",", "");

		try{
			Integer.parseInt(str);
		}
		catch(Exception e){
			return false;
		}
		return true;
	}


	public static Node getXmlFirstElementByTagName(Node node, String tagname){

		NodeList list = node.getChildNodes();
		for(int i=0; i<list.getLength(); i++){

			Node child = list.item(i);
			String nodename = child.getNodeName();
			if(nodename!=null && nodename.equals(tagname)){
				return child;
			}

		}

		return null;

	}

	public static List<Node> getXmlElementsByTagName(Node node, String tagname){

		List<Node> ret = new ArrayList<Node>();

		NodeList list = node.getChildNodes();
		for(int i=0; i<list.getLength(); i++){

			Node child = list.item(i);
			String nodename = child.getNodeName();
			if(nodename!=null && nodename.equals(tagname)){
				ret.add(child);
			}

		}

		return ret;

	}


	public static String getXmlAttribute(Node node, String attr){

		NamedNodeMap nodemap = node.getAttributes();
		Node attrnode =  nodemap.getNamedItem(attr);
		if(attrnode!=null){
			return attrnode.getTextContent();
		}

		return null;
	}


	public static final int parseInt(String str, int defaultval, boolean defaultfource){

		int ret = defaultval;
		try{
			ret = Integer.parseInt(str);
		}
		catch(Exception e){
			if(!defaultfource){
				throw new RuntimeException(e);
			}
		}
		return ret;
	}


	public static final double parseDouble(String str, int defaultval, boolean defaultfource){

		double ret = defaultval;
		try{
			ret = Double.parseDouble(str);
		}
		catch(Exception e){
			if(!defaultfource){
				throw new RuntimeException(e);
			}
		}
		return ret;
	}


    /**
     * 文字列に含まれる全角数字を半角数字に変換します。
     * @param str 変換前文字列(null不可)
     * @return 変換後文字列
     **/
	public static String fullWidthNumberToHalfWidthNumber(String str) {

		if (str == null){
			throw new IllegalArgumentException();
		}

		StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ('０' <= c && c <= '９') {
				sb.setCharAt(i, (char) (c - '０' + '0'));
			}
		}
		return sb.toString();
	}


	/**
	 * 文字列に含まれる半角数字を全角数字に変換します。
	 *
	 * @param str 変換前文字列(null不可)
	 * @return 変換後文字列
	 **/
	public static String halfWidthNumberToFullWidthNumber(String str) {
		if (str == null){
			throw new IllegalArgumentException();
		}
		StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ('0' <= c && c <= '9') {
				sb.setCharAt(i, (char) (c - '0' + '０'));
			}
		}
		return sb.toString();
	}


	public static final Document httpGet(String url){

		Document ret = null;

		try {
			//url = URLEncoder.encode(url, "UTF-8");

			//LoggerFactory.getLogger(Util.class).debug("楽天API(ジャンル検索)：" + url);

			HttpClient httpclient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(url);
			HttpResponse httpresponse = httpclient.execute(httpget);

			HttpEntity httpEntity = httpresponse.getEntity();

			DocumentBuilderFactory dfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dfac.newDocumentBuilder();
			ret  = dbuilder.parse(httpEntity.getContent());


		} catch (Exception e) {
			// 握り潰し
			e.printStackTrace();
		}

		return ret;

	}

	public static String getExceptionMess(Throwable t){

		StringBuilder ret = new StringBuilder("");

		StackTraceElement[] elems = t.getStackTrace();
		for(int i=0; i<elems.length; i++){
			ret.append(elems[i].toString()).append("\r\n");
		}

		return t.getMessage() + "\r\n" + ret.toString();
	}

}
