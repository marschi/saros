/*
 * DPP - Serious Distributed Pair Programming
 * (c) Freie Universitaet Berlin - Fachbereich Mathematik und Informatik - 2006
 * (c) Riad Djemili - 2006
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package de.fu_berlin.inf.dpp;

import java.util.Date;

import org.apache.log4j.Logger;

import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.project.ISharedProject;
import de.fu_berlin.inf.dpp.util.StackTrace;
import de.fu_berlin.inf.dpp.util.Util;

/**
 * A user is a representation of a person sitting in front of an eclipse
 * instance for the use in a Saros SharedProject session.
 * 
 * A user object always has the following immutable characteristics: S/he
 * belongs to a single SharedProject instance, has a final color, and unchanging
 * JID.
 * 
 * There is one user who is a host, all others are clients.
 * 
 * There is one local user representing the person in front of the current
 * eclipse instance, all others are remote users.
 * 
 * The public and mutable properties are the role (Driver/Observer), time since
 * going off-line, connection state, away information and whether this user is
 * still joining or already part of the Session.
 * 
 * @entityObject A user is a entity object, i.e. it can change over time.
 */
public class User {

    private static final Logger log = Logger.getLogger(User.class.getName());

    public enum UserConnectionState {
        UNKNOWN, ONLINE, OFFLINE
    }

    public enum UserRole {
        DRIVER, OBSERVER
    }

    protected final ISharedProject sharedProject;

    protected final JID jid;

    protected final int colorID;

    /**
     * The {@link #isInvitationComplete()} status is always false if a new
     * {@link User} is created. It can be changed only once by calling
     * {@link #invitationCompleted()}. The reason is: if a {@link User} is
     * already in the session, he can not leave and join again. In this case a
     * new {@link User} object will be created.
     */
    protected boolean invitationComplete = false;

    protected UserConnectionState connectionState = UserConnectionState.UNKNOWN;

    protected boolean away = false;

    /**
     * Time stamp when User became offline the last time. In seconds.
     */
    protected long offlineTime = 0;

    protected UserRole role = UserRole.OBSERVER;

    public User(ISharedProject sharedProject, JID jid, int colorID) {
        if (sharedProject == null || jid == null)
            throw new IllegalArgumentException();
        this.sharedProject = sharedProject;
        this.jid = jid;
        this.colorID = colorID;
    }

    public JID getJID() {
        return this.jid;
    }

    /**
     * set the current user role of this user inside the current project.
     * 
     * @param role
     *            (Driver, Observer)
     */
    public void setUserRole(UserRole role) {
        this.role = role;
    }

    /**
     * Gets current project role of this user.
     * 
     * @return role (Driver, Observer)
     */
    public UserRole getUserRole() {
        return this.role;
    }

    /**
     * Utility method to determine whether this user has the UserRole.DRIVER
     * 
     * @return <code>true</code> if this User is driver, <code>false</code>
     *         otherwise.
     * 
     *         This is always !isObserver()
     */
    public boolean isDriver() {
        return this.role == UserRole.DRIVER;
    }

    /**
     * Utility method to determine whether this user has the UserRole.OBSERVER
     * 
     * @return <code>true</code> if this User is observer, <code>false</code>
     *         otherwise.
     * 
     *         This is always !isDriver()
     */
    public boolean isObserver() {
        return this.role == UserRole.OBSERVER;
    }

    public boolean isInSharedProject() {
        return sharedProject.getUser(getJID()) != null;
    }

    @Override
    public String toString() {
        return this.jid.getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jid == null) ? 0 : jid.hashCode());
        return result;
    }

    public int getColorID() {
        return this.colorID;
    }

    /**
     * @return <code>true</code> if the invitation is currently in progress,
     *         <code>false</code> if the invitation process is complete
     */
    public boolean isInvitationComplete() {
        return this.invitationComplete;
    }

    /**
     * Sets the {@link #invitationComplete} flag to true.
     * 
     * @throws IllegalStateException
     *             if the {@link #invitationComplete} flag was already
     *             <code>true</code>;
     */
    public void invitationCompleted() {
        if (invitationComplete)
            throw new IllegalStateException(
                "The invitation status of the user can be set only once!");
        invitationComplete = true;
    }

    public UserConnectionState getConnectionState() {
        return this.connectionState;
    }

    public void setConnectionState(UserConnectionState presence) {
        this.connectionState = presence;
        if (this.connectionState == User.UserConnectionState.OFFLINE) {
            this.offlineTime = new Date().getTime();
        }
    }

    public boolean isAway() {
        return away;
    }

    public void setAway(boolean away) {
        this.away = away;
    }

    public int getOfflineSeconds() {
        if (this.connectionState == UserConnectionState.OFFLINE) {
            return (int) (((new Date().getTime()) - this.offlineTime) / 1000);
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass()) {
            if (obj.getClass() == String.class || obj.getClass() == JID.class)
                log.warn(
                    "Comparing a User to a String or JID is probably a programming mistake: "
                        + obj.getClass(), new StackTrace());
            return false;
        }
        User other = (User) obj;
        if (jid == null) {
            if (other.jid != null)
                return false;
        } else if (!jid.equals(other.jid))
            return false;
        return true;
    }

    /**
     * Gets the ISharedProject to which this user belongs.
     */
    public ISharedProject getSharedProject() {
        return sharedProject;
    }

    /**
     * Returns true if this User object identifies the user which is using the
     * local Eclipse instance as opposed to the remote users in different
     * Eclipse instances.
     */
    public boolean isLocal() {
        return this.equals(sharedProject.getLocalUser());
    }

    /**
     * Returns true if this User is not the local user.
     */
    public boolean isRemote() {
        return !isLocal();
    }

    /**
     * Returns true if this user is the one that initiated the SharedProject
     * session and thus is responsible for synchronization, role management,
     */
    public boolean isHost() {
        return this.equals(sharedProject.getHost());
    }

    /**
     * Returns true if this user is not the host.
     */
    public boolean isClient() {
        return !isHost();
    }

    /**
     * Gets the name for a {@link User} for displaying.
     * 
     * If this is the local user "You" is returned, otherwise the nickname, if
     * available and distinct from the JID, and the JID. If no nickname is
     * known, only the JID is returned.
     */
    public String getHumanReadableName() {

        if (isLocal()) {
            return "You";
        }

        /*
         * TODO This should use a subscription based mechanism or cache the
         * nick, to prevent this being called too many times
         */
        String nickName = Util.getNickname(getSharedProject().getSaros(),
            getJID());
        String jidBase = getJID().getBase();

        if (nickName != null && !nickName.equals(jidBase)) {
            return nickName + " (" + jidBase + ")";
        }

        return jidBase;
    }
}
