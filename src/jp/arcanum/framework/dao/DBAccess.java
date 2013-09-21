package jp.arcanum.framework.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.print.attribute.HashAttributeSet;
import javax.sql.DataSource;

import jp.arcanum.framework.com.ArUtil;

import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.LoggerFactory;

public class DBAccess extends AbstractRequestCycleListener{


	private DBAccess(){

	}


	public static final DBAccess getInstance(){
		return new DBAccess();
	}

	private static final ThreadLocal<Connection> CON = new ThreadLocal<Connection>();

	@Override
	public void onBeginRequest(RequestCycle cycle) {
		CON.set(getConnectionJNDI());
	}

	@Override
	public void onEndRequest(RequestCycle cycle) {


		Connection con = null;
		try {
			con = CON.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally{
			try {
				if(con!=null){
					con.close();
					CON.remove();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}


	}

	public static final Connection getConnection(){
		return CON.get();
	}

	private static Map<String, Class<?>> TYPEMAP =new HashMap();
	static {
		TYPEMAP.put("geometry",org.postgis.PGgeometry.class);
	}

	public static final Connection getConnectionJNDI(){

		Connection ret = null;
		try {

			String dbname = ArUtil.getProperty("app.dbname");
			if(!dbname.equals("")){
				InitialContext initCon = new InitialContext(); //(1)
				DataSource ds = (DataSource)initCon.lookup("java:comp/env/jdbc/" + dbname); //(2)
				ret = ds.getConnection(); //(3)JNDIリソースへのコネクト
				ret.setAutoCommit(false);	// 自動コミットoff!


				//Map typemap = ret.getTypeMap();
				//typemap.put("geometry",org.postgis.PGgeometry.class);
				//ret.setTypeMap(typemap);
				ret.setTypeMap(TYPEMAP);

				 //((org.postgresql.PGConnection)ret).addDataType("geometry",org.postgis.PGgeometry.class);

			}
		}
		catch (Exception e) {
			throw new RuntimeException("コネクション取得に失敗", e);
		}

		return ret;
	}

	public static final void commit(){
		Connection con = null;
		try {
			con = CON.get();
			//LoggerFactory.getLogger(DBAccess.class).debug("commit : connection : " + con.hashCode());
			con.commit();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


}
