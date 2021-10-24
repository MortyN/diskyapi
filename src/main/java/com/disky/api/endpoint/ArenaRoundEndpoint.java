package com.disky.api.endpoint;

import com.disky.api.Exceptions.ArenaException;
import com.disky.api.Exceptions.ArenaRoundException;
import com.disky.api.controller.ArenaController;
import com.disky.api.controller.ArenaRoundController;
import com.disky.api.filter.ArenaFilter;
import com.disky.api.filter.ArenaRoundFilter;
import com.disky.api.model.Arena;
import com.disky.api.model.ArenaRound;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/arenaround")
@RestController
@CrossOrigin
public class ArenaRoundEndpoint {

    @PostMapping("/create")
    public ArenaRound create(@RequestBody(required = true) ArenaRound arenaRound) throws ArenaRoundException {
        ArenaRoundController.create(arenaRound);
        return arenaRound;
    }

    @DeleteMapping()
    public static void delete(@RequestParam(required = true)  Long arenaRoundId) throws ArenaRoundException {
        ArenaRoundController.delete(new ArenaRound(arenaRoundId));
    }

    @PostMapping("/get")
    public static List<ArenaRound> get(@RequestBody(required = true) ArenaRoundFilter arenaRoundFilter) throws  ArenaRoundException {
        return ArenaRoundController.get(arenaRoundFilter);
    }
}