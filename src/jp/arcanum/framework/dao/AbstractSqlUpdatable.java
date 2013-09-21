package jp.arcanum.framework.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.postgis.PGgeometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSqlUpdatable extends AbstractSqlExecutable {

	public int execute(Connection con){

		int ret = -1;

		LoggerFactory.getLogger(getClass()).debug("connection : " + con.hashCode());


		//ResultSet result = null;
		PreparedStatement pst = null;

		try{

			String sql = getSql();
			pst = con.prepareStatement(sql);

			if(IS_SQL_OUT){
				// ログ出力
				Logger logger = LoggerFactory.getLogger(getClass());
				logger.debug("SQL:" + sql);
			}

			for(int i = 0 ; i < getParams().size(); i++){

				Object value = getParams().get(i);

				if(IS_SQL_OUT){
					if(value!=null){
						LoggerFactory.getLogger(getClass()).debug(value.getClass().getName() + " / " + value);
					}
					else{
						LoggerFactory.getLogger(getClass()).debug("null");
					}
				}

				//if(value instanceof Date){
				//	pst.setObject(i+1, new java.sql.Date(((Date)value).getTime()));
				//}
				//else if(value instanceof PGgeometry){
				//
				//	pst.setObject(
				//			i+1,
				//			new PGgeometry(((PGgeometry)value).toString())
				//	);
				//}
				//else{
					pst.setObject(i+1, value);
				//}
			}

			ret = pst.executeUpdate();


		}
		catch(Exception e){
			throw new RuntimeException("更新系のSQLに失敗", e);
		}
		finally{
			try {
				//if(result!=null){
				//	result.close();
				//}
				if(pst!=null){
					pst.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException("更新系のSQLに失敗(クローズに失敗)", e);
			}
		}

		return ret;

	}



}
