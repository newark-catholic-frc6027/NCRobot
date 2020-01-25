package frc.team6027.robot.sensors;

public interface MotorPIDController<T> {

    double getP();
    boolean setP(double gain);

    double getI();
    boolean setI(double gain);

    double getIZone();
    boolean setIZone(double izone);

    double getD();
    boolean setD(double gain);

    default void setPID(double p, double i, double d) {
        this.setP(p);
        this.setI(i);
        this.setD(d);
    }

    double getFF();
    boolean setFF(double gain);

    boolean setOutputRange(double min, double max);
    double	getOutputMax();	
    double	getOutputMin();

    boolean setSetpoint(double setpoint);
    double getSetpoint();

}