package Barbearia.agendamento.Controller;


import Barbearia.agendamento.DTOs.criarAgendaDTO;
import Barbearia.agendamento.informacoes.agendamento;
import Barbearia.agendamento.Bisiness.agendamentoBisiness;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/agenda")
public class controllerAgenda {
    @Autowired
    private agendamentoBisiness agendamentoBisiness;

    @PostMapping
    public agendamento agendar(@Valid @RequestBody criarAgendaDTO dto) {
        return agendamentoBisiness.save(dto);
    }

    @GetMapping("/{id}")
    public Optional<agendamento> buscarPorId(@PathVariable String id) {
        return agendamentoBisiness.findById(id);
    }

    @PatchMapping("/atualizar/{id}")
    public agendamento update(@PathVariable String id,
                              @Valid @RequestBody criarAgendaDTO dto) {
        return agendamentoBisiness.atualizar(id, dto);
    }
}
