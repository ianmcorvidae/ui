/**
 *
 */
package org.iplantc.de.client.models.tool;

import org.iplantc.de.client.models.errorHandling.SimpleServiceError;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

/**
 * @author sriram
 */
public interface ToolAutoBeanFactory extends AutoBeanFactory {

    AutoBean<Tool> getTool();

    AutoBean<ToolList> getToolList();

    AutoBean<ToolContainer> getContainer();

    AutoBean<ToolDevice> getDevice();

    AutoBean<ToolVolume> getVolume();

    AutoBean<ToolVolumesFrom> getVolumesFrom();

    AutoBean<ToolImage> getImage();

    AutoBean<ToolImplementation> getImplementation();

    AutoBean<ToolTestData> getTest();

    AutoBean<SimpleServiceError> simpleServiceError();

    /**
     * Build a tool with default values
     *
     * @return  a tool
     */
    default AutoBean<Tool> getDefaultTool() {
        Tool tool = getTool().as();
        ToolImage image = getImage().as();
        ToolContainer container = getContainer().as();

        container.setImage(image);
        tool.setContainer(container);

        return AutoBeanUtils.getAutoBean(tool);
    }

}
