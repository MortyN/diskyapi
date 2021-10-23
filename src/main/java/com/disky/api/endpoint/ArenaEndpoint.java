package com.disky.api.endpoint;

import com.disky.api.Exceptions.ArenaException;
import com.disky.api.Exceptions.PostControllerException;
import com.disky.api.controller.ArenaController;
import com.disky.api.controller.PostController;
import com.disky.api.filter.ArenaFilter;
import com.disky.api.filter.PostFilter;
import com.disky.api.model.Arena;
import com.disky.api.model.Post;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/arena")
@RestController
@CrossOrigin
public class ArenaEndpoint {

    @PostMapping(path="/create")
    public Arena create(@RequestBody(required = true) Arena arena) throws ArenaException {
        ArenaController.create(arena);
        return arena;
    }

    @DeleteMapping()
    public static Arena delete(@RequestParam(required = true)  Long arenaId) throws ArenaException {
        return ArenaController.changeActive(new Arena(arenaId));
    }

    @PostMapping("/get")
    public static List<Arena> get(@RequestBody(required = true) ArenaFilter arenaFilter) throws ArenaException {
        return ArenaController.get(arenaFilter);
    }
}
