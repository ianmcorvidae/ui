package org.iplantc.de.apps.integration.client.presenter.visitors;

import static com.sencha.gxt.dnd.core.client.DND.Feedback.BOTH;
import static org.iplantc.de.apps.integration.client.view.AppsEditorView.Presenter.HANDLERS;

import org.iplantc.de.apps.integration.client.events.DeleteArgumentEvent;
import org.iplantc.de.apps.integration.client.presenter.dnd.ArgListEditorDragSource;
import org.iplantc.de.apps.integration.client.presenter.dnd.ArgListEditorDropTarget;
import org.iplantc.de.apps.integration.client.presenter.dnd.ArgumentWYSIWYGDeleteHandler;
import org.iplantc.de.apps.integration.client.view.AppsEditorView;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.HasAppTemplateSelectedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.ArgumentGroupSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.HasArgumentGroupSelectedHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent.HasArgumentSelectedEventHandlers;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditorFactory;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentGroupEditor;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;

import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorContext;
import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * @author jstroot
 * 
 */
public class InitializeArgumentGroupEventManagement extends EditorVisitor {

    private final ArgumentGroupEditor argumentGroupEditor;
    private final AutoBean<ArgumentGroup> autoBean;
    private final HasLabelOnlyEditMode hasLabelOnlyEditMode;

    public InitializeArgumentGroupEventManagement(final AppsEditorView.AppsEditorViewAppearance appearance,
                                                  final AutoBean<ArgumentGroup> argumentGroupAutoBean,
                                                  final ArgumentGroupEditor argumentGroupEditor,
                                                  final HasLabelOnlyEditMode hasLabelOnlyEditMode,
                                                  final DeleteArgumentEvent.DeleteArgumentEventHandler deleteArgumentEventHandler) {
        this.argumentGroupEditor = argumentGroupEditor;
        this.autoBean = argumentGroupAutoBean;
        this.hasLabelOnlyEditMode = hasLabelOnlyEditMode;
        argumentGroupEditor.showWhenEmptyOrAllInvisible();
        ArgumentWYSIWYGDeleteHandler deleteHandler = new ArgumentWYSIWYGDeleteHandler(appearance, argumentGroupEditor.argumentsEditor(), argumentGroupEditor.getDndContainer(), appearance.getArgListDeleteButton(), hasLabelOnlyEditMode);
        deleteHandler.addDeleteArgumentEventHandler(deleteArgumentEventHandler);
    }

    @Override
    public <T> boolean visit(EditorContext<T> ctx) {
        Editor<T> editor = ctx.getEditor();

        // Init DnD
        if (editor instanceof AppTemplateForm) {
            AppTemplateForm appTemplateForm = (AppTemplateForm)editor;
            ListEditor<Argument, ArgumentEditorFactory> asEditor = argumentGroupEditor.argumentsEditor();
            new ArgListEditorDragSource(argumentGroupEditor.getDndContainer(), asEditor, hasLabelOnlyEditMode);
            ArgListEditorDropTarget argListEditorDropTarget = new ArgListEditorDropTarget(hasLabelOnlyEditMode, argumentGroupEditor.getDndContainer(), asEditor, appTemplateForm.getDndContainer()
                    .getElement());
            argListEditorDropTarget.setFeedback(BOTH);
            argListEditorDropTarget.setAllowSelfAsSource(true);

        }
        if (editor != argumentGroupEditor) {
            /*
             * Add the new ArgumentGroupEditor as a handler to items which are firing events we care
             * about. Store the registrations on the autobean for later removal.
             */
            if (editor instanceof HasArgumentSelectedEventHandlers) {
                HasArgumentSelectedEventHandlers hasArgumentSelectedHandlers = (HasArgumentSelectedEventHandlers)editor;
                HandlerRegistration argSelReg = hasArgumentSelectedHandlers.addArgumentSelectedEventHandler(argumentGroupEditor);
                getAutobeanHandlers(autoBean).add(argSelReg);

            } else if (editor instanceof HasArgumentGroupSelectedHandlers) {
                HasArgumentGroupSelectedHandlers hasArgumentGroupSelectHandlers = (HasArgumentGroupSelectedHandlers)editor;
                HandlerRegistration argGrpSelReg = hasArgumentGroupSelectHandlers.addArgumentGroupSelectedHandler(argumentGroupEditor);
                getAutobeanHandlers(autoBean).add(argGrpSelReg);
            } else if (editor instanceof HasAppTemplateSelectedEventHandlers) {
                HasAppTemplateSelectedEventHandlers hasAppTemplateSelectedHandler = (HasAppTemplateSelectedEventHandlers)editor;
                HandlerRegistration appTemplateSelectedReg = hasAppTemplateSelectedHandler.addAppTemplateSelectedEventHandler(argumentGroupEditor);
                getAutobeanHandlers(autoBean).add(appTemplateSelectedReg);
            }

            // Add select handlers to the new ArgumentGroupEditor.
            if (editor instanceof ArgumentGroupSelectedEventHandler) {
                argumentGroupEditor.addArgumentGroupSelectedHandler((ArgumentGroupSelectedEventHandler)editor);
            }

        }
    
        // If this is not a LeafValueEditor, continue traversing
        return ctx.asLeafValueEditor() == null;
    }

    private List<HandlerRegistration> getAutobeanHandlers(AutoBean<?> autobean) {
        if (autobean.getTag(HANDLERS) == null) {
            autobean.setTag(HANDLERS, Lists.<HandlerRegistration> newArrayList());
        }
        return autobean.getTag(HANDLERS);
    }

}
