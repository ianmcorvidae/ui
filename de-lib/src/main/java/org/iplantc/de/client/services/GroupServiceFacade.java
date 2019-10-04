package org.iplantc.de.client.services;

import org.iplantc.de.client.models.HasMessage;
import org.iplantc.de.client.models.collaborators.Subject;
import org.iplantc.de.client.models.groups.Group;
import org.iplantc.de.client.models.groups.Privilege;
import org.iplantc.de.client.models.groups.PrivilegeType;
import org.iplantc.de.client.models.groups.UpdateMemberResult;
import org.iplantc.de.client.models.groups.UpdatePrivilegeRequestList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

/**
 * @author aramsey
 */
public interface GroupServiceFacade {

    /**
     * Get the list of Collaborator Lists a user has matching the specified search term
     * @param subjectListCallback
     */
    void getLists(AsyncCallback<List<Subject>> subjectListCallback);

    /**
     * Get the list of all Teams for which the user has View permissions
     * @param teamListCallback
     * @param myTeams
     * @param searchTerm
     */
    void getTeams(boolean myTeams, String searchTerm, AsyncCallback<Splittable> teamListCallback);

    /**
     * Get the list of all Communities for which the user has View permissions
     * @param communitiesCallback
     */
    void getCommunities(AsyncCallback<Splittable> communitiesCallback);

    /**
     * Get the list of all Communities for which the user is a member
     * @param communitiesCallback
     */
    void getMyCommunities(AsyncCallback<Splittable> communitiesCallback);
    /**
     * Create a Collaborator List
     * @param collabList
     * @param collabListCallback
     */
    void addList(Group collabList, AsyncCallback<Group> collabListCallback);

    /**
     * Create a Team
     * @param team
     * @param publicPrivileges
     * @param teamCallback
     */
    void addTeam(Group team, List<PrivilegeType> publicPrivileges, AsyncCallback<Group> teamCallback);

    /**
     * Create a Community
     * @param community
     * @param communityCallback
     */
    void addCommunity(Group community, AsyncCallback<Group> communityCallback);

    /**
     * Delete a collaborator list
     * @param collabList
     * @param retainPermissions
     * @param collabCallback
     */
    void deleteList(Group collabList, boolean retainPermissions, AsyncCallback<Group> collabCallback);

    /**
     * Delete a Team
     * @param team
     * @param teamCallback
     */
    void deleteTeam(Group team, AsyncCallback<Group> teamCallback);

    /**
     * Delete a community
     * @param communityName
     * @param communityCallback
     */
    void deleteCommunity(String communityName, AsyncCallback<Group> communityCallback);

    /**
     * Get the list of members who belong to a Collaborator List
     * @param team
     * @param subjectListCallback
     */
    void getListMembers(Group team, AsyncCallback<List<Subject>> subjectListCallback);

    /**
     * Get the list of members who belong to a Team
     * @param team
     * @param subjectListCallback
     */
    void getTeamMembers(Group team, AsyncCallback<List<Subject>> subjectListCallback);

    /**
     * Get the list of members who belong to a Community i.e. those who have "favorited" the community
     * @param communityName
     * @param subjectListCallback
     */
    void getCommunityMembers(String communityName, AsyncCallback<List<Subject>> subjectListCallback);
    /**
     * Get the list of admins for a Community
     * @param communityName
     * @param adminListCallback
     */
    void getCommunityAdmins(String communityName, AsyncCallback<Splittable> adminListCallback);

    /**
     * Remove a list of admins from a community
     * @param communityName
     * @param adminIds
     * @param updateMemberCallback
     */
    void removeCommunityAdmins(String communityName, List<String> adminIds, AsyncCallback<List<UpdateMemberResult>> updateMemberCallback);

    /**
     * Add a list of admins to a community
     * @param communityName
     * @param adminIds
     * @param updateMemberCallback
     */
    void addCommunityAdmins(String communityName, List<String> adminIds, AsyncCallback<List<UpdateMemberResult>> updateMemberCallback);

