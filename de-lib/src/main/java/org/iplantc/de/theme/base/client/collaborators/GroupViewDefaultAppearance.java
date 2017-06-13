package org.iplantc.de.theme.base.client.collaborators;

import org.iplantc.de.client.models.collaborators.Subject;
import org.iplantc.de.client.models.groups.Group;
import org.iplantc.de.client.models.groups.UpdateMemberResult;
import org.iplantc.de.collaborators.client.GroupView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.XTemplates;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aramsey
 */
public class GroupViewDefaultAppearance implements GroupView.GroupViewAppearance {

    interface Templates extends XTemplates {
        @XTemplates.XTemplate("<span style='color: red;'>*&nbsp</span>{label}")
        SafeHtml requiredFieldLabel(String label);
    }

    private IplantDisplayStrings iplantDisplayStrings;
    private IplantErrorStrings iplantErrorStrings;
    private IplantResources iplantResources;
    private GroupDisplayStrings displayStrings;
    private Templates templates;

    public GroupViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings>create(IplantDisplayStrings.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class),
             GWT.<IplantResources>create(IplantResources.class),
             GWT.<GroupDisplayStrings>create(GroupDisplayStrings.class),
             GWT.<Templates> create(Templates.class));
    }

    public GroupViewDefaultAppearance(IplantDisplayStrings iplantDisplayStrings,
                                      IplantErrorStrings iplantErrorStrings,
                                      IplantResources iplantResources,
                                      GroupDisplayStrings displayStrings,
                                      Templates templates) {

        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantErrorStrings = iplantErrorStrings;
        this.iplantResources = iplantResources;
        this.displayStrings = displayStrings;
        this.templates = templates;
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
    public SafeHtml groupNameLabel() {
        return templates.requiredFieldLabel(displayStrings.groupNameLabel());
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
        if (group == null || Strings.isNullOrEmpty(group.getName())) {
            return displayStrings.newGroupDetailsHeading();
        } else {
            return displayStrings.editGroupDetailsHeading(group.getName());
        }
    }

    @Override
    public String completeRequiredFieldsError() {
        return iplantDisplayStrings.completeRequiredFieldsError();
    }

    @Override
    public String deleteGroupConfirmHeading(Group group) {
        return displayStrings.deleteGroupConfirmHeading(group.getName());
    }

    @Override
    public String deleteGroupConfirm(Group group) {
        return displayStrings.deleteGroupConfirm(group.getName());
    }

    @Override
    public String groupDeleteSuccess(Group group) {
        return displayStrings.groupDeleteSuccess(group.getName());
    }

    @Override
    public String unableToAddMembers(List<UpdateMemberResult> failures) {
        List<String> memberNames = failures.stream().map(UpdateMemberResult::getSubjectName).collect(
                Collectors.toList());
        return displayStrings.unableToAddMembers(memberNames);
    }

    @Override
    public String loadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String groupCreatedSuccess(Group group) {
        return displayStrings.groupCreatedSuccess(group.getName());
    }

    @Override
    public String memberDeleteFail(List<UpdateMemberResult> subjects) {
        List<String> memberNames = subjects.stream().map(UpdateMemberResult::getSubjectName).collect(
                Collectors.toList());
        return displayStrings.memberDeleteFail(memberNames);
    }

    @Override
    public String collaboratorsSelfAdd() {
        return iplantDisplayStrings.collaboratorSelfAdd();
    }

    @Override
    public String groupSelfAdd() {
        return displayStrings.groupSelfAdd();
    }

    @Override
    public String memberAddSuccess(Subject subject, Group group) {
        return displayStrings.memberAddSuccess(subject.getSubjectDisplayName(), group.getName());
    }

    @Override
    public String noCollabListSelected() {
        return displayStrings.noCollabListSelected();
    }

    @Override
    public String collaboratorRemoveConfirm(String names) {
        return iplantDisplayStrings.collaboratorRemoveConfirm(names);
    }

    @Override
    public String collaboratorAddConfirm(String names) {
        return iplantDisplayStrings.collaboratorAddConfirm(names);
    }

    @Override
    public String addCollabErrorMsg() {
        return iplantErrorStrings.addCollabErrorMsg();
    }

    @Override
    public String memberAddToGroupsSuccess(Subject subject) {
        return displayStrings.memberAddToGroupsSuccess(subject.getSubjectDisplayName());
    }
}