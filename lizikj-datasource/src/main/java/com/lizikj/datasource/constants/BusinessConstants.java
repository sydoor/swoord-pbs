package com.lizikj.datasource.constants;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class BusinessConstants {
    public static final String READ_AOP = "execution(* com.lizikj..*.service..*.find*(..)) "
            + "or execution(* com.lizikj..*.service..*.get*(..)) "
            + "or execution(* com.lizikj..*.service..*.load*(..)) "
            + "or execution(* com.lizikj..*.service..*.query*(..)) "
            + "or execution(* com.lizikj..*.service..*.select*(..)) "
            + "or execution(* com.lizikj..*.service..*.list*(..)) ";

    public static final String WRITE_AOP = "execution(* com.lizikj.*.service..*.insert*(..)) "
            + "or execution(* com.lizikj..*.service..*.save*(..)) "
            + "or execution(* com.lizikj..*.service..*.modify*(..)) "
            + "or execution(* com.lizikj..*.service..*.add*(..)) "
            + "or execution(* com.lizikj..*.service..*.del*(..)) "
            + "or execution(* com.lizikj..*.service..*.real*(..)) "
            + "or execution(* com.lizikj..*.service..*.retreat*(..)) "
            + "or execution(* com.lizikj..*.service..*.register*(..)) "
            + "or execution(* com.lizikj..*.service..*.record*(..)) "
            + "or execution(* com.lizikj..*.service..*.lock*(..)) ";

    public static final String READ_METHOD = "^(find|get|load|query|select|list).*$";

    public static final String WRITE_METHOD = "^(modify|insert|update|modify|save|add|del|real|retreat|register|record|lock).*$";


}
