package academy.prog.julia.configurations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Configuration
public class FrontEndStarterConfig {
    @Value("${cert_gen.port}")
    private String cert_gen_port;

    private static final Logger LOGGER = LogManager.getLogger(FrontEndStarterConfig.class);


    /**
     * Starts the frontend application depending on the OS.
     * If running on Windows, attempts to start with a .bat file or PowerShell.
     * On Linux/Mac, it will use a shell script.
     * @return a command line runner for frontend startup.
     */
    @Bean
    public CommandLineRunner frontEndStart() {
        return args -> {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                try {
                    startEndFrontOnBat();
                } catch (IOException e) {
                    LOGGER.info("STARTING FRONTEND PART ON bat FAILED", e);
                    try {
                        startEndFrontOnPowerShell();
                    } catch (IOException ex) {
                        LOGGER.info("STARTING FRONTEND PART ON PowerShell FAILED", ex);
                    }
                }
            } else {
                try {
                    startEndFrontOnSh();
                } catch (IOException e) {
                    LOGGER.info("STARTING FRONTEND PART ON Linux/Mac FAILED", e);
                }
            }
        };
    }

    /**
     * Starts the Python server depending on the OS.
     * If running on Windows, attempts to start with a .bat file or PowerShell.
     * On Linux/Mac, it will use a shell script.
     * @return a command line runner for Python server startup.
     */
    @Bean
    public CommandLineRunner pythonServerStart() {
        return args -> {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                try {
                    startPythonServerOnBat();
                } catch (IOException e) {
                    LOGGER.info("STARTING PYTHON SERVER ON bat FAILED", e);
                    try {
                        startPythonServerOnPowerShell();
                    } catch (IOException ex) {
                        LOGGER.info("STARTING PYTHON SERVER ON PowerShell FAILED", ex);
                    }
                }
            } else if (os.contains("linux")) {
                try {
                    startPythonServerOnShForLinux();
                } catch (IOException e) {
                    LOGGER.info("STARTING PYTHON SERVER ON Linux/Mac FAILED", e);
                }
            } else {
                try {
                    startPythonServerOnSh();
                } catch (IOException e) {
                    LOGGER.info("STARTING PYTHON SERVER ON Linux/Mac FAILED", e);
                }
            }

            Thread.sleep(20000);
            openBrowserForDjangoService();
        };
    }

    /**
     * Starts the frontend on Linux/Mac using shell commands.
     * Navigates to the frontend directory and runs 'npm start'.
     * Registers a shutdown hook to stop the process upon application shutdown.
     * @throws IOException if there is an issue starting the frontend.
     */
    private void startEndFrontOnSh() throws IOException {
        String projectPath = System.getProperty("user.dir").replace("\\", "/");
        String frontendPath = projectPath + "/src/main/frontend";
        String command = "cd \"" + frontendPath + "\" && npm start";
        Process process = new ProcessBuilder("sh", "-c", command)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Process end = new ProcessBuilder("kill", "-9", String.valueOf(process.pid())).start();
            } catch (Exception e) {
                LOGGER.error("LocalHost:3000 is still Alive!", e);
            }
        }));
        catchErrorsForFrontStarting(process);
    }

    /**
     * Starts the frontend on Windows using PowerShell.
     * Navigates to the frontend directory and runs 'npm start'.
     * Registers a shutdown hook to stop the process upon application shutdown.
     * @throws IOException if there is an issue starting the frontend.
     */
    private void startEndFrontOnPowerShell() throws IOException {
        String projectPath = System.getProperty("user.dir");
        String frontendPath = projectPath + "\\src\\main\\frontend";
        String command = "cd \"" + frontendPath + "\n" + "\" npm start";
        Process process = new ProcessBuilder("powershell", "-c", command)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        endFrontWin();
        catchErrorsForFrontStarting(process);
    }

    /**
     * Starts the frontend on Windows using a batch script.
     * Navigates to the frontend directory and runs 'npm start'.
     * Registers a shutdown hook to stop the process upon application shutdown.
     * @throws IOException if there is an issue starting the frontend.
     */
    private void startEndFrontOnBat() throws IOException {
        String projectPath = System.getProperty("user.dir").replace("\\", "/");
        String frontendPath = projectPath + "/src/main/frontend";
        String command = "cd /d \"" + frontendPath + "\" && npm.cmd start";
        Process process = new ProcessBuilder("cmd", "/c", command)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        endFrontWin();
        catchErrorsForFrontStarting(process);
    }

    /**
     * Registers a shutdown hook to terminate the frontend process on Windows.
     * It ensures that 'node.exe' is terminated when the application is closed.
     */
    private void endFrontWin() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Process end = new ProcessBuilder("taskkill", "/F", "/IM", "node.exe").start();
            } catch (Exception e) {
                LOGGER.error("LocalHost:3000 is still Alive!", e);
            }
        }));
    }

    /**
     * Catches and logs any errors that occur during the starting of the frontend process.
     * Reads the error stream of the provided process and logs each error line using the logger.
     * @param process The process whose error stream needs to be read and logged.
     * @throws IOException if there is an issue reading the error stream.
     */
    private void catchErrorsForFrontStarting(Process process) throws IOException {
        InputStream errorStream = process.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
        String line;
        while ((line = reader.readLine()) != null) {
            LOGGER.error(line);
        }
    }

    /**
     * Starts a Python server on Linux/Mac using shell commands.
     * Navigates to the 'cert_gen' directory, installs required dependencies, runs migrations, and starts the server.
     * Registers a shutdown hook to stop the server upon application shutdown.
     * @throws IOException if there is an issue starting the Python server.
     */
    private void startPythonServerOnSh() throws IOException {
        String projectPath = System.getProperty("user.dir").replace("\\", "/");
        String certGenPath = projectPath + "/cert_gen";
        String command = "cd \"" + certGenPath + "\" && pip install -r requirements.txt && " +
                "python3 manage.py migrate && python3 manage.py runserver";
        Process process = new ProcessBuilder("sh", "-c", command)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Process end = new ProcessBuilder("kill", "-9", String.valueOf(process.pid())).start();
            } catch (Exception e) {
                LOGGER.error("Python server is still running!", e);
            }
        }));
        catchErrorsForPythonServerStarting(process);
    }


    /**
     * Starts the Python server for Linux Debian using shell commands.
     * Steps:
     * 1. create venv: python3 -m venv venv
     * 2. activate: source venv/bin/activate
     * 3. install req.txt: pip install -r requirements.txt
     * 4. install: pip install yagmail
     * 5. install: pip install schedule
     * 6 go on: python manage.py migrate
     * 7. go on: python manage.py runserver
     * @throws IOException if there is an issue starting the server.
     */
    private void startPythonServerOnShForLinux() throws IOException {
        String projectPath = System.getProperty("user.dir").replace("\\", "/");
        String certGenPath = projectPath + "/cert_gen";

        String command = "cd \"" + certGenPath + "\" && python3 -m venv venv && ource venv/bin/activate && " +
                " pip install -r requirements.txt && pip install yagmail && pip install schedule && " +
                "python3 manage.py migrate && python3 manage.py runserver";
        Process process = new ProcessBuilder("sh", "-c", command)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Process end = new ProcessBuilder("kill", "-9", String.valueOf(process.pid())).start();
            } catch (Exception e) {
                LOGGER.error("Python server is still running!", e);
            }
        }));
        catchErrorsForPythonServerStarting(process);
    }

    /**
     * Starts the Python server using PowerShell on Windows.
     * Navigates to the 'cert_gen' directory, installs dependencies, runs migrations, and starts the server.
     * @throws IOException if there is an issue starting the server.
     */
    private void startPythonServerOnPowerShell() throws IOException {
        String projectPath = System.getProperty("user.dir");
        String certGenPath = projectPath + "\\cert_gen";
        String command = "cd \"" + certGenPath + "\n" + "\" pip install -r requirements.txt && " +
                "python manage.py migrate && python manage.py runserver";
        Process process = new ProcessBuilder("powershell", "-c", command)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        endPythonServerWin();
        catchErrorsForPythonServerStarting(process);
    }

    /**
     * Starts the Python server using a .bat script on Windows.
     * Navigates to the 'cert_gen' directory, installs dependencies, runs migrations, and starts the server.
     * @throws IOException if there is an issue starting the server.
     */
    private void startPythonServerOnBat() throws IOException {
        String projectPath = System.getProperty("user.dir").replace("\\", "/");
        String certGenPath = projectPath + "/cert_gen";
        String command = "cd /d \"" + certGenPath + "\" && pip install -r requirements.txt && " +
                "python manage.py migrate && python manage.py runserver";
        Process process = new ProcessBuilder("cmd", "/c", command)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        endPythonServerWin();
        catchErrorsForPythonServerStarting(process);
    }

    /**
     * Adds a shutdown hook to stop the Python server on Windows.
     * Uses the 'taskkill' command to forcefully stop any running Python processes.
     */
    private void endPythonServerWin() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Process end = new ProcessBuilder("taskkill", "/F", "/IM", "python.exe").start();
            } catch (Exception e) {
                LOGGER.error("Python server is still running!", e);
            }
        }));
    }

    /**
     * Catches and logs any errors that occur during the starting of the Python server.
     * Reads the error stream of the provided process and logs each error line using the logger.
     * @param process The process whose error stream needs to be read and logged.
     * @throws IOException if there is an issue reading the error stream.
     */
    private void catchErrorsForPythonServerStarting(Process process) throws IOException {
        InputStream errorStream = process.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
        String line;
        while ((line = reader.readLine()) != null) {
            LOGGER.error(line);
        }
    }

    /**
     * Opens the browser to access the Django service, depending on the operating system.
     * Uses system-specific commands to open the provided URL in the default web browser.
     * @throws IOException if there is an issue opening the browser.
     */
    private void openBrowserForDjangoService() throws IOException {
        String url = cert_gen_port;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            Runtime.getRuntime().exec("cmd /c start " + url);
        } else if (os.contains("mac")) {
            Runtime.getRuntime().exec("open " + url);
        } else if (os.contains("nix") || os.contains("nux")) {
            Runtime.getRuntime().exec("xdg-open " + url);
        }
    }

}
