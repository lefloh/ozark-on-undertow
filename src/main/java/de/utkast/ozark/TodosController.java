package de.utkast.ozark;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.annotation.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * The controller for all todos.
 *
 * @author Florian Hirsch
 */
@Controller
@Path("todos")
public class TodosController {

    @Inject
    private Models models;

    @Inject
    private TodoService todoService;

    @GET
    public String get() {
        models.put("todos", todoService.list());
        return "todos.mustache";
    }

    @GET
    @Path("{id}")
    public Response getOne(@PathParam("id") long id) {
        Optional<Todo> todo = todoService.list().stream().filter(t -> t.getId() == id).findFirst();
        if (!todo.isPresent()) {
            return Response.status(404).build();
        }
        models.put("todo", todo.get());
        return Response.ok("todo.mustache").build();
    }

}
