package ru.istu.b1978201.KSite.exceptions;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorPageController implements ErrorController {



    @Override
    public String getErrorPath() {
        return "/error";
    }
}
