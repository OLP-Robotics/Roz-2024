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
   * for any
   * initialization code.
   */
  private final WPI_VictorSPX m_leftMotorLead = new WPI_VictorSPX (2);
  private final WPI_VictorSPX m_leftMotorFollow = new WPI_VictorSPX (10);
  private final WPI_VictorSPX m_rightMotorLead = new WPI_VictorSPX (1);
  private final WPI_VictorSPX m_rightMotorFollow = new WPI_VictorSPX (5);

  private final TalonSRX talMotorL1 = new TalonSRX(8);
  private final WPI_VictorSPX vicMotorR1 = new WPI_VictorSPX(3);

  private DifferentialDrive m_robotDrive;

  private final XboxController m_driverController = new XboxController(0);
  private  XboxController m_coDriverController = new XboxController(1);

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
  }
  
@Override
  public void teleopPeriodic() {
    /*  Drive with tank drive.
    Left trigger moves the robot forward
    Right trigger moves the robot backwards
    When you push the right X axis up it turns the robot to the right 
    When you push the right X axis down it turns the robot to the left 
  `*/
  if(m_driverController.getLeftTriggerAxis()>0) {
    m_robotDrive.tankDrive(-m_driverController.getLeftTriggerAxis(),-m_driverController.getLeftTriggerAxis());
  } 
  if(m_driverController.getRightTriggerAxis()>0) {
    m_robotDrive.tankDrive(m_driverController.getRightTriggerAxis(),m_driverController.getRightTriggerAxis());
  } 
  if(m_driverController.getRightY()>0) {
    m_robotDrive.tankDrive(-m_driverController.getRightY(),m_driverController.getRightY());
  } 
  if(m_driverController.getRightY()<0) {
    m_robotDrive.tankDrive(-m_driverController.getRightY(),m_driverController.getRightY());
  } 
   // this will be for intaking
  if (m_coDriverController.getXButtonPressed()) {
    talMotorL1.set(ControlMode.PercentOutput,-.4);
    vicMotorR1.set(.4);
  } else if (m_coDriverController.getXButtonReleased()) {
    talMotorL1.set(ControlMode.PercentOutput,0);
    vicMotorR1.set(0);
  }
  // this will be for ejecting fast
    else if (m_coDriverController.getYButtonPressed()) {
    talMotorL1.set(ControlMode.PercentOutput,1);
    vicMotorR1.set(-1);
  } else if (m_coDriverController.getYButtonReleased()) {
    talMotorL1.set(ControlMode.PercentOutput,0);
    vicMotorR1.set(0);
  }
   // this will be for ejecting slow
    else if (m_coDriverController.getBButtonPressed()) {
    talMotorL1.set(ControlMode.PercentOutput,0.5);
    vicMotorR1.set(-0.5);
  } else if (m_coDriverController.getBButtonReleased()) {
    talMotorL1.set(ControlMode.PercentOutput,0);
    vicMotorR1.set(0);
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