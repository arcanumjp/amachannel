package jp.arcanum.framework.dao.com;

import java.util.List;

import jp.arcanum.framework.dao.AbstractSqlUpdatable;
import jp.arcanum.framework.dao.AbstractTable;

public class InsertTable extends AbstractSqlUpdatable{


	private AbstractTable _instable;

	public InsertTable(AbstractTable instable){
		_instable = instable;
	}

	private String stringvalues = "";

	public void addStringValue(String field){
		stringvalues = stringvalues + "," + field;
	}

	@Override
	public String getSql() {

		// INSERT INTO XXX_TBL (XX,yy,zz...) VALUES('','',''...);

		String sql = "INSERT INTO " + _instable.getTableName();


		sql = sql + "(";

		List<String> flds = _instable.getFieldNames();
		for(int i=0; i<flds.size(); i++){
			sql = sql + flds.get(i) + ",";
		}
		sql= sql.substring(0,sql.length()-1);


		sql = sql + ") VALUES (";

		for(int i=0; i<flds.size(); i++){

			String field = flds.get(i);
			if(stringvalues.indexOf(field)!=-1){
				sql = sql + _instable.getObject(flds.get(i)) + ",";
			}
			else{
				sql = sql + quote(_instable.getObject(flds.get(i))) + ",";
			}

		}
		sql= sql.substring(0,sql.length()-1);

		sql = sql + ")";

		return sql;
	}

}
