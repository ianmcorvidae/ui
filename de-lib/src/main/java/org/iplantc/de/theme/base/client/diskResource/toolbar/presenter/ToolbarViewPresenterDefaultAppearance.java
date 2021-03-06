package org.iplantc.de.theme.base.client.diskResource.toolbar.presenter;

import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;
import org.iplantc.de.theme.base.client.diskResource.toolbar.ToolbarDisplayMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class ToolbarViewPresenterDefaultAppearance implements ToolbarView.Presenter.Appearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final ToolbarDisplayMessages displayMessages;
    private final DiskResourceMessages diskResourceMessages;

    public ToolbarViewPresenterDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<ToolbarDisplayMessages> create(ToolbarDisplayMessages.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    ToolbarViewPresenterDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                          final ToolbarDisplayMessages displayMessages,
                                          final DiskResourceMessages diskResourceMessages) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayMessages = displayMessages;
        this.diskResourceMessages = diskResourceMessages;
    }

    @Override
    public String emptyTrash() {
        return diskResourceMessages.emptyTrash();
    }

    @Override
    public String emptyTrashWarning() {
        return diskResourceMessages.emptyTrashWarning();
    }

    @Override
    public String importFromCoge() {
        return diskResourceMessages.importFromCoge();
    }

    @Override
    public String cogeSearchError() {
        return diskResourceMessages.cogeSearchError();
    }

    @Override
    public String cogeImportGenomeError() {
        return diskResourceMessages.cogeImportGenomeError();
    }

    @Override
    public String cogeImportGenomeSucess() {
        return diskResourceMessages.cogeImportGenomeSuccess();
    }

    @Override
    public String templatesError() {
        return diskResourceMessages.templatesError();
    }

    public String overWiteMetadata() {
        return diskResourceMessages.overWiteMetadata();
    }

    @Override
    public String doiRequestFail() {
        return diskResourceMessages.doiRequestFail();
    }

    @Override
    public String doiRequestSuccess() {
        return diskResourceMessages.doiRequestSuccess();
    }

    public String applyBulkMetadata() {
        return diskResourceMessages.applyBulkMetadata();
    }

    public String selectMetadataFile() {
        return displayMessages.selectMetadataFile();
    }
}
