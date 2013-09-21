package jp.arcanum.framework.dao.com;

import java.util.List;
import java.util.Map;

import jp.arcanum.framework.dao.AbstractSqlSelectable;
import jp.arcanum.framework.dao.AbstractTable;

public class SelectTableByField extends AbstractSqlSelectable{

	private AbstractTable _where;

	private String[] _orderby;
	private String _ascordesc;


	/**
	 * デフォルトコンストラクタ
	 * 基盤が利用
	 */
	public SelectTableByField(){

	}

	/**
	 *
	 * @param where where句条件
	 * @param orderby ソート順
	 * @param ascordesc null・・・指定なしorderbyがnullの場合は無視
	 */
	public SelectTableByField(
			AbstractTable where,
			String[] orderby,
			String ascordesc
	){
		_where = where;
		_orderby = orderby;
		_ascordesc = ascordesc;
	}

	@Override
	public String getSql() {

		String sql = "SELECT * FROM " + _where.getTableName();		// TODO あとでフィールド名称を別個に

		Map<String, Object> record = _where.getRecord();

		// WHERE句の編集
		if(!record.keySet().isEmpty()){

			sql = sql + " WHERE ";

			List<String> fldlist = _where.getFieldNames();
			for(int i=0; i<fldlist.size(); i++){
				sql = sql + fldlist.get(i) + " = " + quote(record.get(fldlist.get(i)));
				if(i<fldlist.size()-1){
					sql = sql + " AND ";
				}
			}

		}


		if(_orderby!=null){

			String orderbystr = " order by ";
			for(int i=0; i<_orderby.length; i++){
				orderbystr = orderbystr + _orderby[i];
				if(i<_orderby.length-1){
					orderbystr = orderbystr + ",";
				}
			}
			if(_ascordesc!=null){
				orderbystr = orderbystr + " " + _ascordesc;
			}
			sql = sql + orderbystr;
		}

		return sql;
	}

}
