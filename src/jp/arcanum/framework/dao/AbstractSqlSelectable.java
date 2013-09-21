package jp.arcanum.framework.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.postgis.PGgeometry;
import org.postgis.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSqlSelectable extends AbstractSqlExecutable {

	/**
	 * レコード
	 */
	private Map<String, Object> _record = new HashMap<String, Object>();
	public void setValue(final String key, final Object value){
		_record.put(key, value);
	}
	public void setValueGeoPoint(String field, double lat, double lng){
		//GeomFromText('POINT(50 50)', 4326),
		String position = "GeomFromText('POINT(" + lat + " " + lng +")', 4326)";
		setValue(field, position);
		addStringValue(field);
	}
	public void setValueGeoLine(String field, List<Map<String, String>> points){

		String insvalue = "GeomFromText('LINESTRING(";

		for(int i=0; i<points.size(); i++){
			Map<String, String> point = points.get(i);
			String lat = point.get("lat");
			String lng = point.get("lng");
			String pointstr = lat + " " + lng;

			insvalue = insvalue + pointstr;
			if(i!=points.size()-1){
				insvalue = insvalue + ",";
			}
		}

		insvalue = insvalue + ")', 4326)";

		setValue(field, insvalue);
		addStringValue(field);
	}
	public void setValueGeoLineByString(String field, String points){

		StringBuilder insvalue = new StringBuilder("GeomFromText('LINESTRING(");
		insvalue.append(points);
		insvalue.append(")', 4326)");

		setValue(field, insvalue);
		addStringValue(field);
	}

	private String _stringvaues = "";
	public void addStringValue(String strval){
		_stringvaues = _stringvaues + "," +strval;
	}
	public String getStringValues(){
		return _stringvaues;
	}


	public Object getObject(final String fld){
		checkField(fld);
		Object ret = _record.get(fld);
		return ret;
	}
	public Map<String, Object> getRecord(){
		return _record;
	}
	public List<String> getFieldNames(){

		List<String> ret = new ArrayList<String>();

		Iterator ite = _record.keySet().iterator();
		while(ite.hasNext()){
			ret.add((String)ite.next());
		}
		return ret;

	}


	public boolean isNullField(String fld){
		checkField(fld);
		if(_record.get(fld)==null){
			return true;
		}
		return false;
	}

	private void checkField(String fld){
		if(!_record.containsKey(fld)){
			throw new RuntimeException("このレコードにそのキーのデータはありません。 " + fld);
		}
	}

	public String getString(final String key){
		checkField(key);
		String ret = (String)_record.get(key);
		return ret;
	}

	public Date getDate(final String key){
		checkField(key);
		/*
		String datestr =(String)_record.get(key);
		if(datestr==null){
			return null;
		}
		Date ret;
		try {
			ret = new SimpleDateFormat("yyyy-MM-dd").parse(datestr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return ret;
		*/

		return (Timestamp)_record.get(key);


	}

	public int getInt(final String key){
		checkField(key);
		//String ret = (String)_record.get(key);
		//return Integer.parseInt(ret);
		return (Integer)_record.get(key);
	}

	public long getLong(final String key){
		checkField(key);
		//String ret = (String)_record.get(key);
		//return Long.parseLong(ret);
		return (Long)_record.get(key);
	}

	public double getDouble(final String key){
		checkField(key);
		//String ret = (String)_record.get(key);
		//return Double.parseDouble(ret);
		return (Double)_record.get(key);
	}

	/*
	public PGgeometry getGeometry(final String key){
		checkField(key);
		PGgeometry ret = (PGgeometry)_record.get(key);
		return ret;
	}
	*/

	public boolean getBoolean(final String key){
		checkField(key);

		/*

		http://www.postgresql.jp/document/8.3/html/datatype-boolean.html

		"真"状態に対する有効なリテラル値には次のものがあります。

		TRUE
		't'
		'true'
		'y'
		'yes'
		'1'
		"偽"状態に対する有効なリテラル値には次のものがあります。
		FALSE
		'f'
		'false'
		'n'
		'no'
		'0'
		*/

		if(_record.get(key) instanceof Boolean){
			Boolean ret = (Boolean)_record.get(key);
			return ret.booleanValue();
		}


		String value = (String)_record.get(key);
		if(value.equals("TRUE")){
			return Boolean.TRUE;
		}
		if(value.equals("t")){
			return Boolean.TRUE;
		}
		if(value.equals("true")){
			return Boolean.TRUE;
		}
		if(value.equals("y")){
			return Boolean.TRUE;
		}
		if(value.equals("yes")){
			return Boolean.TRUE;
		}
		if(value.equals("1")){
			return Boolean.TRUE;
		}


		if(value.equals("FALSE")){
			return Boolean.FALSE;
		}
		if(value.equals("f")){
			return Boolean.FALSE;
		}
		if(value.equals("false")){
			return Boolean.FALSE;
		}
		if(value.equals("n")){
			return Boolean.FALSE;
		}
		if(value.equals("no")){
			return Boolean.FALSE;
		}
		if(value.equals("0")){
			return Boolean.FALSE;
		}

		throw new RuntimeException("Boolean?? " + value);

	}


	public List<AbstractSqlSelectable> execute(Connection con, Class clazz){

		List<AbstractSqlSelectable> ret = new ArrayList<AbstractSqlSelectable>();


		ResultSet result = null;
		PreparedStatement pst = null;

		try{
			clearParams();
			String sql = getSql();

			pst = con.prepareStatement(sql);
			if(IS_SQL_OUT){
				// ログ出力
				Logger logger = LoggerFactory.getLogger(getClass());
				logger.debug("SQL:" + sql);

				// TODO 引数も出力　後で書く

			}

			for(int i = 0 ; i < getParams().size(); i++){
				if(IS_SQL_OUT){
					Logger logger = LoggerFactory.getLogger(getClass());
					logger.debug("    " + getParams().get(i));
				}

				Object value = getParams().get(i);
				if(value instanceof Date){
					pst.setObject(i+1, new java.sql.Date(((Date)value).getTime()));
				}
				else{
					pst.setObject(i+1, value);
				}

			}

        	result = pst.executeQuery();

        	while(result.next()){
        		AbstractSqlSelectable record = getInstance(clazz);
        		ResultSetMetaData meta = result.getMetaData();
        		for(int i = 1 ; i < meta.getColumnCount()+1; i++){
        			String colname = meta.getColumnName(i);
        			Object value   = result.getObject(i);
        			record.setValue(colname, value);

        		}
        		ret.add(record);
        	}

		}
		catch(Exception e){
			throw new RuntimeException("SELECT系のSQLに失敗", e);
		}
		finally{
			try {
				if(result!=null){
					result.close();
				}
				if(pst!=null){
					pst.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException("更新系のSQLに失敗(クローズに失敗)", e);
			}
		}

		return ret;

	}

	public List<AbstractSqlSelectable> execute(Connection con){
		return execute(con, getClass());
	}


	public AbstractSqlSelectable executeOne(Connection con){
		return executeOne(con, false);
	}

	public AbstractSqlSelectable executeOne(Connection con, boolean force){

		List list = execute(con);
		if(list.isEmpty()){

			if(force){
				throw new RuntimeException("１件あるはずが０件");
			}
			return null;
		}
		if(list.size() != 1){
			throw new RuntimeException("１件のはずが２件以上");
		}

		return (AbstractSqlSelectable) list.get(0);

	}


	private AbstractSqlSelectable getInstance(Class clazz){
		AbstractSqlSelectable ret = null;

		try{
			ret = (AbstractSqlSelectable)clazz.newInstance();
		}
		catch(Exception e){
			throw new RuntimeException("Ｓｅｌｅｃｔ系クラスにデフォルトコンストラクタを作成しなかった可能性とか・・・", e);
		}

		return ret;
	}

	@Override
	public String toString() {

		String ret = "\r\n " + getClass().getName() + " ----------------------------------------------------- \r\n";
		List<String> flds = getFieldNames();
		for(int i=0; i<flds.size(); i++){
			if(flds.get(i)!=null){
				ret = ret + flds.get(i) + "   : " + flds.get(i).getClass().getName() + " / " + getObject(flds.get(i)) + "\r\n";
			}
			else{
				ret = ret + flds.get(i) + "   : (null)\r\n";
			}

		}

		return ret;

	}


}
