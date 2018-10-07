package co.ppk.utilities;

import co.ppk.service.MeatadataBO;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class PaymentGatewayHelper {

    public static MeatadataBO getMetadata(HttpServletRequest request) {
        MeatadataBO.Builder metadataBO = new MeatadataBO.Builder();
        String remoteAddr = "";
        String xcookie = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        assert request != null;
        for (Cookie cookie: request.getCookies()) {
            xcookie = cookie.getName().equals("XSRF-TOKEN") ? cookie.getValue() : xcookie;
        }
        metadataBO.setRemoteIp(remoteAddr)
        .setSessionId(request.getRequestedSessionId())
        .setCookie(xcookie)
        .build();

        return metadataBO.setRemoteIp(remoteAddr)
                .setSessionId(request.getRequestedSessionId())
                .setCookie(xcookie)
                .setUserAgent(request.getHeader("User-Agent"))
                .build();
    }
}
