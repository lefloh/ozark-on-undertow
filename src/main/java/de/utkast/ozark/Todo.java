package de.utkast.ozark;

import javax.mvc.annotation.RedirectScoped;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.io.Serializable;

/**
 * A simple todo.
 *
 * @author Florian Hirsch
 */
@RedirectScoped
public class Todo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotNull
    @Size(min = 2)
    @FormParam("task")
    private String task;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return String.format("Todo {id: %d, task: %s}", id, task);
    }

}
