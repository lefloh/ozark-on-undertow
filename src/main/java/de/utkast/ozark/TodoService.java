package de.utkast.ozark;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple todo service.
 *
 * @author Florian Hirsch
 */
@ApplicationScoped
public class TodoService {

    private AtomicLong cnt = new AtomicLong(1);

    private List<Todo> todos = new ArrayList<>();

    public void add(Todo todo) {
        todo.setId(cnt.getAndIncrement());
        todos.add(todo);
    }

    public List<Todo> list() {
        return todos;
    }

}
