package org.iplantc.de.desktop.client.presenter;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.desktop.client.DesktopView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

import java.util.List;
import java.util.logging.Logger;

/**
 * This class contains all runtime callbacks used by the {@link DesktopPresenterImpl}
 * @author jstroot
 */
class RuntimeCallbacks {
    static class GetUserSessionCallback implements AsyncCallback<List<WindowState>> {
        private final IplantAnnouncer announcer;
        private final DesktopPresenterImpl presenter;
        private final AutoProgressMessageBox progressMessageBox;
        private final DesktopView.Presenter.DesktopPresenterAppearance appearance;

        public GetUserSessionCallback(final AutoProgressMessageBox progressMessageBox,
                                      final DesktopView.Presenter.DesktopPresenterAppearance appearance,
                                      final IplantAnnouncer announcer,
                                      final DesktopPresenterImpl presenter) {
            this.progressMessageBox = progressMessageBox;
            this.appearance = appearance;
            this.announcer = announcer;
            this.presenter = presenter;
        }

        @Override
        public void onFailure(Throwable caught) {
            final SafeHtml message = SafeHtmlUtils.fromTrustedString(appearance.loadSessionFailed());
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 5000));
            presenter.setUserSessionConnection(false);
            progressMessageBox.hide();
        }

        @Override
        public void onSuccess(List<WindowState> result) {
            presenter.setUserSessionConnection(true);
            presenter.restoreWindows(result);
            presenter.doPeriodicSessionSave();
            progressMessageBox.hide();
        }
    }

    static class LogoutCallback implements AsyncCallback<String> {
        private final DEClientConstants constants;
        private final List<WindowState> orderedWindowStates;
        private final UserSessionServiceFacade userSessionService;
        private final UserSettings userSettings;
        private final DesktopView.Presenter.DesktopPresenterAppearance appearance;
        private final Logger LOG = Logger.getLogger(LogoutCallback.class.getName());

        public LogoutCallback(final UserSessionServiceFacade userSessionService,
                               final DEClientConstants constants,
                               final UserSettings userSettings,
                               final DesktopView.Presenter.DesktopPresenterAppearance appearance,
                               final List<WindowState> orderedWindowStates) {
            this.userSessionService = userSessionService;
            this.constants = constants;
            this.userSettings = userSettings;
            this.appearance = appearance;
            this.orderedWindowStates = orderedWindowStates;
        }

        @Override
        public void onFailure(Throwable arg0) {
            LOG.warning("Error on logout:" + arg0.getMessage());
            // logout anyway
            logout();
        }

        @Override
        public void onSuccess(String arg0) {
            logout();
        }

        void logout() {
            final String redirectUrl = GWT.getHostPageBaseURL() + constants.logoutUrl();
            LOG.info("RedirectUrl = " + redirectUrl);
            if (userSettings.isSaveSession()) {
                final AutoProgressMessageBox progressMessageBox = getProgressMessage();
                progressMessageBox.getProgressBar().setDuration(1000);
                progressMessageBox.getProgressBar().setInterval(100);
                progressMessageBox.auto();
                userSessionService.saveUserSession(orderedWindowStates, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log(appearance.saveSessionFailed(), caught);
                        progressMessageBox.hide();
                        Window.Location.assign(redirectUrl);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        progressMessageBox.hide();
                        Window.Location.assign(redirectUrl);
                    }
                });

                progressMessageBox.show();
            } else {
                Window.Location.assign(redirectUrl);
            }
        }

        AutoProgressMessageBox getProgressMessage() {
            return new AutoProgressMessageBox(appearance.savingSession(),
                                              appearance.savingSessionWaitNotice());
        }

    }

    static class SaveUserSettingsCallback implements AsyncCallback<Void> {
        private final IplantAnnouncer announcer;
        private final DesktopView.Presenter.DesktopPresenterAppearance appearance;
        boolean updateSilently;
        private final UserSettings newValue;
        private final UserSettings userSettings;
        private final DesktopView.Presenter presenter;
        private final UserSessionServiceFacade userSessionService;

        public SaveUserSettingsCallback(final UserSettings newValue,
                                        final UserSettings userSettings,
                                        final IplantAnnouncer announcer,
                                        final DesktopView.Presenter presenter,
                                        final DesktopView.Presenter.DesktopPresenterAppearance appearance,
                                        final boolean updateSilently,
                                        final UserSessionServiceFacade userSessionService) {
            this.newValue = newValue;
            this.userSettings = userSettings;
            this.presenter = presenter;
            this.announcer = announcer;
            this.appearance = appearance;
            this.updateSilently = updateSilently;
            this.userSessionService =  userSessionService;
        }

        @Override
        public void onFailure(Throwable caught) {
            announcer.schedule(new ErrorAnnouncementConfig("Sorry about that, we were unable to save your preferences."));
        }

        @Override
        public void onSuccess(Void result) {
            userSettings.setUserSettings(newValue.getUserSetting());
            if(userSettings.isSaveSession()) {
                userSessionService.saveUserSession(presenter.getOrderedWindowStates(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        announcer.schedule(new ErrorAnnouncementConfig(appearance.saveSessionFailed()));
                        presenter.setUserSessionConnection(false);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        presenter.setUserSessionConnection(true);
                        showSaveSettingSuccess();
                    }
                });
            } else {
                showSaveSettingSuccess();
            }
        }

        void showSaveSettingSuccess() {
            if (!updateSilently) {
                announcer.schedule(new SuccessAnnouncementConfig(appearance.saveSettings(), true, 3000));
            }
        }
    }
}
