package de.utkast.ozark;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.annotation.Controller;
import javax.mvc.annotation.View;
import javax.mvc.binding.BindingResult;
import javax.validation.Valid;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * A TodoController.
 */
@Path("todo")
@Controller
public class TodoController {

    @Inject
    private Models models;

    @Inject
    private BindingResult br;

    @Inject
    private TodoService todoService;

    @GET
    public String get() {
        return "todo-form.mustache";
    }

    @POST
    public String post(@Valid @BeanParam Todo todo) {
        if (br.isFailed()) {
            models.put("errors", br.getAllMessages());
            return "todo-form.mustache";
        }
        todoService.add(todo);
        return "redirect:todos";
    }

}
