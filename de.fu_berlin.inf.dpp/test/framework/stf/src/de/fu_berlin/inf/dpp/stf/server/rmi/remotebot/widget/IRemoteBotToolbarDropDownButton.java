package de.fu_berlin.inf.dpp.stf.server.rmi.remotebot.widget;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.widgets.MenuItem;
import org.hamcrest.Matcher;

public interface IRemoteBotToolbarDropDownButton extends Remote {

    /**********************************************
     * 
     * finders
     * 
     **********************************************/

    public IRemoteBotMenu menuItem(String menuItem) throws RemoteException;

    public IRemoteBotMenu menuItem(Matcher<MenuItem> matcher)
        throws RemoteException;

    public IRemoteBotMenu contextMenu(String text) throws RemoteException;

    /**********************************************
     * 
     * actions
     * 
     **********************************************/
    public void click() throws RemoteException;

    public void clickAndWait() throws RemoteException;

    public void setFocus() throws RemoteException;

    public void pressShortcut(KeyStroke... keys) throws RemoteException;

    /**********************************************
     * 
     * states
     * 
     **********************************************/
    public boolean isEnabled() throws RemoteException;

    public boolean isVisible() throws RemoteException;

    public boolean isActive() throws RemoteException;

    public String getText() throws RemoteException;

    public String getToolTipText() throws RemoteException;

    /**********************************************
     * 
     * waits until
     * 
     **********************************************/
    public void waitUntilIsEnabled() throws RemoteException;
}
