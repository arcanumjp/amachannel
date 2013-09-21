package jp.arcanum.wsbbs.core;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import twitter4j.Status;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;

public class TwitterMessageInbound extends MessageInbound{

	//DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


	private static DocumentBuilder _db; // = dbf.newDocumentBuilder();
	static {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			_db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private long _lastoutboundtime;


	private WsOutbound _outbound;
	public void writeTextMessage(Status status){

		long nowtime = System.currentTimeMillis();
		if(_lastoutboundtime + 0 > System.currentTimeMillis()){
			System.out.println("  --> 無視");
			return;
		}
		_lastoutboundtime = nowtime;


		try {

			Document document = _db.newDocument();
			Element root = document.createElement("status");
			root.setAttribute("createat", status.getCreatedAt().toString());
			document.appendChild(root);

			Element user = document.createElement("user");
			user.setAttribute("screenname", status.getUser().getScreenName());
			user.setAttribute("iconurl", status.getUser().getProfileImageURL());
			root.appendChild(user);

			Element tweet = document.createElement("tweet");
			tweet.setTextContent(status.getText());
			root.appendChild(tweet);

			TransformerFactory tf = TransformerFactoryImpl.newInstance();

		    Transformer transformer = tf.newTransformer();
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		    StringWriter writer = new StringWriter();// <-ここでは文字列として出力している
		    StreamResult result = new StreamResult(writer);

		    DOMSource source = new DOMSource(document);
		    transformer.transform(source, result);

		    String xmlstr = writer.toString();
			_outbound.writeTextMessage(CharBuffer.wrap(xmlstr));
			System.out.println(xmlstr);


		} catch (Exception e) {
			System.out.println("         --> [[[[[[[[[[[[[[[[[[[[[[[コネクション終了の模様");
			TwitterSocketServlet._wsmanager.removeInbound(this);
		}
	}

	public TwitterMessageInbound(String subprotocol, HttpServletRequest request){
		// TODO 適切なところに自分を預ける
	}

	@Override
	public void onOpen(WsOutbound outbound) {
		this._outbound = outbound;
		TwitterSocketServlet._wsmanager.addInbound(this);
	}

    @Override
    public void onClose(int status) {
		TwitterSocketServlet._wsmanager.removeInbound(this);
    }

	@Override
	protected void onBinaryMessage(ByteBuffer bytebuff) throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void onTextMessage(CharBuffer charbuff) throws IOException {
		// クライアントから文字が送信された
	}


}
