package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.shared.AppsCallback;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Created by sriram on 4/28/17.
 */
public class ToolCallbackConverter extends DECallbackConverter<String, Tool> {
    private final ToolAutoBeanFactory factory;

    public ToolCallbackConverter(AppsCallback<Tool> callback, ToolAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }
    @Override
    protected Tool convertFrom(String object) {
        AutoBean<Tool> autoBean = AutoBeanCodex.decode(factory, Tool.class, object);
        return autoBean.as();
    }
}
