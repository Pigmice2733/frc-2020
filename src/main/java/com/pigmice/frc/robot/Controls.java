package com.pigmice.frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Controls {
    private interface JoystickProfile {
        int demoModeButton();
        int driveSpeedAxis();
        int turnSpeedAxis();
        int intakeButton();
    }

    private class EasySMX implements JoystickProfile {
        public int demoModeButton() {
            return 5;
        }

        public int driveSpeedAxis() {
            return 1;
        }

        public int turnSpeedAxis() {
            return 2;
        }

        public int intakeButton() {
            return 3;
        }
    }

    private class XBoxProfile implements JoystickProfile {
        public int demoModeButton() {
            return 5;
        }

        public int driveSpeedAxis() {
            return 1;
        }

        public int turnSpeedAxis() {
            return 4;
        }

        public int intakeButton() {
            return 3;
        }
    }

    Joystick joystick = new Joystick(0);
    JoystickProfile profile;

    public Controls() {
        if(joystick.getName().equals("EasySMX CONTROLLER")) {
            profile = new EasySMX();
        } else if(joystick.getName().equals("Controller (XBOX 360 For Windows)")){
            profile = new XBoxProfile();
        } else {
            profile = new XBoxProfile();
        }
    }

    public void initialize() {
    }

    public void update() {
    }

    public double turnSpeed() {
        final double steering = joystick.getRawAxis(profile.turnSpeedAxis());
        return Math.pow(steering, 2) * Math.signum(steering);
    }

    public double driveSpeed() {
        return -joystick.getRawAxis(profile.driveSpeedAxis());
    }

    public boolean demoMode() {
        return !joystick.getRawButton(profile.demoModeButton());
    }

    public boolean intake() {
        return joystick.getRawButton(profile.intakeButton());
    }
}
