package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.analysis.Analysis;

import java.util.List;

public interface AnalysisWindowConfig extends WindowConfig {

    List<Analysis> getSelectedAnalyses();
    
    void setSelectedAnalyses(List<Analysis> selectedAnalyses);

    void setFilter(String filter);

    String getFilter();

}
