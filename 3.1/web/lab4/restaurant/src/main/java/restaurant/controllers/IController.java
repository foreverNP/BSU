package restaurant.controllers;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.web.IWebExchange;
import java.io.Writer;

public interface IController {
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
            throws Exception;
}