    /**
     * Delete members from a Collaborator List
     * @param team
     * @param members
     * @param retainPermissions
     * @param updatesCallback
     */
    void deleteListMembers(Group team, List<Subject> members, boolean retainPermissions, AsyncCallback<List<UpdateMemberResult>> updatesCallback);

    /**
     * Delete members from a Team
     * @param team
     * @param member
     * @param retainPermissions
     * @param updatesCallback
     */
    void deleteTeamMembers(Group team, List<Subject> member, boolean retainPermissions, AsyncCallback<List<UpdateMemberResult>> updatesCallback);

    /**
     * Adds  members to the Collaborator List
     * @param collabList
     * @param subjects
     */
    void addMembersToList(Group collabList, List<Subject> subjects, AsyncCallback<List<UpdateMemberResult>> updatesCallback);

    /**
     * Adds  members to the Team
     * @param team
     * @param subjects
     */
    void addMembersToTeam(Group team, List<Subject> subjects, AsyncCallback<List<UpdateMemberResult>> updatesCallback);

    /**
     * Update the details of a Collaborator List
     * @param originalList
     * @param updatedList
     * @param collabListCallback
     */
    void updateList(String originalList, Group updatedList, AsyncCallback<Group> collabListCallback);

    /**
     * Update the details of a Team
     * @param originalTeam
     * @param updatedTeam
     * @param teamCallback
     */
    void updateTeam(String originalTeam, Group updatedTeam, AsyncCallback<Group> teamCallback);

    /**
     * Update the details of a Community
     * @param originalCommunity
     * @param updatedCommunity
     * @param retagApps
     * @param callback
     */
    void updateCommunity(String originalCommunity, Group updatedCommunity, boolean retagApps, AsyncCallback<Group> callback);
    /**
     * Update the privileges on a Team
     * @param team
     * @param updatePrivilegeRequests
     * @param privilegeListCallback
     */
    void updateTeamPrivileges(Group team, UpdatePrivilegeRequestList updatePrivilegeRequests, AsyncCallback<List<Privilege>> privilegeListCallback);

    /**
     * Get the list of privileges on a Team
     * @param team
     * @param privilegeListCallback
     */
    void getTeamPrivileges(Group team, AsyncCallback<List<Privilege>> privilegeListCallback);

    /**
     * Get the list of privileges on a Community
     * @param community
     * @param privilegeListCallback
     */
    void getCommunityPrivileges(Group community, AsyncCallback<List<Privilege>> privilegeListCallback);
    /**
     * With optin privileges, join the team
     * @param team
     * @param updatesCallback
     */
    void joinTeam(Group team, AsyncCallback<List<UpdateMemberResult>> updatesCallback);

    /**
     * Join or "favorite" a community
     * @param communityName
     * @param updatesCallback
     */
    void joinCommunity(String communityName, AsyncCallback<List<UpdateMemberResult>> updatesCallback);
    /**
     * Without optin privileges, request to join the team
     * @param team
     * @param requestMessage
     * @param voidCallback
     */
    void requestToJoinTeam(Group team, HasMessage requestMessage, AsyncCallback<Void> voidCallback);

    /**
     * Deny a user's request to join a team
     * @param team
     * @param denyMessage
     * @param requesterId
     * @param voidCallback
     */
    void denyRequestToJoinTeam(Group team, HasMessage denyMessage, String requesterId, AsyncCallback<Void> voidCallback);

    /**
     * Leave the selected Team
     * @param team
     * @param updatesCallback
     */
    void leaveTeam(Group team, AsyncCallback<List<UpdateMemberResult>> updatesCallback);

    /**
     * Leave the selected Community
     * @param communityName
     * @param updatesCallback
     */
    void leaveCommunity(String communityName, AsyncCallback<List<UpdateMemberResult>> updatesCallback);

}
