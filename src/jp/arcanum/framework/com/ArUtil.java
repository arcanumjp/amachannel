package jp.arcanum.framework.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.stream.FileImageInputStream;



public class ArUtil {


	private static final Properties APP_PROPS = new Properties();
	public static final void readAppProperties(String proppath){

		InputStream is = null;
		try{
			is = new FileInputStream(proppath);
			APP_PROPS.load(is);

		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
		finally{
			try {
				if(is!=null)is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
	public static final boolean isEmptyProperty(){
		return APP_PROPS.isEmpty();
	}

	public static final String getProperty(String key){
		String ret = APP_PROPS.getProperty(key);
		if(ret==null){
			ret = "";
		}
		return ret;
	}


	public static final Class getNiceURLtoClass(String niceURL){

		String clazzstr = getProperty("page." + niceURL + ".class");
		if(clazzstr.equals("")){
			return null;
		}
		Class clazz = loadClass(clazzstr);

		return clazz;

	}

	public static final Class loadClass(String clazz){

		Class ret = null;
		try{
			ret = Class.forName(clazz);
		}
		catch(Exception e){
			// 握りつぶし

		}
		return ret;

	}


	public static final String getThrowableString(Throwable t){

		StringBuilder ret = new StringBuilder();

		StackTraceElement[] elements = t.getStackTrace();
		for(int i=0; i<elements.length; i++){

			ret.append(elements[i].toString());
			ret.append("\r\n");
		}

		return ret.toString();
	}



	public static final List<String> readFile(String path){

		List ret = new ArrayList();

		FileInputStream fis = null;
		InputStreamReader ir = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(path);
			ir = new InputStreamReader(fis , "UTF-8");
			br = new BufferedReader(ir);

			String line;
			while((line = br.readLine()) != null){

				line = line.trim();
				ret.add(line);


			}

		} catch (Exception e) {
			throw new RuntimeException("ファイル読み込み中に失敗",e);
		}
		finally{
			try{
				if(fis!=null)fis.close();
				if(ir!=null)ir.close();
				if(fis!=null)fis.close();
			}
			catch(Exception e){
				throw new RuntimeException("ファイルを閉じるときに失敗", e);
			}

		}

		return ret;

	}

}
