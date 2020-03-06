package com.pigmice.frc.robot;

import com.pigmice.frc.lib.inputs.Debouncer;
import com.pigmice.frc.lib.inputs.Toggle;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;

public class Controls {
    private interface DriverProfile {
        double driveSpeed();
        double turnSpeed();
        boolean visionAlign();
    }

    private interface OperatorProfile {
        boolean shoot();

        boolean intake();

        boolean feed();
        boolean backFeed();

        boolean extendHood();

        boolean climbUp();
        boolean climbDown();
    }

    private class EasySMX implements DriverProfile, OperatorProfile {
        private final XboxController joystick;

        public EasySMX(XboxController joystick) {
            this.joystick = joystick;
        }

        @Override
        public double driveSpeed() {
            return joystick.getY(Hand.kLeft);
        }

        @Override
        public double turnSpeed() {
            return joystick.getX(Hand.kRight);
        }

        @Override
        public boolean intake() {
            return joystick.getRawButton(8);
        }

        @Override
        public boolean visionAlign() {
            return joystick.getRawButton(8);
        }

        @Override
        public boolean shoot() {
            return joystick.getRawButton(7);
        }

        @Override
        public boolean feed() {
            return joystick.getBumper(Hand.kRight);
        }

        @Override
        public boolean backFeed() {
            return joystick.getBumper(Hand.kLeft);
        }

        @Override
        public boolean climbUp() {
            return joystick.getPOV() == 0;
        }

        @Override
        public boolean climbDown() {
            return joystick.getPOV() == 180;
        }

        @Override
        public boolean extendHood() {
            return joystick.getYButton();
        }
    }

    private class XBox implements DriverProfile, OperatorProfile {
        private final XboxController joystick;

        public XBox(XboxController joystick) {
            this.joystick = joystick;
        }

        @Override
        public double driveSpeed() {
            return joystick.getY(Hand.kLeft);
        }

        @Override
        public double turnSpeed() {
            return joystick.getX(Hand.kRight);
        }

        @Override
        public boolean intake() {
            return joystick.getTriggerAxis(Hand.kRight) > 0.5;
        }

        @Override
        public boolean visionAlign() {
            return joystick.getTriggerAxis(Hand.kRight) > 0.5;
        }

        @Override
        public boolean shoot() {
            return joystick.getTriggerAxis(Hand.kLeft) > 0.5;
        }

        @Override
        public boolean feed() {
            return joystick.getBumper(Hand.kRight);
        }

        @Override
        public boolean backFeed() {
            return joystick.getBumper(Hand.kLeft);
        }

        @Override
        public boolean climbUp() {
            return joystick.getPOV() == 0;
        }

        @Override
        public boolean climbDown() {
            return joystick.getPOV() == 180;
        }

        @Override
        public boolean extendHood() {
            return joystick.getYButton();
        }
    }

    DriverProfile driver;
    OperatorProfile operator;

    Debouncer debouncer;
    Toggle hoodToggle;

    public Controls() {
        XboxController driverJoystick = new XboxController(0);
        XboxController operatorJoystick = new XboxController(1);

        if (driverJoystick.getName().equals("EasySMX CONTROLLER")) {
            driver = new EasySMX(driverJoystick);
        } else if (driverJoystick.getName().equals("Controller (XBOX 360 For Windows)")) {
            driver = new XBox(driverJoystick);
        } else {
            driver = new XBox(driverJoystick);
        }

        if (operatorJoystick.getName().equals("EasySMX CONTROLLER")) {
            operator = new EasySMX(operatorJoystick);
        } else if (operatorJoystick.getName().equals("Controller (XBOX 360 For Windows)")) {
            operator = new XBox(operatorJoystick);
        } else {
            operator = new XBox(operatorJoystick);
        }

        debouncer = new Debouncer(operator::extendHood);
        hoodToggle = new Toggle(debouncer);
    }

    public void initialize() {
    }

    public void update() {
        debouncer.update();
        hoodToggle.update();
    }

    public double turnSpeed() {
        final double steering = driver.turnSpeed();
        return Math.pow(steering, 2) * Math.signum(steering);
    }

    public double driveSpeed() {
        return -driver.driveSpeed();
    }

    public boolean intake() {
        return operator.intake();
    }

    public boolean shoot() {
        return operator.shoot();
    }

    public boolean feed() {
        return operator.feed();
    }

    public boolean backFeed() {
        return operator.backFeed();
    }

    public boolean extendHood() {
        return hoodToggle.get();
    }

    public boolean climbUp() {
        return operator.climbUp();
    }

    public boolean climbDown() {
        return operator.climbDown();
    }

    public boolean visionAlign() {
        return driver.visionAlign();
    }
}
