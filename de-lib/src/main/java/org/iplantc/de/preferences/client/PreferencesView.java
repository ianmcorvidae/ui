package org.iplantc.de.preferences.client;

import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.webhooks.WebhookTypeList;
import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.preferences.client.events.PrefDlgRetryUserSessionClicked;
import org.iplantc.de.preferences.client.events.ResetHpcTokenClicked;
import org.iplantc.de.preferences.client.events.TestWebhookClicked;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author aramsey
 */
public interface PreferencesView extends IsWidget,
                                         PrefDlgRetryUserSessionClicked.HasPrefDlgRetryUserSessionClickedHandlers,
                                         ResetHpcTokenClicked.HasResetHpcLabelClickedHandlers,
                                         TestWebhookClicked.HasTestWebhookClickedHandlers {

    interface PreferencesViewAppearance {

        String defaultOutputFolderHelp();

        String done();

        String duplicateShortCutKey(String key);

        String notifyEmailHelp();

        String preferences();

        String notifyAnalysisEmail();

        String notifyImportEmail();

        String completeRequiredFieldsError();

        String rememberFileSectorPath();

        String rememberFileSectorPathHelp();

        String restoreDefaults();

        String saveSession();

        String defaultOutputFolder();

        String keyboardShortCut();

        String openAppsWindow();

        String kbShortcutMetaKey();

        String oneCharMax();

        String openDataWindow();

        String openAnalysesWindow();

        String openNotificationsWindow();

        String closeActiveWindow();

        String saveSessionHelp();

        String notifyEmail();

        String waitTime();

        String retrySessionConnection();

        SafeHtml sessionConnectionFailed();

        String preferencesFailure();

        String preferencesSuccess();

        String resetHpc();

        SafeHtml resetHpcPrompt();

        String hpcResetFailure();

        String resetHpcHelp();

        String webhooks();

        String webhooksPrompt();

        String dataNotification();

        String appsNotification();

        String analysesNotification();

        String toolsNotification();

        String permNotification();

        String hookTopic();

        String test();

        String teamNotification();

        String testWebhookFail();

        String testWebhookSuccess();

        String mustSelectATopic();

        String data();

        String apps();

        String analysis();

        String permIdRequest();

        String team();
        
        String toolRequest();

        ImageResource deleteIcon();

        String validUrl();

        String width();

        String height();

        String emailSettings();

        String general();

        String notification();

        String webhookTypeFailure();
    }

    void userSessionSuccess();

    void userSessionFail();

    void initAndShow(UserSettings userSettings, WebhookTypeList typeList);

    UserSettings getValue();

    void setDefaultValues();

    void hide();

    boolean isValid();

    void flush();

    void saveUserSettings();

    interface Presenter {

        void go(DesktopView.Presenter presenter, UserSettings userSettings);

        void setDefaultValues();

        void saveUserSettings();

        PreferencesView getView();

        void setViewDebugId(String baseId);

        boolean isValid();
    }
}
