package com.codepath.simplechat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ChatListAdapter extends BaseAdapter {	
	private Context context;
	private String mUserId;
	private ArrayList<Message> mMessages;
	
	public ChatListAdapter(Context context, String userId, ArrayList<Message> messages) {
        this.context = context;
        this.mUserId = userId;
        this.mMessages = messages;
    }

	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
			final ViewHolder holder = new ViewHolder();
			holder.imageLeft = (ImageView)convertView.findViewById(R.id.ivProfileLeft);
			holder.imageRight = (ImageView)convertView.findViewById(R.id.ivProfileRight);
			holder.text = (TextView)convertView.findViewById(R.id.tvText);
			convertView.setTag(holder);
		}
		final Message message = (Message)getItem(position);
		final ViewHolder holder = (ViewHolder)convertView.getTag();
		final boolean isMe = message.userId.equals(mUserId);
		// Show-hide image based on the logged-in user. Display the profile image to the right for the current user, left for all other users.
		if (isMe) {
			holder.imageRight.setVisibility(View.VISIBLE);
			holder.imageLeft.setVisibility(View.GONE);
			holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		} else {
			holder.imageLeft.setVisibility(View.VISIBLE);
			holder.imageRight.setVisibility(View.GONE);
			holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		}
		final ImageView profileView = isMe ? holder.imageRight : holder.imageLeft;
		Picasso.with(context).load(getProfileUrl(message.userId)).into(profileView);
		holder.text.setText(message.text);
		return convertView;
	}
	
	// Create a gravatar based on the hash value obtained from userId
	private static String getProfileUrl(final String userId) {
		String hex = "";
		try {
			final MessageDigest digest = MessageDigest.getInstance("MD5");
			final byte[] hash = digest.digest(userId.getBytes());
			final BigInteger bigInt = new BigInteger(hash);
			hex = bigInt.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
	}
	
	final class ViewHolder {
		public ImageView imageLeft;
		public ImageView imageRight;
		public TextView text;
	}

}
