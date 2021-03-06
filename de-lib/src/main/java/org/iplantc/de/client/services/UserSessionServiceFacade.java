package org.iplantc.de.client.services;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.userSettings.UserSetting;
import org.iplantc.de.client.models.webhooks.Webhook;
import org.iplantc.de.client.models.webhooks.WebhookTypeList;
import org.iplantc.de.shared.AppsCallback;
import org.iplantc.de.shared.DECallback;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface UserSessionServiceFacade {

    Request getUserSession(AsyncCallback<List<WindowState>> callback);

    Request saveUserSession(List<WindowState> windowStates, AsyncCallback<Void> callback);

    Request getUserPreferences(AsyncCallback<UserSetting> callback);

    void saveUserPreferences(UserSetting setting, AsyncCallback<Void> callback, AppsCallback<Void> hookCallback);

    void postClientNotification(Notification notification, AsyncCallback<String> callback);

    Request bootstrap(AsyncCallback<String> callback);

    void logout(AsyncCallback<String> callback);

    void testWebhook(String url, AsyncCallback<Void> callback);

    void updateWebhooks(List<Webhook> hooks, DECallback<Void> callback);

    void getWebhookTypes(DECallback<WebhookTypeList> callback);
}
