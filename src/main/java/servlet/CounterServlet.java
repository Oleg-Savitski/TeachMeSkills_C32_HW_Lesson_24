package servlet;

import jakarta.servlet.ServletContext;
import service.LoggerService;
import session.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/count")
public class CounterServlet extends HttpServlet {
    public static final String GLOBAL_PAGES_VISIT_STATS = "globalPagesVisitStats";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static final Map<String, String> PROJECT_PAGES = new LinkedHashMap<>() {{
        put("/TeachMeSkills_C32_HW_Lesson_24/napoleon", "Napoleon's Biography");
        put("/TeachMeSkills_C32_HW_Lesson_24/napoleon-goals", "Napoleon's Goals");
        put("/TeachMeSkills_C32_HW_Lesson_24/settings", "Application Settings");
        put("/TeachMeSkills_C32_HW_Lesson_24/logs", "Log Journal");
        put("/TeachMeSkills_C32_HW_Lesson_24/count", "Visit Statistics");
    }};

    public static class PageVisitStats implements Serializable {
        private int visitCount;
        private LocalDateTime lastVisitTime;
        private String lastVisitorIP;

        public void incrementVisit(String visitorIP) {
            this.visitCount++;
            this.lastVisitTime = LocalDateTime.now();
            this.lastVisitorIP = visitorIP;
        }

        public int getVisitCount() {
            return visitCount;
        }

        public String getLastVisitTime() {
            return lastVisitTime != null
                    ? lastVisitTime.format(FORMATTER)
                    : "Page not visited";
        }

        public String getLastVisitorIP() {
            return lastVisitorIP != null
                    ? lastVisitorIP
                    : "No data";
        }
    }

    @Override
    public void init() throws ServletException {
        LoggerService.servletInit("CounterServlet");

        ServletContext context = getServletContext();
        Map<String, PageVisitStats> globalStats = new LinkedHashMap<>();

        PROJECT_PAGES.values().forEach(pageName ->
                globalStats.put(pageName, new PageVisitStats())
        );

        context.setAttribute(GLOBAL_PAGES_VISIT_STATS, globalStats);

        super.init();
    }

    public static void updatePageVisitStats(HttpServletRequest req) {
        ServletContext context = req.getServletContext();
        @SuppressWarnings("unchecked")
        Map<String, PageVisitStats> globalPagesVisitStats =
                (Map<String, PageVisitStats>) context.getAttribute(GLOBAL_PAGES_VISIT_STATS);

        String currentPagePath = req.getRequestURI();
        String currentPageName = PROJECT_PAGES.getOrDefault(currentPagePath, "Unknown Page");

        PageVisitStats currentPageStats = globalPagesVisitStats.get(currentPageName);
        if (currentPageStats != null) {
            currentPageStats.incrementVisit(req.getRemoteAddr());
            context.setAttribute(GLOBAL_PAGES_VISIT_STATS, globalPagesVisitStats);
        }

        LoggerService.info("Updated statistics for page: " + currentPageName);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        CounterServlet.updatePageVisitStats(req);
        try {
            HttpSession session = SessionManager.getOrCreateSession(req);
            LoggerService.info("Session ID: " + session.getId());
            session.setAttribute("lastVisitedPage", req.getRequestURI());

            LoggerService.servletRequest("CounterServlet",
                    String.format("IP: %s, RequestURI: %s",
                            req.getRemoteAddr(),
                            req.getRequestURI())
            );

            ServletContext context = getServletContext();
            @SuppressWarnings("unchecked")
            Map<String, PageVisitStats> globalPagesVisitStats =
                    (Map<String, PageVisitStats>) context.getAttribute(GLOBAL_PAGES_VISIT_STATS);

            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<!DOCTYPE html>");
            resp.getWriter().println("<html lang='en'>");
            resp.getWriter().println("<head>");
            resp.getWriter().println("<meta charset='UTF-8'>");
            resp.getWriter().println("<title>Visit Statistics</title>");
            resp.getWriter().println("<style>");
            resp.getWriter().println("body { font-family: Arial, sans-serif; max-width: 900px; margin: 0 auto; padding: 20px; }");
            resp.getWriter().println("h1, h2 { color: #333; }");
            resp.getWriter().println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            resp.getWriter().println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            resp.getWriter().println("th { background-color: #f2f2f2; font-weight: bold; }");
            resp.getWriter().println("tr:nth-child(even) { background-color: #f9f9f9; }");
            resp.getWriter().println("</style>");
            resp.getWriter().println("</head>");
            resp.getWriter().println("<body>");
            resp.getWriter().println("<h1>📊 Visit Statistics</h1>");

            resp.getWriter().println("<table>");
            resp.getWriter().println("<tr>" +
                    "<th>Page</th>" +
                    "<th>Number of Visits</th>" +
                    "<th>Last Visit</th>" +
                    "<th>Last Visitor IP</th>" +
                    "</tr>");

            globalPagesVisitStats.forEach((pageName, stats) -> {
                try {
                    resp.getWriter().println(String.format(
                            "<tr>" +
                                    "<td>%s</td>" +
                                    "<td>%d</td>" +
                                    "<td>%s</td>" +
                                    "<td>%s</td>" +
                                    "</tr>",
                            pageName,
                            stats.getVisitCount(),
                            stats.getLastVisitTime(),
                            stats.getLastVisitorIP()
                    ));
                } catch (IOException e) {
                    LoggerService.error("Error forming statistics row", e);
                }
            });

            resp.getWriter().println("</table>");
            resp.getWriter().println("<br><a href='" + req.getContextPath() + "/reset'>Reset Statistics</a>");
            resp.getWriter().println("</body>");
            resp.getWriter().println("</html>");
        } catch (Exception e) {
            LoggerService.error("Error processing request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
        }
    }

    @Override
    public void destroy() {
        LoggerService.servletDestroy("CounterServlet");
        super.destroy();
    }
}