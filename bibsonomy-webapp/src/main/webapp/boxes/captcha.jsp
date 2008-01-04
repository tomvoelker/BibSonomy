    <%@page import="servlets.listeners.InitialConfigListener"%>
    <%@page import="net.tanesha.recaptcha.*" %>
    
    <%
        // create recaptcha without <noscript> tags
        ReCaptcha captcha = ReCaptchaFactory.newReCaptcha(InitialConfigListener.getInitParam("ReCaptchaPublicKey"), InitialConfigListener.getInitParam("ReCaptchaPrivateKey"), false);
        String captchaScript = captcha.createRecaptchaHtml(request.getParameter("error"), null);
        out.print(captchaScript);
    %>
    