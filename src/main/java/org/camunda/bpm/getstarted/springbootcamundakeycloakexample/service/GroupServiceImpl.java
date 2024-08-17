package org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class GroupServiceImpl implements GroupService {

    // Define a set of valid group names
    private static final Set<String> INVALID_GROUPS = Set.of(
            "create-realm",
            "default-roles-master",
            "offline_access",
            "admin",
            "uma_authorization"
            // Add other valid group names here
    );

    @Override
    public void processGroups(ProcessEngine engine, OidcUser oidcUser) {
        IdentityService identityService = engine.getIdentityService();
        List<String> userGroups = (List<String>) oidcUser.getClaims().get("groups");

        if (userGroups != null) {
            for (String groupName : userGroups) {
                if (groupName == null || groupName.isEmpty()) {
                    continue;
                }

                // Debugging: Log the group names being processed
                System.out.println("Processing group: " + groupName);

                // Check if the group is in the set of valid groups
                if (!INVALID_GROUPS.contains(groupName)) {
                    // Check if the group exists in Camunda, create if not
                    if (identityService.createGroupQuery().groupId(groupName).count() == 0) {
                        System.out.println("Creating group: " + groupName);
                        identityService.saveGroup(identityService.newGroup(groupName));
                    } else {
                        System.out.println("Group already exists: " + groupName);
                    }

                    // Assign user to the group
                    if (identityService.createGroupQuery().groupId(groupName).groupMember(oidcUser.getName()).count() == 0) {
                        System.out.println("Adding user to group: " + groupName);
                        identityService.createMembership(oidcUser.getName(), groupName);
                    } else {
                        System.out.println("User already a member of group: " + groupName);
                    }
                } else {
                    System.out.println("Ignoring group: " + groupName);
                }
            }
        } else {
            System.out.println("No groups found for user.");
        }
    }
}
