package org.iplantc.de.analysis.client.presenter.parameters;

import static org.iplantc.de.client.models.apps.integration.ArgumentType.FileFolderInput;
import static org.iplantc.de.client.models.apps.integration.ArgumentType.FileInput;
import static org.iplantc.de.client.models.apps.integration.ArgumentType.FolderInput;
import static org.iplantc.de.client.models.apps.integration.ArgumentType.Input;
import static org.iplantc.de.client.models.apps.integration.ArgumentType.MultiFileSelector;

import org.iplantc.de.analysis.client.AnalysisParametersView;
import org.iplantc.de.analysis.client.events.SaveAnalysisParametersEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisParamValueSelectedEvent;
import org.iplantc.de.analysis.client.gin.factory.AnalysisParamViewFactory;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.payload.PayloadData;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;
import org.iplantc.de.shared.AnalysisCallback;
import org.iplantc.de.shared.AsyncProviderWrapper;
import org.iplantc.de.shared.DataCallback;

import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;

import java.util.List;

/**
 * @author jstroot
 */
public class AnalysisParametersPresenterImpl implements AnalysisParametersView.Presenter,
                                                        SaveAnalysisParametersEvent.SaveAnalysisParametersEventHandler,
                                                        AnalysisParamValueSelectedEvent.AnalysisParamValueSelectedEventHandler {

    private static class GetStatCallback extends DataCallback<FastMap<DiskResource>> {
        private final AnalysisParameter value;
        private final EventBus eventBus;
        private final IplantAnnouncer announcer;
        private final AnalysisParametersView.Appearance appearance;

        public GetStatCallback(final AnalysisParameter value,
                               final EventBus eventBus,
                               final IplantAnnouncer announcer,
                               final AnalysisParametersView.Appearance appearance) {
            this.value = value;
            this.eventBus = eventBus;
            this.announcer = announcer;
            this.appearance = appearance;
        }

        @Override
        public void onFailure(Integer statusCode, Throwable caught) {
            final SafeHtml message = SafeHtmlUtils.fromTrustedString(appearance.diskResourceDoesNotExist(value.getDisplayValue()));
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 3000));
        }

        @Override
        public void onSuccess(FastMap<DiskResource> result) {
            eventBus.fireEvent(new ShowFilePreviewEvent((File)result.get(value.getDisplayValue()),
                                                        null));
        }
    }

    private static class SaveAnalysisParametersCallback extends DataCallback<File> {
        private final  IplantAnnouncer announcer;
        private final AnalysisParametersView.Appearance appearance;
        private final DiskResourceUtil diskResourceUtil;
        private final EventBus eventBus;
        private final NotificationAutoBeanFactory notificationFactory;
        private final UserInfo userInfo;
        private final UserSessionServiceFacade userSessionService;
        private final SaveAnalysisParametersEvent event;

        private SaveAnalysisParametersCallback(final IplantAnnouncer announcer,
                                               final AnalysisParametersView.Appearance appearance,
                                               final DiskResourceUtil diskResourceUtil,
                                               final EventBus eventBus,
                                               final NotificationAutoBeanFactory notificationFactory,
                                               final UserInfo userInfo,
                                               final UserSessionServiceFacade userSessionService,
                                               final SaveAnalysisParametersEvent event) {
            this.announcer = announcer;
            this.appearance = appearance;
            this.diskResourceUtil = diskResourceUtil;
            this.eventBus = eventBus;
            this.notificationFactory = notificationFactory;
            this.userInfo = userInfo;
            this.userSessionService = userSessionService;
            this.event = event;
        }

        @Override
        public void onFailure(Integer statusCode, Throwable caught) {
            ErrorHandler.post(caught);
        }

        @Override
        public void onSuccess(final File file) {
            eventBus.fireEvent(new FileSavedEvent(file));

            // Create notification message
            Notification notification = notificationFactory.getNotification().as();
            notification.setCategory("data");
            notification.setUser(userInfo.getUsername());
            notification.setSubject(file.getName().isEmpty() ? appearance.importFailed(event.getPath())
                                                             : appearance.fileUploadSuccess(file.getName()));

            // Create Notification payload and attach message
            PayloadData payloadData = notificationFactory.getNotificationPayloadData().as();
            payloadData.setAction("file_uploaded");
            file.setParentFolderId(diskResourceUtil.parseParent(file.getPath()));
            file.setSourceUrl(event.getPath());
            payloadData.setData(file);
            Splittable payloadSplittable = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(payloadData));
            notification.setNotificationPayload(payloadSplittable);

            userSessionService.postClientNotification(notification,
                                                      new AsyncCallback<String>() {
                          @Override
                          public void onFailure(Throwable caught) {
                              event.getHideable().hide();
                              announcer.schedule(new ErrorAnnouncementConfig(caught.getMessage()));
                          }

                          @Override
                          public void onSuccess(String result) {
                              event.getHideable().hide();
                              announcer.schedule(new SuccessAnnouncementConfig(appearance.importRequestSubmit(file.getName())));
                          }
                      });
        }
    }

    private static class AnalysisParameterKeyProvider implements ModelKeyProvider<AnalysisParameter> {
        @Override
        public String getKey(AnalysisParameter item) {
            return item.getId() + item.getDisplayValue();
        }
    }

    @Inject AsyncProviderWrapper<FileEditorServiceFacade> fileEditorServiceAsyncProvider;
    @Inject AsyncProviderWrapper<DiskResourceServiceFacade> diskResourceServiceAsyncProvider;
    @Inject AnalysisServiceFacade analysisService;
    @Inject EventBus eventBus;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject UserInfo userInfo;
    @Inject UserSessionServiceFacade userSessionService;
    @Inject IplantAnnouncer announcer;
    @Inject AnalysisParametersView.Presenter.BeanFactory factory;
    @Inject NotificationAutoBeanFactory notificationFactory;

    private final AnalysisParametersView.Appearance appearance;
    private final ListStore<AnalysisParameter> listStore;
    private final AnalysisParametersView view;

    @Inject
    AnalysisParametersPresenterImpl(final AnalysisParamViewFactory viewFactory,
                                    final AnalysisParametersView.Appearance appearance){
        this.appearance = appearance;
        this.listStore = new ListStore<>(new AnalysisParameterKeyProvider());
        this.view = viewFactory.createParamView(listStore);

        this.view.addSaveAnalysisParametersEventHandler(this);
        this.view.addAnalysisParamValueSelectedEventHandler(this);
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void fetchAnalysisParameters(Analysis analysis) {
        view.mask(appearance.retrieveParametersLoadingMask());
        analysisService.getAnalysisParams(analysis, new AnalysisCallback<List<AnalysisParameter>>() {
            @Override
            public void onFailure(Integer statusCode, Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<AnalysisParameter> result) {
                listStore.addAll(result);
                view.unmask();
            }
        });
    }

    @Override
    public void onAnalysisParamValueSelected(AnalysisParamValueSelectedEvent event) {
        final AnalysisParameter value = event.getValue();

        if (!((Input.equals(value.getType())
                   || FileInput.equals(value.getType())
                   || FolderInput.equals(value.getType())
                   || FileFolderInput.equals(value.getType())
                   || MultiFileSelector.equals(value.getType())))) {
            return;
        }
        String infoType = value.getInfoType();
        if (infoType.equalsIgnoreCase("ReferenceGenome")
                || infoType.equalsIgnoreCase("ReferenceSequence")
                || infoType.equalsIgnoreCase("ReferenceAnnotation")) {
            return;
        }

        final File hasPath = factory.file().as();
        hasPath.setPath(value.getDisplayValue());
        final FastMap<TYPE> typeFastMap = diskResourceUtil.asStringPathTypeMap(Lists.newArrayList(hasPath),
                                                                               TYPE.FILE);
        diskResourceServiceAsyncProvider.get(new AsyncCallback<DiskResourceServiceFacade>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(DiskResourceServiceFacade service) {
                service.getStat(typeFastMap,
                                new GetStatCallback(value, eventBus, announcer, appearance));

            }
        });
    }

    @Override
    public void onRequestSaveAnalysisParameters(final SaveAnalysisParametersEvent event) {
        fileEditorServiceAsyncProvider.get(new AsyncCallback<FileEditorServiceFacade>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(FileEditorServiceFacade service) {
                service.uploadTextAsFile(event.getPath(),
                                         event.getFileContents(),
                                         true,
                                         new SaveAnalysisParametersCallback(announcer,
                                                                            appearance,
                                                                            diskResourceUtil,
                                                                            eventBus,
                                                                            notificationFactory,
                                                                            userInfo,
                                                                            userSessionService,
                                                                            event));

            }
        });
    }
}
