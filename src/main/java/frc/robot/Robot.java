// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
//import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
// import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.util.sendable.SendableRegistry;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;

//Import required WPILib libraries
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package
 * after creating this project, you must also update the build.gradle
 * file in the project.
 */
public class Robot extends TimedRobot {
  private static final String autoDefault = "Do nothing";
  private static final String autoOption1 = "Shoot";
  private static final String autoOption2 = "Shoot n Move";
  private static final String autoOption3 = "Shoot x2";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be used
   * initialization code.
   */
  private final WPI_VictorSPX m_leftMotorLead = new WPI_VictorSPX(6);
  private final WPI_VictorSPX m_leftMotorFollow = new WPI_VictorSPX(2);
  private final WPI_VictorSPX m_rightMotorLead = new WPI_VictorSPX(5);
  private final WPI_VictorSPX m_rightMotorFollow = new WPI_VictorSPX(1);

  private final TalonSRX m_climbRight = new TalonSRX(4);
  private final WPI_VictorSPX m_climbLeft = new WPI_VictorSPX(7);

  private final TalonSRX leftShooter = new TalonSRX(8);
  private final WPI_VictorSPX rightShooter = new WPI_VictorSPX(3);

  private final WPI_VictorSPX intakeLittleWheels = new WPI_VictorSPX(9);
  private final WPI_VictorSPX intakeMover = new WPI_VictorSPX(10);

  private DifferentialDrive m_robotDrive;

  private final XboxController m_driverController = new XboxController(0);
  private XboxController m_coDriverController = new XboxController(1);

  private DigitalInput leftLimitSwitch = new DigitalInput(0);
  private DigitalInput rightLimitSwitch = new DigitalInput(1);

  private final Timer moveTimer = new Timer();
  private final Timer shootTimer = new Timer();
  private final Timer twoNoteTimer = new Timer();
  private boolean shootCompleted = false;
  private boolean twoNoteShootCompleted = false;

  private double fullSpeed = 1;
  private double minSpeed = 0.3;

  // Create an instance of the AnalogInput class so we can read from it later
  /*
   * public DigitalOutput ultrasonicTriggerPinOne = new DigitalOutput(0);
   * public AnalogInput ultrasonicSensorOne = new AnalogInput(0);
   * public double ultrasonicSensorOneRange = 0;
   * public double voltageScaleFactor = 1;
   * public void turnOnSensorOne() {
   * ultrasonicTriggerPinOne.set(true);
   * }
   * public void turnOffSensors() {
   * ultrasonicTriggerPinOne.set(false);
   * }
   */

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption(autoDefault, autoDefault);
    m_chooser.addOption(autoOption1, autoOption1);
    m_chooser.addOption(autoOption2, autoOption2);
    m_chooser.addOption(autoOption3, autoOption3);
    SmartDashboard.putData("Auto choices", m_chooser);

    // USED TO BE. Commented out because the follower motor wasn't actually moving
    // m_leftMotorLead.follow(m_leftMotorFollow);
    // m_rightMotorLead.follow(m_rightMotorFollow);

    // NEW
    m_leftMotorFollow.set(ControlMode.Follower, m_leftMotorLead.getDeviceID());
    // m_leftMotorLead.configOpenloopRamp(0.2);
    // m_rightMotorLead.configOpenloopRamp(0.2);
    m_leftMotorFollow.setInverted(InvertType.FollowMaster);

    m_rightMotorFollow.set(ControlMode.Follower, m_rightMotorLead.getDeviceID());

    m_rightMotorFollow.setInverted(InvertType.FollowMaster);

    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightMotorLead.setInverted(true);

    // m_robotDrive = new DifferentialDrive(m_leftMotorLead::set,
    // m_rightMotorLead::set);
    m_robotDrive = new DifferentialDrive(m_leftMotorLead, m_rightMotorLead);
    SendableRegistry.addChild(m_robotDrive, m_leftMotorLead);
    SendableRegistry.addChild(m_robotDrive, m_rightMotorLead);

