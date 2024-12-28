package servlet;

import jakarta.servlet.ServletException;
import service.LoggerService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/settings")
public class SettingsServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        LoggerService.servletInit("SettingsServlet");
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        CounterServlet.updatePageVisitStats(req);
        LocalDateTime requestTime = LocalDateTime.now();

        try {
            LoggerService.servletRequest("SettingsServlet",
                    String.format("IP: %s", req.getRemoteAddr())
            );

            String appName = getServletContext().getInitParameter("appName");
            String appVersion = getServletContext().getInitParameter("appVersion");
            String developerName = getServletContext().getInitParameter("developerName");
            String supportEmail = getServletContext().getInitParameter("supportEmail");

            appName = appName != null ? appName : "Not specified";
            appVersion = appVersion != null ? appVersion : "Unknown";
            developerName = developerName != null ? developerName : "Not specified";
            supportEmail = supportEmail != null ? supportEmail : "No contact";

            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Application Settings</title>
                    <style>
                        body {
                            font-family: 'Arial', sans-serif;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                            background-color: #f4f4f4;
                        }
                        .settings-container {
                            background-color: white;
                            border-radius: 10px;
                            padding: 30px;
                            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                        }
                        h1 {
                            color: #333;
                            text-align: center;
                            border-bottom: 2px solid #4a4e69;
                            padding-bottom: 10px;
                        }
                        .setting-item {
                            margin-bottom: 15px;
                            padding: 10px;
                            background-color: #f9f9f9;
                            border-left: 4px solid #4a4e69;
                        }
                        .setting-label {
                            font-weight: bold;
                            color: #4a4e69;
                        }
                    </style>
                </head>
                <body>
                    <div class="settings-container">
                        <h1>🔧 Application Settings</h1>
                        <div class="setting-item">
                            <span class="setting-label">Application Name:</span> %s
                        </div>
                        <div class="setting-item">
                            <span class="setting-label">Version:</span> %s
                        </div>
                        <div class="setting-item">
                            <span class="setting-label">Developer:</span> %s
                        </div>
                        <div class="setting-item">
                            <span class="setting-label">Support Email:</span> %s
                        </div>
                        <div class="setting-item">
                            <span class="setting-label">Request Processing Time:</span> %s
                        </div>
                    </div>
                </body>
                </html>
            """.formatted(
                    appName,
                    appVersion,
                    developerName,
                    supportEmail,
                    requestTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            ));

            LoggerService.info("Settings page successfully generated");

        } catch (Exception e) {
            LoggerService.error(
                    String.format(
                            "Error in SettingsServlet with IP %s: %s",
                            req.getRemoteAddr(),
                            e.getMessage()
                    ),
                    e
            );

            resp.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load application settings"
            );
        }
    }

    @Override
    public void destroy() {
        LoggerService.servletDestroy("SettingsServlet");
        super.destroy();
    }
}