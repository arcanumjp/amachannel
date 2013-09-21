package jp.arcanum.wsbbs.thread;

import java.util.ArrayList;
import java.util.List;

import jp.arcanum.framework.util.Util;
import jp.arcanum.wsbbs.core.TwitterMessageInbound;

import org.apache.catalina.websocket.MessageInbound;
import org.slf4j.LoggerFactory;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

public class WebSocketManager {





	public WebSocketManager(String screenname, String trackword){

        _twstream = new TwitterStreamFactory().getInstance();

        String consumer       = Util.getProperty("twitter.oauth.consumer");
        String consumersecret = Util.getProperty("twitter.oauth.consumersecret");
        String acctoken       = Util.getProperty("twitter.oauth.acctoken");
        String acctokensecret = Util.getProperty("twitter.oauth.acctokensecret");

        System.out.println("con     : " + consumer);
        System.out.println("con sec : " + consumersecret);
        System.out.println("acc     : " + acctoken);
        System.out.println("acc sec : " + acctokensecret);

        _twstream.setOAuthConsumer(consumer, consumersecret);
        _twstream.setOAuthAccessToken(new AccessToken(acctoken, acctokensecret));

        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {

            	if(!status.getUser().getLang().equalsIgnoreCase("ja")){
            		return;
            	}
                //System.out.println(status.getCreatedAt() +   " @" + status.getUser().getScreenName() + " - " + status.getText());

                for(TwitterMessageInbound inbound : _iblist){
                    //System.out.println("    --> " + inbound.toString());
                	inbound.writeTextMessage(status);
                }

           }
            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) { }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) { }

            @Override
            public void onStallWarning(StallWarning warning) { }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
                _twstream.shutdown();
                System.out.println("==================================================================");
                System.out.println(" ストリームを終了しました。");
                System.out.println("==================================================================");

            }
        };

        //NoClassDefFoundError: twitter4j/internal/org/json/JSONString
        //twitter4j.internal.org.json.JSONString s;

        _twstream.addListener(listener);


        FilterQuery fq = new FilterQuery();
        fq.track(new String[]{"あまちゃん"});

        _twstream.filter(fq);

	}


	/**
	 *
	 */
	private TwitterStream _twstream;


	/**
	 * WebSocket通信のインバウンドリスト
	 */
	private List<TwitterMessageInbound> _iblist = new ArrayList<TwitterMessageInbound>();
	public void addInbound(TwitterMessageInbound inbound){
		_iblist.add(inbound);
	}
	public void removeInbound(TwitterMessageInbound inbound){
		_iblist.remove(inbound);
	}




}