    /** This function is called periodically during operator control. */

    // Initialize range readings on SmartDashboard as max distance in Centimeters.
    SmartDashboard.putNumber("Sensor 1 Range", 500);

    CameraServer.startAutomaticCapture();

  }

  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
    moveTimer.start();
    shootTimer.start();

  }

  public void simpleAuto() {
    // the shooter spins for 0.2 second and after that, the intake wheels start
    // moving as the shooter keeps spinning
    if (shootTimer.get() == 0) {
      shootTimer.start();
    }

    // // MOVE BEFORE SHOOTING
    // if (shootTimer.get() != 0 && shootTimer.get() < 1.2) {
    // // Drives forward for 1.2 sec to move 3 1/2 feet
    // m_robotDrive.tankDrive(.5, .5);
    // } else {
    // m_robotDrive.tankDrive(0, 0);
    // }

    // SHOOT
    shootingSequence(fullSpeed, 1);

    // MOVE AFTER SHOOTING
    if (moveTimer.get() > 7 && moveTimer.get() < 9.6) {
      m_robotDrive.tankDrive(.5, .5);
    } else {
      m_robotDrive.tankDrive(0, 0);
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      // Put custom auto code here
      case autoOption1:
        if (shootTimer.get() == 0) {
          shootTimer.start();
        }
        shootingSequence(1, 0);
        break;
      case autoOption2:
        simpleAuto();
        break;
      case autoOption3:
        twoNoteSequence();
        break;
      case autoDefault:
      default:
        motorOff();
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    shootTimer.reset();
    SmartDashboard.putString("DB/String 1", "  ");
  }

  @Override
  public void teleopPeriodic() {
    /*
     * Drive with tank drive.
     * Left trigger moves the robot forward
     * Right trigger moves the robot backwards
     * When you push the right Y axis up it turns the robot to the right
     * When you push the right Y axis down it turns the robot to the left
     * 
     */
    // if (m_driverController.getLeftTriggerAxis() > 0) {
    // m_robotDrive.tankDrive(-m_driverController.getLeftTriggerAxis(),
    // -m_driverController.getLeftTriggerAxis());
    // } else if (m_driverController.getRightTriggerAxis() > 0) {
    // m_robotDrive.tankDrive(m_driverController.getRightTriggerAxis(),
    // m_driverController.getRightTriggerAxis());
    // } else if (m_driverController.getRightY() > 0) {
    // m_robotDrive.tankDrive(-m_driverController.getRightY(),
    // m_driverController.getRightY());
    // } else if (m_driverController.getRightY() < 0) {
    // m_robotDrive.tankDrive(-m_driverController.getRightY(),
    // m_driverController.getRightY());
    // } else
    // m_robotDrive.tankDrive(0, 0);

    if (m_driverController.getLeftTriggerAxis() > 0) {
      m_robotDrive.tankDrive(-m_driverController.getLeftTriggerAxis(), -m_driverController.getLeftTriggerAxis());
    }
    if (m_driverController.getRightTriggerAxis() > 0) {
      m_robotDrive.tankDrive(m_driverController.getRightTriggerAxis(), m_driverController.getRightTriggerAxis());
    }
    if (m_driverController.getRightY() != 0) {
      m_robotDrive.tankDrive(-m_driverController.getRightY(), m_driverController.getRightY());
    }
    if (m_driverController.getLeftTriggerAxis() <= 0 &&
        m_driverController.getRightTriggerAxis() <= 0 &&
        m_driverController.getRightY() == 0) {
      m_robotDrive.tankDrive(0, 0);
    }

    // uses triggers for arm intake
    if (m_coDriverController.getLeftBumperPressed()) {
      intakeLittleWheels.set(-0.7);
    } else if (m_coDriverController.getRightBumperPressed()) {
      intakeLittleWheels.set(0.7);
    } else if (m_coDriverController.getRightBumperReleased() || m_coDriverController.getLeftBumperReleased()) {
      // if nothing pressed, dont move
      intakeLittleWheels.set(0);
    }

    // this will be for intaking the shooter
    if (m_coDriverController.getXButtonPressed()) {
      leftShooter.set(ControlMode.PercentOutput, -fullSpeed);
      rightShooter.set(-fullSpeed);
      // intakeLittleWheels.set(-1);
    } else if (m_coDriverController.getXButtonReleased()) {
      leftShooter.set(ControlMode.PercentOutput, 0);
      rightShooter.set(0);
    }
    // this will be for ejecting shooter fast
    SmartDashboard.putString("DB/String 1", Double.toString(shootTimer.get()));

    if (m_coDriverController.getYButton()) {
      // the shooter spins for 0.2 second and after that, the intake wheels start
      // moving as the shooter keeps spinning
      if (shootTimer.get() == 0) {
        shootTimer.start();
      }
      shootingSequence(fullSpeed, 0);
    } else if (m_coDriverController.getYButtonReleased()) {
      motorOff();
      shootCompleted = false;
    }

    // this will be for ejecting shooter slow
    else if (m_coDriverController.getBButtonPressed()) {
      leftShooter.set(ControlMode.PercentOutput, minSpeed);
      rightShooter.set(minSpeed);
    } else if (m_coDriverController.getBButtonReleased()) {
      leftShooter.set(ControlMode.PercentOutput, 0);
      rightShooter.set(0);
    } else if (m_coDriverController.getAButtonPressed()) {
      leftShooter.set(ControlMode.PercentOutput, fullSpeed);
      rightShooter.set(fullSpeed);
    } else if (m_coDriverController.getAButtonReleased()) {
      leftShooter.set(ControlMode.PercentOutput, 0);
      rightShooter.set(0);
    }

    if (m_coDriverController.getLeftY() < 0 && !(leftLimitSwitch.get())) {
      intakeMover.set(-0.7);
    } else if (m_coDriverController.getLeftY() > 0 && !(rightLimitSwitch.get())) {
      intakeMover.set(0.7);
    } else if (m_coDriverController.getLeftY() == 0) { // if nothing pressed, don't move
      intakeMover.set(0);
    }

    // // uses triggers if note gets stuck
    // if (m_coDriverController.getLeftTriggerAxis() > 0) {
    // intakeLittleWheels.set(1);
    // } else if (m_coDriverController.getLeftTriggerAxis() <= 0) {
    // intakeLittleWheels.set(0);
    // }
    // if (m_driverController.getLeftY() < 0)
    // intakeMover.set(-0.5);
    // } else if (m_driverController.getLeftY() > 0 && !(rightLimitSwitch.get())) {
    // intakeMover.set(0.5);
    // } else if (m_driverController.getLeftY() == 0) { // if nothing pressed, don't
    // move
    // intakeMover.set(0);
    // }

    // BRINGS ROBOT UP
    // if (m_driverController.getLeftBumperPressed()) {
    // m_climbRight.set(ControlMode.PercentOutput, 0.7);
    // m_climbLeft.set(0.7);
    // } else if (m_driverController.getLeftBumperReleased()) { // if nothing
    // pressed, left doesn't move
    // m_climbRight.set(ControlMode.PercentOutput, 0);
    // m_climbLeft.set(0);
    // }
    // if (m_driverController.getRightBumperPressed()) {
    // m_climbRight.set(ControlMode.PercentOutput, -0.7);
    // m_climbLeft.set(-0.7);
    // } else if (m_driverController.getRightBumperReleased()) { // if nothnig
    // pressed, right doesn't move
    // m_climbRight.set(ControlMode.PercentOutput, 0);
    // m_climbLeft.set(0);
    // }

  }

  public void motorOff() {
    SmartDashboard.putString("DB/String 2", "button released");
    leftShooter.set(ControlMode.PercentOutput, 0);
    rightShooter.set(0);
    intakeLittleWheels.set(0);
    shootTimer.reset();
  }

  public void shootingSequence(double shootingSpeed, double timeStart) {
    SmartDashboard.putString("DB/String 4", Boolean.toString(shootCompleted));
    m_robotDrive.feed(); // Avoid getting "Output not updated often enough" errors

    if (shootCompleted == false) {
      // Spin the shooter for 3 sec
      if ((shootTimer.get() != timeStart) && (shootTimer.get() < timeStart + 3)) {
        leftShooter.set(ControlMode.PercentOutput, shootingSpeed);
        rightShooter.set(shootingSpeed);
        SmartDashboard.putString("DB/String 2", "Less than 1");
      }
      // After 3 sec the intake wheel feeds note into shooter for another 3 sec
      else if ((shootTimer.get() >= timeStart + 3) && (shootTimer.get() < timeStart + 5)) {
        intakeLittleWheels.set(shootingSpeed);
        leftShooter.set(ControlMode.PercentOutput, shootingSpeed);
        rightShooter.set(shootingSpeed);
        SmartDashboard.putString("DB/String 2", "between 1 and 5");
      }
      // After 6 sec or by deafult stop all motors
      else if (shootTimer.get() >= timeStart + 5) {
        motorOff();
        shootCompleted = true;
      }
    }
  }

  public void twoNoteSequence() {
    SmartDashboard.putString("DB/String 6", "  ");
    m_robotDrive.feed(); // Avoid getting "Output not updated often enough" errors
    if (shootTimer.get() == 0) {
      shootTimer.start();
    }
    shootingSequence(fullSpeed, 1); // goes through one note sequence

    if (shootCompleted == true) {
      twoNoteTimer.start();
      SmartDashboard.putString("DB/String 5", "  ");

      // Drives forward for 1.9 sec to move ~53 in
      if ((twoNoteTimer.get() >= 0) && (twoNoteTimer.get() < 2.4)) {
        if (!rightLimitSwitch.get()) {
          intakeMover.set(0.7);
        }
        intakeLittleWheels.set(-0.7);
        m_robotDrive.tankDrive(.475, .475);
        SmartDashboard.putString("DB/String 7", "drove forward");
      } else if ((twoNoteTimer.get() >= 2.4) && (twoNoteTimer.get() < 3.5)) {
        if (!leftLimitSwitch.get()) {
          intakeMover.set(-0.7);
        }
        // after moving, the robot stops and brings the intake up
        SmartDashboard.putString("DB/String 8", "stopped");
        m_robotDrive.tankDrive(0, 0);
        intakeLittleWheels.set(0);
      } else if (twoNoteTimer.get() > 3.5 && twoNoteTimer.get() < 5.6) {
        // Drives back to the subwoofer while warming up the wheels to shoot
        m_robotDrive.tankDrive(-.6, -.6);
        leftShooter.set(ControlMode.PercentOutput, 1);
        rightShooter.set(0.9);
      } else if (twoNoteTimer.get() >= 5.6 && twoNoteTimer.get() < 7.6) { // the robot stops driving and continues to
                                                                          // warm
        // up the wheels
        m_robotDrive.tankDrive(0, 0);
        leftShooter.set(ControlMode.PercentOutput, 1);
        rightShooter.set(1);
      } else if (twoNoteTimer.get() >= 7.6 && twoNoteTimer.get() < 9.6) { // intake feeds the note into the shooter
        intakeLittleWheels.set(0.5);
        leftShooter.set(ControlMode.PercentOutput, 1);
        rightShooter.set(1);
      } else if (twoNoteTimer.get() >= 9.6) { // turns off intake and shooter
        intakeLittleWheels.set(0);
        leftShooter.set(ControlMode.PercentOutput, 0);
        rightShooter.set(0);
        twoNoteShootCompleted = true;
      }
    }
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics that you want ran during disabled, autonomous, teleoperated
   * and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }
}
