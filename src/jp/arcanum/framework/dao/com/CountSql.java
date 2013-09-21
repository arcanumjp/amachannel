package jp.arcanum.framework.dao.com;


import jp.arcanum.framework.dao.AbstractSqlSelectable;
import jp.arcanum.framework.dao.AbstractTable;

public class CountSql extends AbstractSqlSelectable{

	private AbstractSqlSelectable _mainsql;


	// 基盤が利用
	public CountSql(){

	}

	public CountSql(
			AbstractTable mainsql
	){
		_mainsql = mainsql;
	}



	@Override
	public String getSql() {

		String ret = "SELECT COUNT(*) AS FLD_COUNT FROM (" +  _mainsql.getSql() + ") AS FOO }";
		setParams(getParams());

		return ret;
	}

	public int getFldCount(){
		return getInt("FLD_COUNT FROM");

	}

}

