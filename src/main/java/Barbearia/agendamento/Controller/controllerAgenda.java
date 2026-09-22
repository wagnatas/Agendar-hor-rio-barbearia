package Barbearia.agendamento.Controller;


import Barbearia.agendamento.informacoes.agendamento;
import Barbearia.agendamento.Bisiness.agendamentoBisiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/agenda")
public class controllerAgenda {
    @Autowired
    private agendamentoBisiness agendamentoBisiness;

    @PostMapping
    public agendamento agendar(@RequestBody agendamento agendar) {
        return agendamentoBisiness.save(agendar);
    }

    @GetMapping("/{id}")
    public Optional<agendamento> buscarPorId(@PathVariable String id) {
        return agendamentoBisiness.findById(id);
    }

    @PatchMapping("/{id}")
    public agendamento update(@PathVariable String id,
                              @RequestBody agendamento dadosatualizados) {
        return agendamentoBisiness.atualizar(id, dadosatualizados);
    }
}
