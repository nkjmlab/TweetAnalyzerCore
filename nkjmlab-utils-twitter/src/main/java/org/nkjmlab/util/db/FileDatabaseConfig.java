package org.nkjmlab.util.db;

import java.io.File;

public class FileDatabaseConfig {

  private final String dbDir;
  private final String dbName;
  private final String username;
  private final String password;
  private final String jdbcUrl;
  private final File dbDirFile;


  private FileDatabaseConfig(String dbDir, String dbName, String username, String password) {
    this.dbDir = dbDir;
    this.dbName = dbName;
    this.username = username;
    this.password = password;
    this.jdbcUrl = "jdbc:h2:tcp://localhost/" + getDbDir() + dbName;
    this.dbDirFile = dbDir.startsWith("~/")
        ? new File(dbDir.replace("~/",
            new File(System.getProperty("user.home")).getPath() + File.separator))
        : new File(dbDir);


  }


  public String getDbDir() {
    return dbDir;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getDbName() {
    return dbName;
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }



  public static class Builder {
    private String dbDir = "~/db/";
    private String dbName;
    private String username = "";
    private String password = "";

    public Builder() {}

    public Builder(String dbDir, String dbName, String username, String password) {
      this.dbDir = dbDir;
      this.dbName = dbName;
      this.username = username;
      this.password = password;
    }

    public Builder setUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder setPassword(String password) {
      this.password = password;
      return this;
    }

    public Builder setDbDir(String dbDir) {
      this.dbDir = dbDir;
      return this;
    }

    public Builder setDbName(String dbName) {
      this.dbName = dbName;
      return this;
    }

    public FileDatabaseConfig build() {
      return new FileDatabaseConfig(dbDir, dbName, username, password);
    }
  }



  private File getDbDirFile() {
    return dbDirFile;
  }


  @Override
  public String toString() {
    return "FileDatabaseConfig [dbDir=" + dbDir + ", dbName=" + dbName + ", username=" + username
        + ", password=" + password + ", jdbcUrl=" + jdbcUrl + ", dbDirFile=" + dbDirFile + "]";
  }



}
