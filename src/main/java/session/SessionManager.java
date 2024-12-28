package session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionManager {
    public static HttpSession getOrCreateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);

        if (session.getAttribute("visitCount") == null) {
            session.setAttribute("visitCount", 0);
        }

        return session;
    }

    public static int incrementVisitCount(HttpSession session) {
        Integer currentCount = (Integer) session.getAttribute("visitCount");
        currentCount = (currentCount == null) ? 1 : currentCount + 1;
        session.setAttribute("visitCount", currentCount);
        return currentCount;
    }

    public static void resetVisitCount(HttpSession session) {
        session.setAttribute("visitCount", 0);
    }
}