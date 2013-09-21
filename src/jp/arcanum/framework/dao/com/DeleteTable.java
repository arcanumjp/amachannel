package jp.arcanum.framework.dao.com;

import java.util.List;

import jp.arcanum.framework.dao.AbstractSqlUpdatable;
import jp.arcanum.framework.dao.AbstractTable;

public class DeleteTable extends AbstractSqlUpdatable{


	private AbstractTable _deltable;

	public DeleteTable(AbstractTable deltable){
		_deltable = deltable;
	}



	@Override
	public String getSql() {

		// DELETE FROM XXX_TBL WHERE XX=XX AND YY=YY AND ...;

		String sql = "DELETE FROM " + _deltable.getTableName();

		List<String> flds = _deltable.getFieldNames();
		if(!flds.isEmpty()){
			sql = sql + " WHERE ";

			for(int i=0; i<flds.size(); i++){
				if(i!=0){
					sql = sql + " AND ";
				}
				sql = sql + flds.get(i) + "=" + quote(_deltable.getObject(flds.get(i)));

			}


		}

		return sql;
	}

}
