package de.fu_berlin.inf.dpp.ui.browser_functions;

import com.google.gson.Gson;
import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.ui.model.Account;
import de.fu_berlin.inf.dpp.util.ThreadUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Display;
import org.jivesoftware.smack.XMPPException;

/**
 * This class implements the functions to be called by Javascript code for
 * the contact list. These are the callback functions to invoke Java code from
 * Javascript.
 */
public class ContactListBrowserFunctions {

    private static final Logger LOG = Logger
        .getLogger(ContactListBrowserFunctions.class);

    private ContactListCoreService contactListCoreService;

    private Browser browser;

    /**
     * @param browser the SWT browser in which the functions should be injected
     * @param contactListCoreService
     */
    public ContactListBrowserFunctions(Browser browser,
        ContactListCoreService contactListCoreService) {
        this.browser = browser;
        this.contactListCoreService = contactListCoreService;
    }

    /**
     * Injects Javascript functions into the HTML page. These functions
     * call Java code below when invoked.
     */
    public void createJavascriptFunctions() {
        //TODO remember to disable button in HTML while connecting
        new BrowserFunction(browser, "__java_connect") {
            @Override
            public Object function(Object[] arguments) {
                if (arguments.length > 0 && arguments[0] != null) {
                    Gson gson = new Gson();
                    final Account account = gson
                        .fromJson((String) arguments[0], Account.class);
                    ThreadUtils.runSafeAsync(LOG, new Runnable() {
                        @Override
                        public void run() {
                            contactListCoreService.connect(account);
                        }
                    });
                } else {
                    LOG.error("Connect was called without an account.");
                    browser.execute(
                        "alert('Cannot connect because no account was given.');");
                }
                return null;
            }
        };
        new BrowserFunction(browser, "__java_disconnect") {
            @Override
            public Object function(Object[] arguments) {
                ThreadUtils.runSafeAsync(LOG, new Runnable() {
                    @Override
                    public void run() {
                        contactListCoreService.disconnect();
                    }
                });
                return null;
            }
        };

        new BrowserFunction(browser, "__java_deleteContact") {
            @Override
            public Object function(final Object[] arguments) {
                ThreadUtils.runSafeAsync(LOG, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            contactListCoreService
                                .deleteContact(new JID((String) arguments[0]));
                        } catch (XMPPException e) {
                            LOG.error("Error deleting contact ", e);
                            //TODO getDefault() may create a new display when Eclipse shut down
                            Display.getDefault().asyncExec(new Runnable() {
                                @Override
                                public void run() {
                                    browser.execute( "alert('Error deleting contact');");
                                }
                            });
                        }
                    }
                });
                return null;
            }
        };

        new BrowserFunction(browser, "__java_addContact") {
            @Override
            public Object function(final Object[] arguments) {
                ThreadUtils.runSafeAsync(LOG, new Runnable() {
                    @Override
                    public void run() {
                        contactListCoreService.addContact(new JID((String) arguments[0]));
                    }
                });
                return null;
            }
        };
    }
}
