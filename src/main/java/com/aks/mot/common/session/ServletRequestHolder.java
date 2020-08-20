package com.aks.mot.common.session;

import javax.servlet.http.HttpServletRequest;

public class ServletRequestHolder {

    private static ThreadLocal<HttpServletRequest> holder = new ThreadLocal<HttpServletRequest>();

    private static HttpServletRequest httpRequest;

    public static HttpServletRequest get() {
        HttpServletRequest httpServletRequest = holder.get();

        if ( httpServletRequest == null ) {
            return ServletRequestHolder.httpRequest;
        }

        return httpServletRequest;
    }

    public static void hold(HttpServletRequest request) {
        holder.set(request);
    }

    public static void unhold() {
        holder.remove();
    }

}
