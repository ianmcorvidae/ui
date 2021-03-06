package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.commons.client.presenter.SharingPresenter;

import java.util.List;

/**
 * @author aramsey
 */
public interface AppSharingPresenterFactory {
    SharingPresenter create(List<App> apps);
}
