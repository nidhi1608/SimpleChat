package com.codepath.simplechat;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {
	private static final String TAG = ChatActivity.class.getName();
	public static final String USER_ID_KEY = "userId";
	private static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
	private static String sUserId;
	
	private EditText etMessage;
	private Button btSend;
	private ListView lvChat;
	private ChatListAdapter mAdapter;
	private ArrayList<Message> mMessages = new ArrayList<Message>();
	private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (ParseUser.getCurrentUser() != null) {
			startWithCurrentUser();
		} else {
			login();
		}
        handler.postDelayed(runnable, 100);
    }
    
    private Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
		      refreshMessages();
		      handler.postDelayed(this, 100);
		   }
	};

	private void refreshMessages() {
		receiveMessage();		
	}
    
    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
    	sUserId = ParseUser.getCurrentUser().getObjectId();	
    	saveMessage();
	}
    
    // Create an anonymous user using ParseAnonymousUtils and set sUserId 
    private void login() {
		ParseAnonymousUtils.logIn(new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if (e != null) {
					Log.d(TAG, "Anonymous login failed.");
				} else {
					startWithCurrentUser();
				}
			}
		});		
	}
    
    private void saveMessage() {
    	etMessage = (EditText) findViewById(R.id.etMessage);
		btSend = (Button) findViewById(R.id.btSend);
		lvChat = (ListView) findViewById(R.id.lvChat);
		mAdapter = new ChatListAdapter(ChatActivity.this, sUserId, mMessages);
		lvChat.setAdapter(mAdapter);
		btSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String data = etMessage.getText().toString();
				ParseObject message = new ParseObject("Messages");
				message.put(USER_ID_KEY, sUserId);
				message.put("message", data);
				message.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						receiveMessage();
					}
				});
				etMessage.setText("");
			}
		});
    }
    
    private void receiveMessage() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
		query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> messages, ParseException e) {
				if (e == null) {
					final ArrayList<Message> newMessages = new ArrayList<Message>();					
					for (int i = messages.size() - 1; i >= 0; i--) {
						final Message message = new Message();
						message.userId = messages.get(i).getString(USER_ID_KEY);
						message.text = messages.get(i).getString("message");
						newMessages.add(message);
					}
					addItemstoListView(newMessages);
				} else {
					Log.d("message", "Error: " + e.getMessage());
				}
			}
		});
	}
    
    private void addItemstoListView(final List<Message> messages) {
		mMessages.clear();
		mMessages.addAll(messages);
		mAdapter.notifyDataSetChanged();
		lvChat.invalidate();
	}

}
