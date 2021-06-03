package org.nkjmlab.util.db;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.nkjmlab.util.lang.Try;

public class H2Server {

  private static final int DEFAULT_TCP_PORT = 9092;
  private static final String DEFAULT_TCP_PASSWORD = "DEFAULT_TCP_PASSWORD_99";
  private static Logger log = LogManager.getLogger();


  public static void main(String[] args) {}

  public static void startAndWait() {
    startAndWait(DEFAULT_TCP_PORT, DEFAULT_TCP_PASSWORD);
  }

  public static void shutdown() {
    shutdown(DEFAULT_TCP_PORT, DEFAULT_TCP_PASSWORD);
  }

  public static void startAndWait(int tcpPort, String tcpPassword, String... options) {
    List<String> args = new ArrayList<>(List.of("-web", "-webAllowOthers", "-tcp", "-ifNotExists"));
    args.addAll(List.of("-tcpPort", tcpPort + ""));
    args.addAll(List.of("-tcpPassword", tcpPassword));
    args.addAll(Arrays.asList(options));
    startAndWait(getClassPathOf("^h2-.*.jar$"), tcpPort, args);
  }

  /**
   *
   * @param tcpPassword is a password of tcpPassword Of H2 Server. not password of DB admin.
   */
  public static void shutdown(int tcpPort, String tcpPassword) {
    if (!isActive(tcpPort)) {
      log.info("H2 server is not active.");
      return;
    }
    try {
      log.info("Try to start shutdown h2 server...");
      Server.shutdownTcpServer("tcp://localhost:" + tcpPort, tcpPassword, false, false);
      Thread.sleep(2000);
    } catch (SQLException | InterruptedException e) {
      log.error(e.getMessage());
    }
    if (isActive(tcpPort)) {
      log.warn("H2 server is still active.");
    } else {
      log.info("H2 server is stopped.");
    }

  }

  public static String getClassPathOf(String regex) {
    for (String cp : System.getProperty("java.class.path").split(File.pathSeparator)) {
      if (new File(cp).getName().matches(regex)) {
        return cp;
      }
    }
    throw new RuntimeException(regex + " not found");
  }

  public static void startAndWait(String h2ClassPath, int tcpPort, List<String> h2ServerOpts) {
    if (isActive(tcpPort)) {
      log.info("H2 server has been already activated.");
      return;
    }
    List<String> args = new ArrayList<>();
    args.add("java");
    args.addAll(List.of("-cp", h2ClassPath, "org.h2.tools.Server"));
    args.addAll(h2ServerOpts);


    try {
      ProcessBuilder pb = new ProcessBuilder(args.toArray(String[]::new));
      pb.redirectErrorStream(true);
      Process process = pb.start();
      long timeout = 4;
      TimeUnit unit = TimeUnit.SECONDS;
      log.info("Try to start H2 server and wait [{} {}], command= {}", timeout, unit, args);
      process.waitFor(timeout, unit);

      if (isActive(tcpPort)) {
        log.info("H2 server is activated.");
      } else {
        log.error("Fail to start h2 server.");
      }
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage());
    }

  }

  private static boolean isActive(int port) {
    try (ServerSocket socket = new ServerSocket(port)) {
      return false;
    } catch (IOException e) {
      return true;
    }
  }



  public static void openBrowser(Connection conn, boolean keepAlive) {
    try {
      Server server = Server.createWebServer(keepAlive ? new String[] {"-webPort", "0"}
          : new String[] {"-webPort", "0", "-webDaemon"});
      server.start();
      log.info("H2 Temporal WebServer is start at {}", server.getURL());

      WebServer webServer = (WebServer) server.getService();
      webServer.addSession(conn);
      String url = webServer.addSession(conn);
      Server.openBrowser(url);
      log.info("Database open on browser = {}", url);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void openBrowser(DataSource dataSource, boolean keepAlive) {
    Try.runOrElse(() -> openBrowser(dataSource.getConnection(), keepAlive),
        e -> log.error(e.getMessage()));
  }



}
