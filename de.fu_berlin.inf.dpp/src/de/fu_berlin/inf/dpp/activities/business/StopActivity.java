package de.fu_berlin.inf.dpp.activities.business;

import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.activities.serializable.IActivityDataObject;
import de.fu_berlin.inf.dpp.activities.serializable.StopActivityDataObject;
import de.fu_berlin.inf.dpp.project.ISharedProject;

/**
 * A StopActivity is used for signaling to a user that he should be stopped or
 * started (meaning that no more activityDataObjects should be generated by this
 * user).
 */
public class StopActivity extends AbstractActivity {

    protected User initiator;

    /** The user who has to be locked / unlocked. */
    protected final User user;

    public enum Type {
        LOCKREQUEST, UNLOCKREQUEST
    }

    protected final Type type;

    public enum State {
        INITIATED, ACKNOWLEDGED
    }

    protected final State state;

    /** A stop activity has a unique ID. */
    protected final String stopActivityID;

    public StopActivity(User source, User initiator, User user, Type type,
        State state, String stopActivityID) {

        super(source);

        this.initiator = initiator;
        this.user = user;
        this.state = state;
        this.type = type;
        this.stopActivityID = stopActivityID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
            + ((initiator == null) ? 0 : initiator.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result
            + ((stopActivityID == null) ? 0 : stopActivityID.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        StopActivity other = (StopActivity) obj;
        if (initiator == null) {
            if (other.initiator != null)
                return false;
        } else if (!initiator.equals(other.initiator))
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        if (stopActivityID == null) {
            if (other.stopActivityID != null)
                return false;
        } else if (!stopActivityID.equals(other.stopActivityID))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

    /**
     * The user to be locked/unlocked by this activityDataObject
     */
    public User getUser() {
        return user;
    }

    /**
     * The user who requested the lock/unlock.
     * 
     * (in most cases this should be the host)
     */
    public User getInitiator() {
        return initiator;
    }

    /**
     * Returns the JID of the user to which this StopActivity should be sent.
     * 
     * This method is a convenience method for getting the user or initiator
     * based on the state of this stop activityDataObject.
     */
    public User getRecipient() {
        switch (getState()) {
        case INITIATED:
            return getUser();
        case ACKNOWLEDGED:
            return getInitiator();
        default:
            throw new IllegalStateException(
                "StopActivity is in an illegal state to return a recipient");
        }
    }

    public State getState() {
        return state;
    }

    public StopActivity generateAcknowledgment(User source) {
        return new StopActivity(source, initiator, user, type,
            State.ACKNOWLEDGED, stopActivityID);
    }

    public Type getType() {
        return type;
    }

    public String getActivityID() {
        return stopActivityID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StopActivity (id: " + stopActivityID);
        sb.append(", type: " + type);
        sb.append(", state: " + state);
        sb.append(", initiator: " + initiator.toString());
        sb.append(", affected user: " + user.toString());
        sb.append(", src: " + getSource() + ")");
        return sb.toString();
    }

    public boolean dispatch(IActivityConsumer consumer) {
        return consumer.consume(this);
    }

    public void dispatch(IActivityReceiver receiver) {
        receiver.receive(this);
    }

    public IActivityDataObject getActivityDataObject(ISharedProject project) {
        return new StopActivityDataObject(source.getJID(), initiator.getJID(),
            user.getJID(), type, state, stopActivityID);
    }
}