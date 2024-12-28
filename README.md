Homework:
1. Implement a logging system that will record events in a file logs.txt (log events: servlet initialization, request processing, servlet destruction).
2. Develop a new servlet that will process requests at /logs. This servlet should read the contents of the file. logs.txt and output it to the browser in a human-readable format.
3. Create servlets for the application from homework 22. Servlets should return a page about you or your goals.(Make sure that logging is configured in the servlets.)
4. It is necessary to define the configuration parameters in the file web.xml . Parameters: AppName, appVersion, developerName, supportEmail. Create a servlet that will extract these parameters from the web.xml and output them in the browser when switching to /settings.
5. Create a servlet that will print the number of visits to this page when you go to /count. When working, use the Session mechanism.
6. Add the option to reset the session counter. This should be implemented via a separate URL (/reset), which resets the number of visits per page /count.
