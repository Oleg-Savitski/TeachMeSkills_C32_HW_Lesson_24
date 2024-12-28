package servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import service.LoggerService;

import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/reset")
public class ResetCounterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException {
        try {
            LoggerService.info("Requesting visit statistics reset");

            ServletContext context = req.getServletContext();
            Map<String, CounterServlet.PageVisitStats> globalStats = new LinkedHashMap<>();

            CounterServlet.PROJECT_PAGES.values().forEach(pageName ->
                    globalStats.put(pageName, new CounterServlet.PageVisitStats())
            );

            context.setAttribute(CounterServlet.GLOBAL_PAGES_VISIT_STATS, globalStats);

            LoggerService.debug("Global visit statistics have been reset");

            resp.sendRedirect(req.getContextPath() + "/count");
        } catch (Exception e) {
            LoggerService.error("Error in ResetCounterServlet: " + e.getMessage());
            throw new ServletException("Error resetting statistics", e);
        }
    }
}