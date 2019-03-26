package no.nav.journalfoeringMottak.server;

import javax.servlet.http.*;
import java.io.IOException;

public class AliveCheckServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println("alive");
    }
}
