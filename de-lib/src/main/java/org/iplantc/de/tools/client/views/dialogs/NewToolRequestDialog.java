/**
 *
 */
package org.iplantc.de.tools.client.views.dialogs;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tools.client.gin.factory.NewToolRequestFormPresenterFactory;
import org.iplantc.de.tools.client.views.requests.NewToolRequestFormView;
import org.iplantc.de.tools.shared.ToolsModule;

import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * @author sriram
 *
 */
public class NewToolRequestDialog extends IPlantDialog {


    private Tool tool;
    private final NewToolRequestFormView.Presenter presenter;
    private final NewToolRequestFormView.NewToolRequestFormViewAppearance appearance;

    @Inject
    NewToolRequestDialog(final NewToolRequestFormPresenterFactory presenterFactory,
                         NewToolRequestFormView.NewToolRequestFormViewAppearance appearance) {
        this.appearance = appearance;
        setPixelSize(480, 400);
        this.setResizable(false);
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setHideOnButtonClick(false);
        setOkButtonText(I18N.DISPLAY.submit());
        presenter = presenterFactory.createPresenter(() -> {
            hide();
        });
        presenter.go(this);
        addOkButtonSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                presenter.onSubmitBtnClick();
            }
        });
        
        addCancelButtonSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                presenter.onCancelBtnClick();
            }
        });

    }


    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        presenter.setViewDebugId(baseID + ToolsModule.RequestToolIds.TOOL_REQUEST_VIEW);
    }

    public void show(NewToolRequestFormView.Mode mode) {
        presenter.setMode(mode);
         switch (mode) {
             case NEWTOOL:
                 setHeading(appearance.newToolRequest());
                 break;
             case MAKEPUBLIC:
                 setHeading(appearance.makePublicRequest());
                 break;
         }
        super.show();
    }

    @Override
    public void show() {
        throw new UnsupportedOperationException("Method not supported!");
    }

    public void setTool(Tool tool) {
        this.tool = tool;
        presenter.setTool(tool);
   }

}

