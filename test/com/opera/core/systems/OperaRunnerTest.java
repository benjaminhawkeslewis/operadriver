package com.opera.core.systems;

import com.opera.core.systems.arguments.OperaArgument;
import com.opera.core.systems.arguments.OperaCoreArguments;
import com.opera.core.systems.arguments.OperaDesktopArguments;
import com.opera.core.systems.runner.OperaRunner;
import com.opera.core.systems.runner.OperaRunnerSettings;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OperaRunnerTest {

  private static TestOperaRunner runner;
  private OperaRunnerSettings settings;

  @Before
  public void beforeEach() {
    settings = new OperaRunnerSettings();
    runner = null;
  }

  @Test
  public void testConstruction() {
    runner = new TestOperaRunner();
    List<OperaArgument> arguments = runner.getSettings().getArguments().getArguments();

    // TODO(andreastt): Problems with core-gogi disallows us to have -autotestmode as the first argument
    assertNotNull(runner);
    assertNotNull(runner.getSettings().getProfile());
    assertEquals("autotestmode", arguments.get(2).getArgument());  // 0
    assertEquals("pd", arguments.get(0).getArgument());  // 1
    assertEquals("debugproxy", arguments.get(1).getArgument());  // 2
    assertTrue(Integer.valueOf(arguments.get(1).getValue().split(":")[1]) > 0);  // 2
  }

  @Test
  public void testConstructionWithProductCore() {
    settings.setProduct(OperaProduct.CORE);
    runner = new TestOperaRunner(settings);
    assertNotNull(runner);
    assertTrue(runner.getSettings().getArguments() instanceof OperaCoreArguments);
  }

  @Test
  public void testConstructionWithProductDesktop() {
    settings.setProduct(OperaProduct.DESKTOP);
    runner = new TestOperaRunner(settings);
    assertNotNull(runner);
    assertTrue(runner.getSettings().getArguments() instanceof OperaDesktopArguments);
  }

  @Test
  public void testConstructionWithProfile() {
    String profile = "/my/profile";
    settings.setProfile(profile);
    runner = new TestOperaRunner(settings);
    assertNotNull(runner);
    assertEquals(profile,
                 runner.getSettings().getArguments().getArguments().get(0).getValue());  // 1
  }

  @Test
  public void testConstructionWithPort() {
    settings.setPort(0);
    runner = new TestOperaRunner(settings);
    assertNotNull(runner);
    assertTrue(Integer.valueOf(
        runner.getSettings().getArguments().getArguments().get(1).getValue().split(":")[1])
               > 0);  // 2
  }

  @Test
  public void testConstructionWithArguments() {
    OperaArguments arguments = new OperaArguments();
    arguments.add("foo");
    arguments.add("bar", "bah");
    arguments.add("baz", "abc");

    settings.setArguments(arguments);
    runner = new TestOperaRunner(settings);
    assertNotNull(runner);
    assertTrue("should contain all the arguments added",
               runner.getSettings().getArguments().getArguments()
                   .containsAll(arguments.getArguments()));
  }

  @Test
  @Ignore
  public void testStartOpera() {

  }

  @Test
  @Ignore
  public void testStopOpera() {

  }

  @Test
  @Ignore
  public void testIsOperaRunning() {

  }

  @Test
  @Ignore
  public void testHasOperaCrashed() {

  }

  @Test
  @Ignore
  public void testGetOperaCrashlog() {

  }

  @Test
  @Ignore
  public void testShutdown() {

  }

  @Test
  @Ignore
  public void testSaveScreenshot() {

  }

  public class TestOperaRunner extends OperaRunner {

    public TestOperaRunner() {
      super();
    }

    public TestOperaRunner(OperaRunnerSettings settings) {
      super(settings);
    }

    public OperaRunnerSettings getSettings() {
      return this.settings;
    }

  }

}