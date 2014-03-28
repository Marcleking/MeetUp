package com.bouchardm.meetup;

import android.app.*;

import com.google.android.gms.common.api.GoogleApiClient;

public class LoggedPerson extends Application {
	private GoogleApiClient client;

	public GoogleApiClient getClient() {
		return client;
	}

	public void setClient(GoogleApiClient client) {
		this.client = client;
	}
	
}

