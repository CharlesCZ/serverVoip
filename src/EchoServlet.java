package src;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.*;
import javax.servlet.sip.*;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.annotation.SipListener;


/**
 * EchoServlet provides a simple example of a SIP servlet.
 * EchoServlet echoes instant messages sent by Windows Messenger.
 */

//@javax.servlet.sip.annotation.SipServlet(name = "probnyservlet",applicationName = "serverVoip",loadOnStartup = 2)
public class EchoServlet extends SipServlet  implements SipServletListener {

    @Override
    public void servletInitialized(SipServletContextEvent sipServletContextEvent) {

    }




    /**
     * _address keeps the mapping between sign-in name and actual contact address.
     */
    protected HashMap _addresses = new HashMap();

    /**
     * Invoked for SIP INVITE requests, which are sent by Windows Messenger to establish a chat session.
     */
    protected void doInvite(SipServletRequest req) throws IOException, ServletException {
        // We accept invitation for a new session by returning 200 OK response.
        req.createResponse(SipServletResponse.SC_OK).send();
    }

    /**
     * Invoked for SIP REGISTER requests, which are sent by Windows Messenger for sign-in and sign-off.
     */
    protected void doRegister(SipServletRequest req) throws IOException, ServletException {
        String aor = req.getFrom().getURI().toString().toLowerCase();
        synchronized (_addresses) {
            // The non-zero value of Expires header indicates a sign-in.
            if (req.getExpires() != 0) {
                // Keep the name/address mapping.
                _addresses.put(aor, req.getAddressHeader("Contact").getURI());
            }
            // The zero value of Expires header indicates a sign-off.
            else {
                // Remove the name/address mapping.
                _addresses.remove(aor);
            }
        }
        // We accept the sign-in or sign-off by returning 200 OK response.
        req.createResponse(SipServletResponse.SC_OK).send();
    }

    /**
     * Invoked for SIP MESSAGE requests, which are sent by Windows Messenger for instant messages.
     */
    protected void doMessage(SipServletRequest req) throws IOException, ServletException {
        SipURI uri = null;
        synchronized (_addresses) {
            // Get the previous registered address for the sender.
            uri = (SipURI) _addresses.get(req.getFrom().getURI().toString().toLowerCase());
        }
        if (uri == null) {
            // Reject the message if it is not from a registered user.
            req.createResponse(SipServletResponse.SC_FORBIDDEN).send();
            return;
        }

        // We accept the instant message by returning 200 OK response.
        req.createResponse(SipServletResponse.SC_OK).send();

        // Create an echo SIP MESSAGE request with the same content.
        SipServletRequest echo = req.getSession().createRequest("MESSAGE");
        String charset = req.getCharacterEncoding();
        if (charset != null) {
            echo.setCharacterEncoding(charset);
        }
        echo.setRequestURI(uri);
        echo.setContent(req.getContent(), req.getContentType());
        // Send the echo MESSAGE request back to Windows Messenger.
        echo.send();
    }

    /**
     * Invoked for SIP 2xx class responses.
     */
    protected void doSuccessResponse(SipServletResponse resp) throws IOException, ServletException {
        // Print out when the echo message was accepted.
        if (resp.getMethod().equalsIgnoreCase("MESSAGE")) {
            System.out.println("\"" + resp.getRequest().getContent() + "\" was accepted: " + resp.getStatus());
        }
    }

    /**
     * Invoked for SIP 4xx-6xx class responses.
     */
    protected void doErrorResponse(SipServletResponse resp) throws IOException, ServletException {
        // Print out when the echo message was rejected/
        if (resp.getMethod().equalsIgnoreCase("MESSAGE")) {
            System.out.println("\"" + resp.getRequest().getContent() + "\" was rejected: " + resp.getStatus());
        }
    }

    /**
     * Invoked for SIP BYE requests, which are sent by Windows Messenger to terminate a chat session/
     */
    protected void doBye(SipServletRequest req) throws IOException, ServletException {
        // Accept session termination by returning 200 OK response.
        req.createResponse(SipServletResponse.SC_OK).send();
    }
}