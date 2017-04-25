package org.iplantc.de.theme.base.client.collaborators;

import org.iplantc.de.client.models.groups.Group;
import org.iplantc.de.collaborators.client.GroupView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author aramsey
 */
public class GroupViewDefaultAppearance implements GroupView.GroupViewAppearance {

    private IplantDisplayStrings iplantDisplayStrings;
    private IplantResources iplantResources;
    private GroupDisplayStrings displayStrings;

    public GroupViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings>create(IplantDisplayStrings.class),
             GWT.<IplantResources>create(IplantResources.class),
             GWT.<GroupDisplayStrings>create(GroupDisplayStrings.class));
    }

    public GroupViewDefaultAppearance(IplantDisplayStrings iplantDisplayStrings,
                                      IplantResources iplantResources,
                                      GroupDisplayStrings displayStrings) {

        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
        this.displayStrings = displayStrings;
    }

    @Override
    public String addGroup() {
        return iplantDisplayStrings.add();
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public String deleteGroup() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.delete();
    }

    @Override
    public int nameColumnWidth() {
        return 300;
    }

    @Override
    public String nameColumnLabel() {
        return iplantDisplayStrings.name();
    }

    @Override
    public int descriptionColumnWidth() {
        return 300;
    }

    @Override
    public String descriptionColumnLabel() {
        return iplantDisplayStrings.description();
    }

    @Override
    public String noCollabLists() {
        return displayStrings.noCollabLists();
    }

    @Override
    public String groupNameLabel() {
        return displayStrings.groupNameLabel();
    }

    @Override
    public String groupDescriptionLabel() {
        return displayStrings.groupDescriptionLabel();
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public String noCollaborators() {
        return iplantDisplayStrings.noCollaborators();
    }

    @Override
    public int groupDetailsWidth() {
        return 500;
    }

    @Override
    public int groupDetailsHeight() {
        return 500;
    }

    @Override
    public String groupDetailsHeading(Group group) {
        if (group == null) {
            return displayStrings.newGroupDetailsHeading();
        } else {
            return displayStrings.editGroupDetailsHeading(group.getName());
        }
    }
}
