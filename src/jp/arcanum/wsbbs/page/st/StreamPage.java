package jp.arcanum.wsbbs.page.st;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;

public class StreamPage extends WebPage{

	private Button _start = new Button("start") {
		public void onClick(){
			onClickStart();
		}
	};



	public StreamPage(){
		add(_start);
	}





	private void onClickStart(){

	}

}
