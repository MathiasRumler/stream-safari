package mvp.streamy.controller;

import java.util.Date;

public record CustomErrorResponse( String exception, String message, Date date) {

    public CustomErrorResponse( String exception, String message) {
        this( exception, message, new Date());
    }
}
