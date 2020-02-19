package com.pigmice.frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Controls {
    private interface DriverProfile {
        double driveSpeed();
        double turnSpeed();

        boolean shoot();
    }

    private interface OperatorProfile {
        boolean intake();
        boolean feed();

        boolean climbUp();
        boolean climbDown();
    }

    private class EasySMX implements DriverProfile, OperatorProfile {
        private final Joystick joystick;

        public EasySMX(Joystick joystick) {
            this.joystick = joystick;
        }

        @Override
        public double driveSpeed() {
            return joystick.getRawAxis(1);
        }

        @Override
        public double turnSpeed() {
            return joystick.getRawAxis(2);
        }

        @Override
        public boolean intake() {
            return joystick.getRawButton(3);
        }

        @Override
        public boolean shoot() {
            return joystick.getRawButton(8);
        }

        @Override
        public boolean feed() {
            return joystick.getRawButton(6);
        }

        @Override
        public boolean climbUp() {
            return joystick.getRawButton(1);
        }

        @Override
        public boolean climbDown() {
            return joystick.getRawButton(2);
        }
    }

    private class XBox implements DriverProfile, OperatorProfile {
        private final Joystick joystick;

        public XBox(Joystick joystick) {
            this.joystick = joystick;
        }

        @Override
        public double driveSpeed() {
            return joystick.getRawAxis(1);
        }

        @Override
        public double turnSpeed() {
            return joystick.getRawAxis(4);
        }

        @Override
        public boolean intake() {
            return joystick.getRawButton(3);
        }

        @Override
        public boolean shoot() {
            return joystick.getRawAxis(3) > 0.75;
        }

        @Override
        public boolean feed() {
            return joystick.getRawButton(6);
        }

        @Override
        public boolean climbUp() {
            return joystick.getRawButton(1);
        }

        @Override
        public boolean climbDown() {
            return joystick.getRawButton(2);
        }
    }

    DriverProfile driver;
    OperatorProfile operator;

    public Controls() {
        Joystick driverJoystick = new Joystick(0);
        Joystick operatorJoystick = new Joystick(1);

        if(driverJoystick.getName().equals("EasySMX CONTROLLER")) {
            driver = new EasySMX(driverJoystick);
        } else if(driverJoystick.getName().equals("Controller (XBOX 360 For Windows)")){
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
    }

    public void initialize() {
    }

    public void update() {
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
        return driver.shoot();
    }

    public boolean feed() {
        return operator.feed();
    }
}
