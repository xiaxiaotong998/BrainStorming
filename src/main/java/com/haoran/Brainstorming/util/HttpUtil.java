package com.haoran.Brainstorming.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class HttpUtil {

    public static boolean isApiRequest(HttpServletRequest request) {
        return request.getHeader("Accept") == null || !request.getHeader("Accept").contains("text/html");
    }


    public static void responseWrite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!HttpUtil.isApiRequest(request)) {
            response.setContentType("text/html;charset=utf-8");
            response.sendRedirect("/login");
        } else  {
            response.setContentType("application/json;charset=utf-8");
            Result result = new Result();
            result.setCode(201);
            result.setDescription("login svp");
            response.getWriter().write(JsonUtil.objectToJson(result));
        }
    }
}
