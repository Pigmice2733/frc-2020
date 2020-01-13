package com.pigmice.frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    @Override
    public void robotInit() {
        CameraServer.getInstance().startAutomaticCapture("Driver Cam", 0);

        Vision.startProcessing();
    }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.putNumber("Target angle", Vision.getAngle());
        SmartDashboard.putNumber("Target distance", Vision.getDistance());
    }
}
