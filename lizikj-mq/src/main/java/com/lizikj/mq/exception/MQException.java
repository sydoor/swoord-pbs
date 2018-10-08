package com.lizikj.mq.exception;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class MQException extends Exception {

    public MQException() {
        super();

    }

    public MQException(String message, Throwable cause,
                       boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

    }

    public MQException(String message, Throwable cause) {
        super(message, cause);

    }

    public MQException(String message) {
        super(message);

    }

    public MQException(Throwable cause) {
        super(cause);

    }


}
