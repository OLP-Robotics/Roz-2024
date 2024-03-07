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
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
//Import required WPILib libraries
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

   /**
   * This function is run when the robot is first started up and should be used
   * initialization code.
   */
  private final WPI_VictorSPX m_leftMotorLead = new WPI_VictorSPX (2);
  private final WPI_VictorSPX m_leftMotorFollow = new WPI_VictorSPX (6);
  private final WPI_VictorSPX m_rightMotorLead = new WPI_VictorSPX (1);
  private final WPI_VictorSPX m_rightMotorFollow = new WPI_VictorSPX (5);
  
  private final TalonSRX m_climbRight = new TalonSRX(4);
  private final WPI_VictorSPX m_climbLeft = new WPI_VictorSPX(7);

  private final TalonSRX leftShooter = new TalonSRX(8);
  private final WPI_VictorSPX rightShooter = new WPI_VictorSPX(3);

  private final WPI_VictorSPX intakeLittleWheels = new WPI_VictorSPX(9);
  private final WPI_VictorSPX intakeMover = new WPI_VictorSPX(10);

  private DifferentialDrive m_robotDrive;

  private final XboxController m_driverController = new XboxController(0);
  private  XboxController m_coDriverController = new XboxController(1);

 // private DigitalInput rightLimitSwitch = new DigitalInput(1);
  private DigitalInput leftLimitSwitch = new DigitalInput(0);

  private final Timer mtimer = new Timer();
  private final Timer shootTimer = new Timer();

  

  //Create an instance of the AnalogInput class so we can read from it later 
  /*
  public DigitalOutput ultrasonicTriggerPinOne = new DigitalOutput(0);
  public AnalogInput ultrasonicSensorOne = new AnalogInput(0);
  public double ultrasonicSensorOneRange = 0;
  public double voltageScaleFactor = 1;
  public void turnOnSensorOne() {
    ultrasonicTriggerPinOne.set(true);
  }
  public void turnOffSensors() {
    ultrasonicTriggerPinOne.set(false);
  } */


  @Override
  public void robotInit() {
    SmartDashboard.putData("Auto choices", m_chooser);

    m_leftMotorLead.follow(m_leftMotorFollow);
    m_rightMotorLead.follow(m_rightMotorFollow);
    //m_robotDrive = new DifferentialDrive(m_leftMotorLead::set, m_rightMotorLead::set);
    m_robotDrive = new DifferentialDrive(m_leftMotorLead, m_rightMotorLead);
    SendableRegistry.addChild(m_robotDrive, m_leftMotorLead);
    SendableRegistry.addChild(m_robotDrive, m_rightMotorLead);

    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightMotorLead.setInverted(true);

    /** This function is called periodically during operator control. */

    //Initialize range readings on SmartDashboard as max distance in Centimeters.
    SmartDashboard.putNumber("Sensor 1 Range", 500);

    CameraServer.startAutomaticCapture();

  }

  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
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
    /*  Drive with tank drive.
    Left trigger moves the robot forward
    Right trigger moves the robot backwards
    When you push the right Y axis up it turns the robot to the right 
    When you push the right Y axis down it turns the robot to the left 
  `*/
  if(m_driverController.getLeftTriggerAxis()>0) {
    m_robotDrive.tankDrive(-m_driverController.getLeftTriggerAxis(),-m_driverController.getLeftTriggerAxis());
  } else if(m_driverController.getRightTriggerAxis()>0) {
    m_robotDrive.tankDrive(m_driverController.getRightTriggerAxis(),m_driverController.getRightTriggerAxis());
  } else if(m_driverController.getRightY()>0) {
    m_robotDrive.tankDrive(-m_driverController.getRightY(),m_driverController.getRightY());
  } else if(m_driverController.getRightY()<0) {
    m_robotDrive.tankDrive(-m_driverController.getRightY(),m_driverController.getRightY());
  } else m_robotDrive.tankDrive(0,0);

 //uses triggers for arm intake 
 if(m_coDriverController.getLeftBumperPressed()) {
    intakeLittleWheels.set(-1);
  } else if(m_coDriverController.getRightBumperPressed()) {
    intakeLittleWheels.set(1);
  } else if(m_coDriverController.getRightBumperReleased() || m_coDriverController.getLeftBumperReleased()) { //if nothing pressed, dont move
    intakeLittleWheels.set(0);
  }

   // this will be for intaking the shooter
  if (m_coDriverController.getXButtonPressed()) {
    leftShooter.set(ControlMode.PercentOutput,-.4);
    rightShooter.set(.4);
   // intakeLittleWheels.set(-1);
  } else if (m_coDriverController.getXButtonReleased()) {
    leftShooter.set(ControlMode.PercentOutput,0);
    rightShooter.set(0);
  }
  // this will be for ejecting shooter fast
    if (m_coDriverController.getYButton()) {
      SmartDashboard.putString("DB/String 1", Double.toString(shootTimer.get()));
  // the shooter spins for 0.2 second and after that, the intake wheels start moving as the shooter keeps spinning
      // shootTimer.reset();
      shootTimer.start();
      // Spin the shooter for 1 sec
    if(shootTimer.get() < 1) {
      leftShooter.set(ControlMode.PercentOutput,.4);
      rightShooter.set(-.4);
      SmartDashboard.putString("DB/String 2", "Less than 1");
      }
      // After 2 sec the intake wheel feeds note into shooter for another 3 sec
     else if(shootTimer.get() >= 1 && shootTimer.get() < 5 ) {
     intakeLittleWheels.set(-.4);
      leftShooter.set(ControlMode.PercentOutput,.4);
      rightShooter.set(-.4);
      SmartDashboard.putString("DB/String 2", "between 1 and 5");
      }
      // After 5 sec or by deafult stop all motors
   else if (shootTimer.get() >= 5) {
    SmartDashboard.putString("DB/String 2", "button released");
    leftShooter.set(ControlMode.PercentOutput,0);
    rightShooter.set(0);
    intakeLittleWheels.set(0);
    shootTimer.reset();
  }
}

    
   // this will be for ejecting shooter slow
    else if (m_coDriverController.getBButtonPressed()) {
    leftShooter.set(ControlMode.PercentOutput,0.4);
    rightShooter.set(-0.4);
  } else if (m_coDriverController.getBButtonReleased()) {
    leftShooter.set(ControlMode.PercentOutput,0);
    rightShooter.set(0);
  }

  if(m_coDriverController.getRightY()<0 && !(leftLimitSwitch.get())) {
    intakeMover.set(-0.5);
  } else if(m_coDriverController.getRightY()>0) {
    intakeMover.set(0.5);
  } else if (m_coDriverController.getRightY() == 0) { //if nothing pressed, don't move
    intakeMover.set(0);
  }

// BRINGS ROBOT UP
if (m_driverController.getLeftBumperPressed()){
//  m_climbRight.set(ControlMode.PercentOutput,0.7);
  m_climbLeft.set(0.7);
} else if (m_driverController.getLeftBumperReleased()){ //if nothing pressed, left doesn't move
 // m_climbRight.set(ControlMode.PercentOutput,0);
 m_climbLeft.set(0);
}
if (m_driverController.getRightBumperPressed()){
 // m_climbRight.set(ControlMode.PercentOutput,-0.7);
  m_climbLeft.set(-0.7);
} else if (m_driverController.getRightBumperReleased()){ //if nothnig pressed, right doesn't move
//  m_climbRight.set(ControlMode.PercentOutput,0);
 m_climbLeft.set(0);
}

  }


  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
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
