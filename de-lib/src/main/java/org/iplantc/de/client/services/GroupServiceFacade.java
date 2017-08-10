package org.iplantc.de.client.services;

import org.iplantc.de.client.models.collaborators.Subject;
import org.iplantc.de.client.models.groups.Group;
import org.iplantc.de.client.models.groups.UpdateMemberResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author aramsey
 */
public interface GroupServiceFacade {

    /**
     * Get the list of Collaborator Lists a user has matching the specified search term
     * @param searchTerm
     * @param callback
     */
    void getLists(AsyncCallback<List<Subject>> callback);

    /**
     * Get the list of all Teams for which the user has View permissions
     * @param callback
     */
    void getTeams(AsyncCallback<List<Group>> callback);

    /**
     * Get the the list of all Teams that a user belongs to
     * @param callback
     */
    void getMyTeams(AsyncCallback<List<Group>> callback);


    /**
     * Create a Collaborator List
     * @param group
     * @param callback
     */
    void addGroup(Group group, AsyncCallback<Group> callback);

    /**
     * Delete a Collaborator List
     * @param group
     * @param retainPermissions
     * @param callback
     */
    void deleteGroup(Group group, boolean retainPermissions, AsyncCallback<Group> callback);

    /**
     * Get the list of members who belong to a Collaborator List
     * @param group
     * @param callback
     */
    void getListMembers(Group group, AsyncCallback<List<Subject>> callback);

    /**
     * Get the list of members who belong to a Team
     * @param group
     * @param callback
     */
    void getTeamMembers(Group group, AsyncCallback<List<Subject>> callback);

    /**
     * Delete members from a Collaborator List
     * @param group
     * @param member
     * @param retainPermissions
     * @param callback
     */
    void deleteMembers(Group group, List<Subject> member, boolean retainPermissions, AsyncCallback<List<UpdateMemberResult>> callback);

    /**
     * Adds  members to the Collaborator List
     * @param group
     * @param subjects
     */
    void addMembers(Group group, List<Subject> subjects, AsyncCallback<List<UpdateMemberResult>> callback);

    /**
     * Update the details of a Collaborator List
     * @param originalGroup
     * @param group
     * @param callback
     */
    void updateGroup(String originalGroup, Group group, AsyncCallback<Group> callback);

}
