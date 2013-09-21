package jp.arcanum.framework.dao.com;

import java.util.List;
import java.util.Map;

import jp.arcanum.framework.dao.AbstractSqlSelectable;
import jp.arcanum.framework.dao.AbstractSqlUpdatable;
import jp.arcanum.framework.dao.AbstractTable;

public class UpdateTableByField extends AbstractSqlUpdatable{

	private AbstractTable _set;
	private AbstractTable _where;

	/**
	 * デフォルトコンストラクタ
	 * 基盤が利用
	 */
	public UpdateTableByField(){

	}

	/**
	 *
	 * @param where where句条件
	 * @param orderby ソート順
	 * @param ascordesc null・・・指定なしorderbyがnullの場合は無視
	 */
	public UpdateTableByField(
			AbstractTable set,
			AbstractTable where
	){
		_set = set;
		_where = where;
	}

	@Override
	public String getSql() {

		String sql = "UPDATE " + _where.getTableName();		// TODO あとでフィールド名称を別個に

		sql = sql + " SET ";
		Map<String, Object> setrecord = _set.getRecord();
		List<String> setlist = _set.getFieldNames();
		for(int i=0; i<setlist.size(); i++){
			sql = sql + setlist.get(i) + " = " + quote(setrecord.get(setlist.get(i)));
			if(i<setlist.size()-1){
				sql = sql + ", ";
			}
		}


		Map<String, Object> whererecord = _where.getRecord();

		// WHERE句の編集
		if(!whererecord.keySet().isEmpty()){

			sql = sql + " WHERE ";

			List<String> wherelist = _where.getFieldNames();
			for(int i=0; i<wherelist.size(); i++){
				sql = sql + wherelist.get(i) + " = " + quote(whererecord.get(wherelist.get(i)));
				if(i<wherelist.size()-1){
					sql = sql + " AND ";
				}
			}

		}

		return sql;
	}

}
