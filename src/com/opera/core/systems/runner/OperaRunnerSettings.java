package com.opera.core.systems.runner;

import com.opera.core.systems.OperaProduct;
import com.opera.core.systems.arguments.OperaCoreArguments;
import com.opera.core.systems.arguments.OperaDesktopArguments;
import com.opera.core.systems.arguments.interfaces.OperaArguments;
import com.opera.core.systems.scope.internal.OperaIntervals;

import org.openqa.selenium.Platform;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.openqa.selenium.net.PortProber;

import java.io.File;
import java.util.logging.Level;

/**
 * Defines a settings object which impacts OperaRunner, the interface for controlling the Opera
 * binary.
 *
 * @author Andreas Tolf Tolfsen <andreastt@opera.com>
 */
public class OperaRunnerSettings
    implements com.opera.core.systems.runner.interfaces.OperaRunnerSettings {

  protected File operaBinary = null;
  protected Integer display = null;
  protected OperaProduct product = OperaProduct.CORE;
  protected String profile = null;  // "" for Opera < 12
  protected boolean noQuit = false;
  protected String host = "127.0.0.1";
  protected Integer port = 0;  // -1 for Opera < 12
  protected Level loggingLevel = Level.INFO;
  protected com.opera.core.systems.arguments.interfaces.OperaArguments arguments;

  public OperaRunnerSettings() {
    // We read in environmental variable OPERA_ARGS in addition to existing arguments passed down
    // through OperaRunnerSettings/OperaArguments.  These are combined and can be retrieved using
    // OperaDriverSettings.getArguments().
    //
    // Note that this is a deviation from the principle of arguments normally overwriting
    // environmental variables.
    String environmentArguments = System.getenv("OPERA_ARGS");
    if (environmentArguments != null && !environmentArguments.isEmpty()) {
      OperaArguments
          parsedEnvironmentArguments =
          com.opera.core.systems.OperaArguments.parse(environmentArguments);
      if (arguments != null && !arguments.getArguments().isEmpty()) {
        arguments.merge(parsedEnvironmentArguments);
      } else {
        arguments = parsedEnvironmentArguments;
      }
    } else {
      arguments = new com.opera.core.systems.OperaArguments();
    }
  }

  public File getBinary() {
    return operaBinary;
  }

  public void setBinary(String path) {
    if (path != null && !path.isEmpty()) {
      File binary = new File(path);
      if (binary.exists() && binary.isFile() && binary.canExecute()) {
        operaBinary = binary;
      } else {
        throw new OperaRunnerException("No such file or not executable: " + binary);
      }
    } else {
      throw new OperaRunnerException("Invalid file path: " + path);
    }
  }

  public void setBinary(File binary) {
    if (binary == null) {
      throw new OperaRunnerException("Invalid file: " + binary);
    }
    operaBinary = binary;
  }

  public Integer getDisplay() {
    return display;
  }

  public void setDisplay(Integer display) throws UnsupportedOperationException {
    if (!Platform.getCurrent().is(Platform.LINUX)) {
      throw new UnsupportedOperationException(
          "Unsupported operating system: " + Platform.getCurrent());
    }
    this.display = display;
  }

  public OperaProduct getProduct() {
    return product;
  }

  public void setProduct(OperaProduct product) {
    this.product = product;
  }

  public String getProfile() {
    if (profile == null) {
      profile = TemporaryFilesystem.getDefaultTmpFS().createTempDir("opera-profile", "")
          .getAbsolutePath();
    }

    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public boolean getNoQuit() {
    return noQuit;
  }

  public void setNoQuit(boolean noQuit) {
    this.noQuit = noQuit;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    // The port Opera should connect to.  0 = Random, -1 = Opera default (7001) (for use with Opera
    // < 12).
    if (port == 0) {
      port = PortProber.findFreePort();
    } else if (port == -1) {
      port = (int) OperaIntervals.SERVER_PORT.getValue();
    }

    return port;
  }

  public void setPort(Integer port) {
    // The port Opera should connect to.  0 = Random, -1 = Opera default (7001) (for use with Opera
    // < 12).
    if (port == 0) {
      this.port = PortProber.findFreePort();
    } else if (port == -1) {
      this.port = (int) OperaIntervals.SERVER_PORT.getValue();
    } else {
      this.port = port;
    }

    this.port = port;
  }

  public boolean supportsDebugProxy() {
    return port != OperaIntervals.SERVER_PORT.getValue();
  }

  public OperaArguments getArguments() {
    return arguments;
  }

  public void setArguments(OperaArguments arguments) {
    this.arguments = arguments;
  }

  public Level getLoggingLevel() {
    return loggingLevel;
  }

  public void setLoggingLevel(Level level) {
    loggingLevel = level;
  }

  public static OperaRunnerSettings getDefaultSettings() {
    OperaRunnerSettings settings = new OperaRunnerSettings();

    OperaArguments arguments;
    if (settings.getProduct().is(OperaProduct.DESKTOP)) {
      arguments = new OperaDesktopArguments();
    } else {
      arguments = new OperaCoreArguments();
    }

    settings.setArguments(arguments);
    return settings;
  }

}