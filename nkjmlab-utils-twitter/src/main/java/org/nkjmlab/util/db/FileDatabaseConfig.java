package org.nkjmlab.util.db;

import java.io.File;

public class FileDatabaseConfig {

  private final File dbDir;
  private final String dbName;
  private final String username;
  private final String password;
  private final String jdbcUrl;


  private FileDatabaseConfig(File dbDir, String dbName, String username, String password) {
    this.dbDir = dbDir;
    this.dbName = dbName;
    this.username = username;
    this.password = password;
    this.jdbcUrl = "jdbc:h2:tcp://localhost/" + new File(dbDir, dbName);
  }


  public File getDbDir() {
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
    private String dbDir;
    private String dbName;
    private String username = "";
    private String password = "";

    public Builder() {
      setDbDir("~/db/");
    }

    /**
     * Initializes a newly created {@code FileDatabaseConfig.Builder} object; you can get
     * {{@code FileDatabaseConfig} object via {@link #build()} method.
     *
     * @param dbDir the directory including the database file.
     * @param dbName the name of database.
     * @param username
     * @param password
     */
    public Builder(String dbDir, String dbName, String username, String password) {
      this.dbName = dbName;
      this.username = username;
      this.password = password;
      setDbDir(dbDir);
    }

    public Builder(String dbDir, String dbName) {
      this(dbDir, dbName, "", "");
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
      if (dbDir.startsWith("~/")) {
        dbDir = dbDir.replace("~/",
            new File(System.getProperty("user.home")).getPath() + File.separator);
      }
      this.dbDir = dbDir;
      return this;
    }

    public Builder setDbName(String dbName) {
      this.dbName = dbName;
      return this;
    }

    public FileDatabaseConfig build() {
      return new FileDatabaseConfig(new File(dbDir), dbName, username, password);
    }
  }



  @Override
  public String toString() {
    return "FileDatabaseConfig [dbDir=" + dbDir + ", dbName=" + dbName + ", username=" + username
        + ", password=" + password + ", jdbcUrl=" + jdbcUrl + "]";
  }



}
