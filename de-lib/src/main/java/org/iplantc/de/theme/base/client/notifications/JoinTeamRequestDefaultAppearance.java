package org.iplantc.de.theme.base.client.notifications;

import org.iplantc.de.notifications.client.views.JoinTeamRequestView;

import com.google.gwt.core.client.GWT;

public class JoinTeamRequestDefaultAppearance implements JoinTeamRequestView.JoinTeamRequestAppearance {

    private JoinTeamRequestDisplayStrings displayStrings;

    public JoinTeamRequestDefaultAppearance() {
        this(GWT.create(JoinTeamRequestDisplayStrings.class));
    }

    public JoinTeamRequestDefaultAppearance(JoinTeamRequestDisplayStrings displayStrings) {
        this.displayStrings = displayStrings;
    }

    @Override
    public String joinTeamRequestHeader() {
        return displayStrings.joinTeamRequestHeader();
    }

    @Override
    public int joinTeamRequestWidth() {
        return 400;
    }

    @Override
    public String requesterNameLabel() {
        return displayStrings.requesterNameLabel();
    }

    @Override
    public String requesterEmailLabel() {
        return displayStrings.requesterEmailLabel();
    }

    @Override
    public String requesterMessageLabel() {
        return displayStrings.requesterMessageLabel();
    }

    @Override
    public String approveBtnText() {
        return displayStrings.approveBtnText();
    }

    @Override
    public String denyBtnText() {
        return displayStrings.denyBtnText();
    }

    @Override
    public String teamLabel() {
        return displayStrings.teamLabel();
    }

    @Override
    public String joinRequestIntro() {
        return displayStrings.joinRequestIntro();
    }

    @Override
    public int joinTeamRequestHeight() {
        return 250;
    }

    @Override
    public String buttonBarWidth() {
        return joinTeamRequestWidth() + "px";
    }

    @Override
    public String setPrivilegesHeading() {
        return displayStrings.setPrivilegesHeading();
    }

    @Override
    public String setPrivilegesText(String requesterName, String teamName) {
        return displayStrings.setPrivilegesText(requesterName, teamName);
    }

    @Override
    public int privilegeComboWidth() {
        return 300;
    }

    @Override
    public String addMemberFail(String requesterName, String teamName) {
        return displayStrings.addMemberFail(requesterName, teamName);
    }

    @Override
    public String joinTeamSuccess(String requesterName, String teamName) {
        return displayStrings.joinTeamSuccess(requesterName, teamName);
    }

    @Override
    public int privilegeDlgWidth() {
        return 350;
    }

    @Override
    public String denyRequestHeader() {
        return displayStrings.denyRequestHeader();
    }

    @Override
    public String denyRequestLabel(String requesterName) {
        return displayStrings.denyRequestLabel(requesterName);
    }

    @Override
    public String denyRequestMessage(String requesterName, String teamName) {
        return displayStrings.denyRequestMessage(requesterName, teamName);
    }

    @Override
    public String denyRequestSuccess(String requesterName, String teamName) {
        return displayStrings.denyRequestSuccess(requesterName, teamName);
    }

    @Override
    public String denyDetailsHeader() {
        return displayStrings.denyDetailsHeader();
    }

    @Override
    public String denyDetailsMessage(String teamName) {
        return displayStrings.denyDetailsMessage(teamName);
    }

    @Override
    public String denyAdminLabel() {
        return displayStrings.denyAdminLabel();
    }
}
