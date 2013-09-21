package jp.arcanum.framework.dao;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import jp.arcanum.framework.dao.com.CountSql;
import jp.arcanum.framework.dao.com.DeleteTable;
import jp.arcanum.framework.dao.com.InsertTable;
import jp.arcanum.framework.dao.com.SelectTableByField;
import jp.arcanum.framework.dao.com.UpdateTableByField;

public abstract class AbstractTable extends AbstractSqlSelectable{

	@Override
	public String getSql() {
		throw new RuntimeException("使わない");
	}

	/**
	 * テーブル名取得
	 * @return
	 */
	public abstract String getTableName();


	public int insert(Connection con){
		InsertTable sqlins = new InsertTable(this);
		sqlins.addStringValue(getStringValues());
		return sqlins.execute(con);
	}

	public int update(Connection con, AbstractTable where){

		UpdateTableByField sql = new UpdateTableByField(this, where);
		return sql.execute(con);

	}


	public int selectCount(Connection con){

		/*
			TODO さいごにはやる！

		CountSql sql = new CountSql(this);
		List list = sql.execute(con);
		if(list.isEmpty()){
			return 0;
		}

		CountSql record = (CountSql) list.get(0);
		return record.getFldCount();
		*/

		List list = select(con);
		return list.size();

	}

	public int delete(Connection con){
		DeleteTable sqldel = new DeleteTable(this);
		return sqldel.execute(con);
	}

	public List select(Connection con){
		return select(con, null, null);
	}

	public List select(Connection con, String[] orderby, String ascordesc){

		SelectTableByField sql = new SelectTableByField(this, orderby, ascordesc);
		List ret = sql.execute(con, this.getClass());

		return ret;
	}


	public AbstractTable selectOne(Connection con, boolean force){
		List<AbstractSqlSelectable> list = select(con);
		if(force && list.size()!=1){
			throw new RuntimeException("1件ではない！(" + list.size()+"件)");
		}
		if(list.isEmpty()){
			return null;
		}
		return (AbstractTable)list.get(0);
	}
}
