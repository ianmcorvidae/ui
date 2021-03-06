package org.iplantc.de.theme.base.client.notifications;

import com.google.gwt.i18n.client.Messages;

/**
 * @author aramsey
 */
public interface JoinTeamRequestDisplayStrings extends Messages {

    String joinTeamRequestHeader();

    String requesterNameLabel();

    String requesterEmailLabel();

    String requesterMessageLabel();

    String approveBtnText();

    String denyBtnText();

    String teamLabel();

    String joinRequestIntro();

    String setPrivilegesHeading();

    String setPrivilegesText(String requesterName, String teamName);

    String addMemberFail(String requesterName, String teamName);

    String joinTeamSuccess(String requesterName, String teamName);

    String denyRequestHeader();

    String denyRequestLabel(String requesterName);

    String denyRequestMessage(String requesterName, String teamName);

    String denyRequestSuccess(String requesterName, String teamName);

    String denyDetailsHeader();

    String denyDetailsMessage(String teamName);

    String denyAdminLabel();
}
