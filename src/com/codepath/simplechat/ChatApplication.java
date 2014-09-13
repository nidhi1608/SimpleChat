package com.codepath.simplechat;

import android.app.Application;

import com.parse.Parse;

public class ChatApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "AERqqIXGvzHQa7Nmg45xa5T8zWRRjqT8UmbFQeeI", "8bXPznF5eSLWq0sY9gTUuPrEF5BJlia7ltmLQFRh");
	}

}
