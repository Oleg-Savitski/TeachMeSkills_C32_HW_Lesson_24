package servlet;

import jakarta.servlet.ServletException;
import service.LoggerService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/napoleon-goals")
public class NapoleonGoalsServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        LoggerService.servletInit("NapoleonGoalsServlet");
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        CounterServlet.updatePageVisitStats(req);
        LoggerService.servletRequest("NapoleonGoalsServlet",
                String.format("IP: %s, UserAgent: %s",
                        req.getRemoteAddr(),
                        req.getHeader("User-Agent")
                )
        );

        try {
            String filePath = getServletContext().getRealPath("/napoleon-goals.html");
            LoggerService.debug("Goals file path: " + filePath);
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                LoggerService.error("Goals file not found: " + filePath);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Goals page not found");
                return;
            }

            String htmlContent = Files.readString(path);
            LoggerService.debug("Goals file size: " + htmlContent.length() + " bytes");
            String modifiedContent = htmlContent.replace(
                    "</body>",
                    """
                    <div class="navigation" style="
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        margin: 20px 0;
                    ">
                        <a href="/TeachMeSkills_C32_HW_Lesson_24/napoleon"
                           style="
                            display: inline-block;
                            padding: 10px 20px;
                            background-color: #4a4e69;
                            color: white;
                            text-decoration: none;
                            border-radius: 5px;
                            text-align: center;
                        ">Go to Biography</a>
                    </div>
                    </body>"""
            );

            LoggerService.info("Goals content successfully modified");
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println(modifiedContent);
            LoggerService.info("Napoleon's goals page successfully processed");

        } catch (IOException e) {
            LoggerService.error(
                    String.format(
                            "Critical error processing goals from IP %s",
                            req.getRemoteAddr()
                    ),
                    e
            );

            resp.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error loading goals"
            );
        }
    }

    @Override
    public void destroy() {
        LoggerService.servletDestroy("NapoleonGoalsServlet");
        super.destroy();
    }
}