package de.fu_berlin.inf.dpp.activities.serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.fu_berlin.inf.dpp.activities.SPathDataObject;
import de.fu_berlin.inf.dpp.activities.business.IActivity;
import de.fu_berlin.inf.dpp.activities.business.JupiterActivity;
import de.fu_berlin.inf.dpp.concurrent.jupiter.Operation;
import de.fu_berlin.inf.dpp.concurrent.jupiter.Timestamp;
import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.project.ISharedProject;

/**
 * A JupiterActivityDataObject is an Activity that can be handled by the Jupiter
 * Algorithm.
 */
@XStreamAlias("jupiterActivity")
public class JupiterActivityDataObject extends AbstractActivityDataObject {

    /**
     * Timestamp that specifies the definition context of the enclosed
     * operation.
     */
    protected final Timestamp timestamp;

    protected final Operation operation;

    protected final SPathDataObject editor;

    public JupiterActivityDataObject(Timestamp timestamp, Operation operation,
        JID source, SPathDataObject sPathDataObject) {
        super(source);
        this.timestamp = timestamp;
        this.operation = operation;
        this.editor = sPathDataObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.fu_berlin.inf.dpp.jupiter.Request#getOperation()
     */
    public Operation getOperation() {
        return this.operation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.fu_berlin.inf.dpp.jupiter.Request#getTimestamp()
     */
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public SPathDataObject getEditorPath() {
        return this.editor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        JupiterActivityDataObject other = (JupiterActivityDataObject) obj;
        if (editor == null) {
            if (other.editor != null)
                return false;
        } else if (!editor.equals(other.editor))
            return false;
        if (operation == null) {
            if (other.operation != null)
                return false;
        } else if (!operation.equals(other.operation))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((editor == null) ? 0 : editor.hashCode());
        result = prime * result
            + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result
            + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("JupiterActivityDataObject(");
        buffer.append(this.timestamp);
        buffer.append(",");
        buffer.append(this.operation);
        buffer.append(",");
        buffer.append(this.getSource());
        buffer.append(")");
        return buffer.toString();
    }

    public boolean dispatch(IActivityDataObjectConsumer consumer) {
        return consumer.consume(this);
    }

    public void dispatch(IActivityDataObjectReceiver receiver) {
        receiver.receive(this);
    }

    public IActivity getActivity(ISharedProject sharedProject) {
        return new JupiterActivity(timestamp, operation, sharedProject
            .getUser(source), editor.toSPath(sharedProject));
    }
}
