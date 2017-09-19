package org.iplantc.de.diskResource.client.views.dataLink;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.DataLinkView;
import org.iplantc.de.diskResource.client.events.selection.AdvancedSharingSelected;
import org.iplantc.de.diskResource.client.events.selection.CreateDataLinkSelected;
import org.iplantc.de.diskResource.client.events.selection.DeleteDataLinkSelected;
import org.iplantc.de.diskResource.client.views.dataLink.cells.DataLinkPanelCell;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.List;

/**
 * @author jstroot
 */
public class DataLinkViewImpl implements DataLinkView,
                                         DeleteDataLinkSelected.DeleteDataLinkSelectedHandler {

    /**
     * A handler who controls this widgets button visibility based on tree check selection.
     *
     * @author jstroot
     */
    private final class TreeSelectionHandler implements SelectionHandler<DiskResource> {

        private final HasEnabled advancedDataLinkButton;
        private final HasEnabled copyDataLinkButton;
        private final HasEnabled createBtn;
        private final Tree<DiskResource, DiskResource> tree;

        public TreeSelectionHandler(HasEnabled createBtn, HasEnabled copyDataLinkButton,
                                    HasEnabled advancedDataLinkButton,
                                    Tree<DiskResource, DiskResource> tree) {
            this.createBtn = createBtn;
            this.copyDataLinkButton = copyDataLinkButton;
            this.advancedDataLinkButton = advancedDataLinkButton;
            this.tree = tree;
        }

        @Override
        public void onSelection(SelectionEvent<DiskResource> event) {
            List<DiskResource> selectedItems = tree.getSelectionModel().getSelectedItems();
            boolean createBtnEnabled = selectedItems.size() > 0;
            boolean dataLinkSelected = selectedItems.size() == 1
                                           && (selectedItems.get(0) instanceof DataLink);

            for (DiskResource item : selectedItems) {
                if (item instanceof DataLink) {
                    createBtnEnabled = false;
                    break;
                }
            }

            createBtn.setEnabled(createBtnEnabled);
            copyDataLinkButton.setEnabled(dataLinkSelected);
            advancedDataLinkButton.setEnabled(dataLinkSelected);
        }

    }

    @UiTemplate("DataLinkViewImpl.ui.xml")
    interface DataLinkPanelUiBinder extends UiBinder<Widget, DataLinkViewImpl> { }

    @UiField TextButton advancedDataLinkButton;
    @UiField TextButton collapseAll;
    @UiField TextButton showDataLinkButton;
    @UiField TextButton createDataLinksBtn;
    @UiField TextButton expandAll;
    @UiField TreeStore<DiskResource> store;
    @UiField Tree<DiskResource, DiskResource> tree;

    private static final DataLinkPanelUiBinder uiBinder = GWT.create(DataLinkPanelUiBinder.class);
    private final Widget widget;
    @UiField(provided = true) final Appearance appearance;
    private final DataLinkView.Presenter presenter;

    @Inject
    DataLinkViewImpl(final DataLinkView.Appearance appearance,
                     @Assisted final Presenter presenter,
                     @Assisted final List<DiskResource> sharedResources) {
        this.appearance = appearance;
        this.presenter = presenter;
        widget = uiBinder.createAndBindUi(this);

        // Set the tree's node close/open icons to an empty image. Images for our tree will be controlled
        // from the cell.
        ImageResourcePrototype emptyImgResource = new ImageResourcePrototype("", UriUtils.fromString(""), 0, 0, 0, 0, false, false);
        tree.getStyle().setNodeCloseIcon(emptyImgResource);
        tree.getStyle().setNodeOpenIcon(emptyImgResource);

        tree.getSelectionModel().addSelectionHandler(new TreeSelectionHandler(createDataLinksBtn,
                                                                              showDataLinkButton,
                                                                              advancedDataLinkButton,
                                                                              tree));
        DataLinkPanelCell dataLinkPanelCell = new DataLinkPanelCell();
        dataLinkPanelCell.addDeleteDataLinkSelectedHandler(this);
        dataLinkPanelCell.addDeleteDataLinkSelectedHandler(presenter);
        tree.setCell(dataLinkPanelCell);
        new QuickTip(widget);

    }

    @Override
    public void onDeleteDataLinkSelected(DeleteDataLinkSelected event) {
        showDataLinkButton.setEnabled(false);
    }

    //<editor-fold desc="UI Handlers">
    @UiHandler("advancedDataLinkButton")
    void onAdvancedDataLinkSelected(SelectEvent event) {
        DiskResource selectedItem = tree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            widget.fireEvent(new AdvancedSharingSelected(selectedItem));
        }
    }

    @UiHandler("collapseAll")
    void onCollapseAllSelected(SelectEvent event) {
        tree.collapseAll();
    }

    @UiHandler("showDataLinkButton")
    void onCopyDataLinkButtonSelected(SelectEvent event) {
        // Open dialog window with text selected.
        IPlantDialog dlg = new IPlantDialog();
        dlg.setHeading(appearance.dataLinkTitle());
        dlg.setHideOnButtonClick(true);
        dlg.setResizable(false);
        dlg.setPredefinedButtons(Dialog.PredefinedButton.OK);
        dlg.setSize(appearance.copyDataLinkDlgWidth(), appearance.copyDataLinkDlgHeight());
        TextField textBox = new TextField();
        textBox.setWidth(appearance.copyDataLinkDlgTextBoxWidth());
        textBox.setReadOnly(true);
        textBox.setValue(presenter.getSelectedDataLinkDownloadUrl());
        VerticalLayoutContainer container = new VerticalLayoutContainer();
        dlg.setWidget(container);
        container.add(textBox);
        container.add(new Label(appearance.copyPasteInstructions()));
        dlg.setFocusWidget(textBox);
        dlg.show();
        textBox.selectAll();
    }

    @UiHandler("createDataLinksBtn")
    void onCreateDataLinksSelected(SelectEvent event) {
        List<DiskResource> selectedItems = tree.getSelectionModel().getSelectedItems();
        if (selectedItems != null && !selectedItems.isEmpty()) {
            widget.fireEvent(new CreateDataLinkSelected(selectedItems));
        }
    }

    @UiHandler("expandAll")
    void onExpandAllSelected(SelectEvent event) {
        tree.expandAll();
    }
    //</editor-fold>

    @Override
    public void addRoots(List<DiskResource> roots) {
        store.add(roots);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public Tree<DiskResource, DiskResource> getTree() {
        return tree;
    }

    @Override
    public void mask(String mask) {
        tree.mask(mask);
    }

    @Override
    public void unmask() {
        tree.unmask();
    }

    @UiFactory TreeStore<DiskResource> createTreeStore() {
        return new TreeStore<>(new ModelKeyProvider<DiskResource>() {

            @Override
            public String getKey(DiskResource item) {
                return item.getId();
            }
        });
    }

    @UiFactory ValueProvider<DiskResource, DiskResource> createValueProvider() {
        return new IdentityValueProvider<>();
    }

    @Override
    public HandlerRegistration addCreateDataLinkSelectedHandler(CreateDataLinkSelected.CreateDataLinkSelectedHandler handler) {
        return widget.addHandler(handler, CreateDataLinkSelected.TYPE);
    }

    @Override
    public HandlerRegistration addAdvancedSharingSelectedHandler(AdvancedSharingSelected.AdvancedSharingSelectedHandler handler) {
        return widget.addHandler(handler, AdvancedSharingSelected.TYPE);
    }
}
