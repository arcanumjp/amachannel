package jp.arcanum.framework.com;

import java.io.Serializable;

public class SelectOption implements Serializable{

	private String _value;
	public String getValue(){
		return _value;
	}
	public void setValue(String str){
		_value = str;
	}

	private String _label;
	public String getLabel(){
		return _label;
	}
	public void setLabel(String str){
		_label = str;
	}

	public SelectOption(
		String value,
		String label
	){
		_value = value;
		_label = label;
	}

	@Override
	public String toString(){

		return getValue() + " - " + getLabel();

	}


}
