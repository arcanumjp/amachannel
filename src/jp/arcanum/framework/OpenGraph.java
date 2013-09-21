package jp.arcanum.framework;

import java.util.HashMap;
import java.util.Map;


public class OpenGraph {

	private Map<String, String> _record = new HashMap<String, String>();

	public String getOgTitle(){
		return _record.get("ogtitle");
	}

	public void setOgTitle(String value){
		_record.put("ogtitle", value);
	}



	public String getOgType(){
		return _record.get("ogtype");
	}

	public void setOgType(String value){
		_record.put("ogtype", value);
	}




	public String getOgDescription(){
		return _record.get("ogdescription");
	}

	public void setOgDescription(String value){
		_record.put("ogdescription", value);
	}





	public String getOgUrl(){
		return _record.get("ogurl");
	}

	public void setOgUrl(String value){
		_record.put("ogurl", value);
	}





	public String getOgImage(){
		return _record.get("ogimage");
	}

	public void setOgImage(String value){
		_record.put("ogimage", value);
	}





	public String getOgSiteName(){
		return _record.get("ogsite_name");
	}

	public void setOgSiteName(String value){
		_record.put("ogsite_name", value);
	}





	public String getOgEMail(){
		return _record.get("ogemail");
	}

	public void setOgEMail(String value){
		_record.put("ogemail", value);
	}




	public String getOgPhoneNumber(){
		return _record.get("ogphone_number");
	}

	public void setOgPhoneNumber(String value){
		_record.put("ogphone_number", value);
	}




}
