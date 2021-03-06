package org.iplantc.de.apps.integration.client.view.propertyEditors;

import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids;
import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.PropertyPanelIds;

import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.FileInfoType;
import org.iplantc.de.client.services.AppBuilderMetadataServiceFacade;
import org.iplantc.de.commons.client.validators.CmdLineArgCharacterValidator;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class FolderInputPropertyEditor extends AbstractArgumentPropertyEditor {

    interface EditorDriver extends SimpleBeanEditorDriver<Argument, FolderInputPropertyEditor> {
    }

    interface FolderInputPropertyEditorUiBinder extends UiBinder<Widget, FolderInputPropertyEditor> {
    }

    @UiField(provided = true) PropertyEditorAppearance appearance;
    @UiField @Path("name") TextField argumentOptionEditor;
    @UiField FieldLabel argumentOptionLabel, toolTipLabel, fileInfoTypeLabel;
    @UiField(provided = true) @Ignore ComboBox<FileInfoType> fileInfoTypeComboBox;
    @UiField TextField label;
    @UiField CheckBoxAdapter requiredEditor, omitIfBlank;
    @UiField @Path("description") TextField toolTipEditor;
    @UiField @Path("fileParameters.implicit") CheckBoxAdapter isImplicit;

    private static FolderInputPropertyEditorUiBinder uiBinder = GWT.create(FolderInputPropertyEditorUiBinder.class);
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Inject
    public FolderInputPropertyEditor(final PropertyEditorAppearance appearance,
                                     final AppBuilderMetadataServiceFacade appMetadataService,
                                     final IplantValidationConstants validationConstants) {
        this.appearance = appearance;
        this.fileInfoTypeComboBox = createFileInfoTypeComboBox(appMetadataService);

        initWidget(uiBinder.createAndBindUi(this));

        argumentOptionEditor.addValidator(new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars()));

        omitIfBlank.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appearance.createContextualHelpLabelNoFloat(appearance.excludeWhenEmpty(), appearance.folderInputExcludeArgument())).toSafeHtml());
        argumentOptionLabel.setHTML(appearance.createContextualHelpLabel(appearance.argumentOption(), appearance.argumentOptionHelp()));
        toolTipLabel.setHTML(appearance.createContextualHelpLabel(appearance.toolTipText(), appearance.toolTip()));
        requiredEditor.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appearance.isRequired()).toSafeHtml());
        isImplicit.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appearance.createContextualHelpLabelNoFloat(appearance.isImplicit(), appearance.fileInputIsImplicit())).toSafeHtml());

        editorDriver.initialize(this);
        editorDriver.accept(new InitializeTwoWayBinding(this));
        ensureDebugId(Ids.PROPERTY_EDITOR + Ids.FOLDER_INPUT);
    }

    @Override
    public void edit(Argument argument) {
        super.edit(argument);
        editorDriver.edit(argument);
    }

    @Override
    public com.google.gwt.editor.client.EditorDriver<Argument> getEditorDriver() {
        return editorDriver;
    }

    @Override
    @Ignore
    protected ComboBox<FileInfoType> getFileInfoTypeComboBox() {
        return fileInfoTypeComboBox;
    }

    @Override
    protected void initLabelOnlyEditMode(boolean isLabelOnlyEditMode) {
        fileInfoTypeComboBox.setEnabled(!isLabelOnlyEditMode);
        argumentOptionEditor.setEnabled(!isLabelOnlyEditMode);
        requiredEditor.setEnabled(!isLabelOnlyEditMode);
        omitIfBlank.setEnabled(!isLabelOnlyEditMode);

        if (isLabelOnlyEditMode) {
            fileInfoTypeComboBox.getValidators().clear();
            argumentOptionEditor.getValidators().clear();
            requiredEditor.getValidators().clear();
            omitIfBlank.getValidators().clear();
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        label.ensureDebugId(baseID + PropertyPanelIds.LABEL);
        argumentOptionEditor.ensureDebugId(baseID + PropertyPanelIds.ARGUMENT_OPTION);
        requiredEditor.ensureDebugId(baseID + PropertyPanelIds.REQUIRED);
        omitIfBlank.ensureDebugId(baseID + PropertyPanelIds.OMIT_IF_BLANK);
        toolTipEditor.ensureDebugId(baseID + PropertyPanelIds.TOOL_TIP);
        fileInfoTypeComboBox.ensureDebugId(baseID + PropertyPanelIds.FILE_INFO_TYPE);
    }

}
