package servlet;

import jakarta.servlet.ServletException;
import service.LoggerService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@WebServlet("/logs")
public class LogViewerServlet extends HttpServlet {
    private static final String LOG_FILE_PATH = "C:\\Java-job\\TeachMeSkills_C32_HW_Lesson_24\\source\\logs.txt";

    @Override
    public void init() throws ServletException {
        LoggerService.servletInit("LogViewerServlet");
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        CounterServlet.updatePageVisitStats(req);
        try {
            LoggerService.servletRequest("LogViewerServlet",
                    String.format("IP: %s", req.getRemoteAddr())
            );

            File logFile = new File(LOG_FILE_PATH);

            if (!logFile.exists()) {
                LoggerService.error("Log file not found: " + LOG_FILE_PATH);
                sendErrorPage(resp, "Log file not found");
                return;
            }

            if (!logFile.canRead()) {
                LoggerService.error("No read permissions for log file: " + LOG_FILE_PATH);
                sendErrorPage(resp, "No read permissions for log file");
                return;
            }

            List<String> logLines = Files.readAllLines(Path.of(LOG_FILE_PATH), StandardCharsets.UTF_8)
                    .stream()
                    .skip(Math.max(0, Files.readAllLines(Path.of(LOG_FILE_PATH)).size() - 500))
                    .toList();

            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<!DOCTYPE html>");
            resp.getWriter().println("<html lang='en'>");
            resp.getWriter().println("<head>");
            resp.getWriter().println("<meta charset='UTF-8'>");
            resp.getWriter().println("<title>Log Journal</title>");
            resp.getWriter().println("<style>");
            resp.getWriter().println("body { font-family: monospace; }");
            resp.getWriter().println(".error { color: red; }");
            resp.getWriter().println(".warning { color: orange; }");
            resp.getWriter().println(".debug { color: blue; }");
            resp.getWriter().println("</style>");
            resp.getWriter().println("</head>");
            resp.getWriter().println("<body>");
            resp.getWriter().println("<h1>Log Journal (last 500 entries)</h1>");
            resp.getWriter().println("<pre>");

            for (String line : logLines) {
                String cssClass = getCssClass(line);
                resp.getWriter().println(String.format("<span class='%s'>%s</span>", cssClass, escapeHtml(line)));
            }

            resp.getWriter().println("</pre>");
            resp.getWriter().println("</body>");
            resp.getWriter().println("</html>");

            LoggerService.info("Log journal successfully displayed");

        } catch (Exception e) {
            LoggerService.error(
                    String.format(
                            "Critical error displaying logs from IP %s",
                            req.getRemoteAddr()
                    ),
                    e
            );
            sendErrorPage(resp, "Error displaying logs: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        LoggerService.servletDestroy("LogViewerServlet");
        super.destroy();
    }

    private String getCssClass(String logLine) {
        if (logLine.contains("| ERROR |")) return "error";
        if (logLine.contains("| WARNING |")) return "warning";
        if (logLine.contains("| DEBUG |")) return "debug";
        return "";
    }

    private String escapeHtml(String input) {
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void sendErrorPage(HttpServletResponse resp, String errorMessage) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println("<!DOCTYPE html>");
        resp.getWriter().println("<html>");
        resp.getWriter().println("<head><title>Error</title></head>");
        resp.getWriter().println("<body>");
        resp.getWriter().println("<h1>Error Loading Logs</h1>");
        resp.getWriter().println("<p>" + errorMessage + "</p>");
        resp.getWriter().println("</body>");
        resp.getWriter().println("</html>");
    }
}