package com.pigmice.frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Controls {
    private interface ControllerProfile {
        boolean demoMode();
        double driveSpeed();
        double turnSpeed();
        boolean intake();
        boolean shoot();
        boolean feed();
    }

    private class EasySMX implements ControllerProfile {
        private final Joystick joystick;

        public EasySMX(Joystick joystick) {
            this.joystick = joystick;
        }

        @Override
        public boolean demoMode() {
            return joystick.getRawButton(5);
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
    }

    private class XBox implements ControllerProfile {
        private final Joystick joystick;

        public XBox(Joystick joystick) {
            this.joystick = joystick;
        }

        @Override
        public boolean demoMode() {
            return joystick.getRawButton(5);
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
    }

    ControllerProfile controller;

    public Controls() {
        Joystick joystick = new Joystick(0);

        if(joystick.getName().equals("EasySMX CONTROLLER")) {
            controller = new EasySMX(joystick);
        } else if(joystick.getName().equals("Controller (XBOX 360 For Windows)")){
            controller = new XBox(joystick);
        } else {
            controller = new XBox(joystick);
        }
    }

    public void initialize() {
    }

    public void update() {
    }

    public double turnSpeed() {
        final double steering = controller.turnSpeed();
        return Math.pow(steering, 2) * Math.signum(steering);
    }

    public double driveSpeed() {
        return -controller.driveSpeed();
    }

    public boolean demoMode() {
        return !controller.demoMode();
    }

    public boolean intake() {
        return controller.intake();
    }

    public boolean shoot() {
        return controller.shoot();
    }

    public boolean feed() {
        return controller.feed();
    }
}
