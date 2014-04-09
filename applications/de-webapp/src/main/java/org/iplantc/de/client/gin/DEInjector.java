package org.iplantc.de.client.gin;

import org.iplantc.de.analysis.client.gin.AnalysisInjector;
import org.iplantc.de.analysis.client.gin.AnalysisModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * Discovery Environment GinJector
 * Created by jstroot on 4/9/14.
 */
@GinModules({DEGinModule.class, AnalysisModule.class})
public interface DEInjector extends Ginjector, AnalysisInjector {
    public static final DEInjector INSTANCE = GWT.create(DEInjector.class);


}
