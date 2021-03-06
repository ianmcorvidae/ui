package org.iplantc.de.diskResource.client.presenters.metadata;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataList;
import org.iplantc.de.client.models.diskResources.MetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateAttribute;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.events.selection.ImportMetadataBtnSelected;
import org.iplantc.de.diskResource.client.events.selection.MetadataInfoBtnSelected;
import org.iplantc.de.diskResource.client.events.selection.SaveMetadataSelected;
import org.iplantc.de.diskResource.client.events.selection.SaveMetadataToFileBtnSelected;
import org.iplantc.de.diskResource.client.events.selection.SelectTemplateBtnSelected;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMetadataUpdateCallback;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.MetadataTemplateDescDlg;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.MetadataTemplateViewDialog;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.SelectMetadataTemplateDialog;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jstroot sriram
 */
public class MetadataPresenterImpl implements MetadataView.Presenter,
                                              MetadataInfoBtnSelected.MetadataInfoBtnSelectedHandler,
                                              SelectTemplateBtnSelected.SelectTemplateBtnSelectedHandler,
                                              ImportMetadataBtnSelected.ImportMetadataBtnSelectedHandler,
                                              SaveMetadataToFileBtnSelected.SaveMetadataToFileBtnSelectedHandler {

    private class TemplateViewCancelSelectHandler implements SelectEvent.SelectHandler {

        private MetadataTemplateViewDialog metadataTemplateDlg;

        public TemplateViewCancelSelectHandler(MetadataTemplateViewDialog metadataTemplateDlg) {
            this.metadataTemplateDlg = metadataTemplateDlg;
        }

        @Override
        public void onSelect(SelectEvent event) {
            metadataTemplateDlg.hide();
        }
    }

    private class TemplateViewOkSelectHandler implements SelectEvent.SelectHandler {

        private MetadataTemplateViewDialog metadataTemplateDlg;
        private boolean writable;

        public TemplateViewOkSelectHandler(boolean writable, MetadataTemplateViewDialog metadataTemplateDlg) {
            this.writable = writable;
            this.metadataTemplateDlg = metadataTemplateDlg;
        }

        @Override
        public void onSelect(SelectEvent event) {
            if (!writable) {
                return;
            }

            if (!metadataTemplateDlg.isValid()) {
                ConfirmMessageBox cmb =
                        new ConfirmMessageBox(appearance.error(), appearance.incomplete());
                cmb.addDialogHideHandler(new DialogHideHandler() {

                    @Override
                    public void onDialogHide(DialogHideEvent event) {
                        if (event.getHideButton().equals(PredefinedButton.YES)) {
                            updateMetadataFromTemplateView();
                        }

                    }
                });
                cmb.show();
            } else {
                updateMetadataFromTemplateView();
            }
        }

        private void updateMetadataFromTemplateView() {
            metadataTemplateDlg.mask(appearance.loadingMask());
            ArrayList<Avu> mdList = metadataTemplateDlg.getMetadataFromTemplate();
            view.updateMetadataFromTemplateView(mdList, templateAttributes);
        }
    }

    private DiskResource resource;
    private final MetadataView view;
    private final DiskResourceServiceFacade drService;
    List<MetadataTemplateInfo> templates;

    private List<Avu> userMdList;
    private List<MetadataTemplateAttribute> templateAttributes;

    private static DiskResourceAutoBeanFactory autoBeanFactory =
            GWT.create(DiskResourceAutoBeanFactory.class);
    @Inject MetadataView.Presenter.Appearance appearance;

    @Inject AsyncProviderWrapper<MetadataTemplateViewDialog> templateViewDialogProvider;
    @Inject AsyncProviderWrapper<SelectMetadataTemplateDialog> selectMetaTemplateDlgProvider;
    @Inject AsyncProviderWrapper<MetadataTemplateDescDlg> metadataTemplateDescDlgProvider;
    MetadataTemplateViewDialog templateViewDialog;
    @Inject EventBus eventBus;

    @Inject
    public MetadataPresenterImpl(final MetadataView view,
                                 final DiskResourceServiceFacade drService) {

        this.view = view;
        this.drService = drService;

        view.addImportMetadataBtnSelectedHandler(this);
        view.addSelectTemplateBtnSelectedHandler(this);
        view.addSaveMetadataToFileBtnSelectedHandler(this);
    }

    void loadMetadata() {
        drService.getDiskResourceMetaData(resource, new DiskResourceMetadataListAsyncCallback());
    }

    @Override
    public void go(HasOneWidget container, final DiskResource selected) {
        this.resource = selected;
        view.init(DiskResourceUtil.getInstance().isWritable(selected));
        container.setWidget(view);
        view.mask();
        drService.getMetadataTemplateListing(new AsyncCallback<List<MetadataTemplateInfo>>() {
            @Override
            public void onFailure(Throwable arg0) {
                view.unmask();
                ErrorHandler.post(appearance.templateListingError(), arg0);
            }

            @Override
            public void onSuccess(final List<MetadataTemplateInfo> result) {
                templates = result;
                loadMetadata();
            }
        });
    }

    @Override
    public void setDiskResourceMetadata(final DiskResourceMetadataUpdateCallback callback) {
        AsyncCallback<String> batchAvuCallback = new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        };

        DiskResourceMetadataList umd = autoBeanFactory.metadataList().as();

        umd.setAvus(view.getUserMetadata());
        umd.setOtherMetadata(view.getAvus());
        drService.setDiskResourceMetaData(resource, umd, batchAvuCallback);
    }

    @Override
    public void onSelectTemplateBtnSelected(SelectTemplateBtnSelected event) {
        selectMetaTemplateDlgProvider.get(new AsyncCallback<SelectMetadataTemplateDialog>() {
            @Override
            public void onFailure(Throwable throwable) {}

            @Override
            public void onSuccess(SelectMetadataTemplateDialog dialog) {
                dialog.addDialogHideHandler(hideEvent -> {
                                            PredefinedButton hideButton = hideEvent.getHideButton();
                                            if (hideButton != null && hideButton.equals(PredefinedButton.OK)) {
                                                MetadataTemplateInfo selectedTemplate = dialog.getSelectedTemplate();
                                                if (selectedTemplate != null) {
                                                    onTemplateSelected(selectedTemplate.getId());
                                                }
                                            }});
                dialog.show(templates, true);
                dialog.addMetadataInfoBtnSelectedHandler(MetadataPresenterImpl.this);
            }
        });
    }

    @Override
    public void onMetadataInfoBtnSelected(MetadataInfoBtnSelected event) {
        metadataTemplateDescDlgProvider.get(new AsyncCallback<MetadataTemplateDescDlg>() {
            @Override
            public void onFailure(Throwable caught) {}

            @Override
            public void onSuccess(MetadataTemplateDescDlg result) {
                MetadataTemplateInfo info = event.getTemplateInfo();
                result.show(info);
            }
        });
    }

    public void onTemplateSelected(String templateId) {
        drService.getMetadataTemplate(templateId, new MetadataTemplateAsyncCallback());
    }

    @Override
    public DiskResource getSelectedResource() {
        return resource;
    }

    @Override
    public void onImportMetadataBtnSelected(ImportMetadataBtnSelected event) {
        List<Avu> selectedItems = event.getAvuList();
        ConfirmMessageBox cmb = new ConfirmMessageBox(appearance.importMd(), appearance.importMdMsg());
        cmb.addDialogHideHandler(new DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                switch (event.getHideButton()) {
                    case YES:
                        view.mask();
                        view.addToUserMetadata(selectedItems);
                        view.removeImportedMetadataFromStore(selectedItems);
                        view.unmask();
                        break;
                    case NO:
                        break;
                    default:
                        //error, button added with no specific action ready
                }
            }
        });
        cmb.show();
    }

    @Override
    public boolean isDirty() {
        List<Avu> userMetadata = view.getUserMetadata();
        if (userMdList != null && userMetadata != null && userMdList.size() != userMetadata.size()) {
            return true;
        } else {
            return view.isDirty();
        }

    }

    @Override
    public void downloadTemplate(String templateId) {
        final String encodedSimpleDownloadURL = drService.downloadTemplate(templateId);
        WindowUtil.open(encodedSimpleDownloadURL, "width=100,height=100");
    }

    @Override
    public void onSaveMetadataToFileBtnSelected(SaveMetadataToFileBtnSelected event) {
        eventBus.fireEvent(new SaveMetadataSelected(resource));
    }

    @Override
    public void setViewDebugId(String debugId) {
        view.asWidget().ensureDebugId(debugId);
    }

    @Override
    public boolean isValid() {
        return view.isValid();
    }

    private class DiskResourceMetadataListAsyncCallback
            implements AsyncCallback<DiskResourceMetadataList> {
        @Override
        public void onSuccess(final DiskResourceMetadataList result) {
            view.loadMetadata(result.getOtherMetadata());
            userMdList = result.getAvus();
            if (userMdList != null) {
                if (templates != null && !templates.isEmpty()) {
                    view.loadUserMetadata(userMdList);
                }
            }

            view.unmask();
        }


        @Override
        public void onFailure(Throwable caught) {
            view.unmask();
            ErrorHandler.post(caught);
        }
    }

    private class MetadataTemplateAsyncCallback implements AsyncCallback<MetadataTemplate> {

        @Override
        public void onFailure(Throwable arg0) {
            ErrorHandler.post(appearance.templateinfoError(), arg0);
        }

        @Override
        public void onSuccess(MetadataTemplate result) {
            //close exisitng view before opening one...
            if (templateViewDialog != null) {
                templateViewDialog.hide();
            }
            templateAttributes = result.getAttributes();
            templateViewDialogProvider.get(new AsyncCallback<MetadataTemplateViewDialog>() {
                @Override
                public void onFailure(Throwable throwable) {
                    ErrorHandler.post(throwable);
                }

                @Override
                public void onSuccess(MetadataTemplateViewDialog dialog) {
                    templateViewDialog = dialog;
                    templateViewDialog.addOkButtonSelectHandler(new TemplateViewOkSelectHandler(isWritable(),
                                                                                                templateViewDialog));
                    templateViewDialog.addCancelButtonSelectHandler(new TemplateViewCancelSelectHandler(
                            templateViewDialog));
                    templateViewDialog.setHeading(result.getName());
                    templateViewDialog.setModal(false);
                    templateViewDialog.show(view.getUserMetadata(),
                                            isWritable(),
                                            templateAttributes);
                }
            });
        }

        private boolean isWritable() {
            return DiskResourceUtil.getInstance().isWritable(resource);
        }
    }
}
