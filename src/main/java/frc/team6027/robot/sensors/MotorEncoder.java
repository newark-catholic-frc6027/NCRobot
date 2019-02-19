package frc.team6027.robot.sensors;

import edu.wpi.first.wpilibj.PIDSource;

public interface MotorEncoder<T> extends PIDSource {

    void markPosition();
    void resetMark();
    void reset();
    double getLastMarkPosition();
    double getPosition();
    /**
     * Distance since mark
     */
    double getDistance();

    double getVelocity();


/*
    public static class MotorEncoderError<E> {
        String errorMessage;
        Throwable exception;
        E encoderError;

        public MotorEncoderError(String error) {
            this.errorMessage = error;
        }

        public MotorEncoderError(String error, E encoderError) {
            this.errorMessage = error;
            this.encoderError = encoderError;
        }

        public MotorEncoderError(String error, Throwable ex) {
            this.errorMessage = error;
            this.exception = ex;

        }

        public MotorEncoderError(String error, E encoderError, Throwable ex) {
            this.errorMessage = error;
            this.encoderError = encoderError;
            this.exception = ex;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public void setErrorMessage(String err) {
            this.errorMessage = err;
        }

        public Throwable getException() {
            return this.exception;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
        }

        public E getEncoderError() {
            return encoderError;
        }

        public void setEncoderError(E error) {
            this.encoderError = error;
        }

    }
    */
}