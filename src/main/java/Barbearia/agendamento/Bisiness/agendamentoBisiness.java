package Barbearia.agendamento.Bisiness;

import Barbearia.agendamento.DTOs.criarAgendaDTO;
import Barbearia.agendamento.Repository.agendaRepository;
import Barbearia.agendamento.informacoes.agendamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class agendamentoBisiness {

    @Autowired
    private agendaRepository agendaRepository;

    public agendamento save(criarAgendaDTO dto) {
        agendamento agendar = new agendamento();
        agendar.setId(UUID.randomUUID().toString());
        agendar.setNome(dto.getNome());
        agendar.setDataHora(LocalDateTime.parse(dto.getDataHora()));
        agendar.setTelefone(dto.getTelefone());
        agendar.setServicos(dto.getServicos());

        if (agendaRepository.existsByDataHora(agendar.getDataHora())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Horario ja preenchido.");
        }
        return agendaRepository.save(agendar);
    }

    public Optional<agendamento> findById(String id) {
        return agendaRepository.findById(id);
    }

    public agendamento atualizar(String id, criarAgendaDTO dadosAtualizados) {
        agendamento agendamentoExistente = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento nao encontrado."));

        if (dadosAtualizados.getNome() != null) {
            agendamentoExistente.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getTelefone() != null) {
            agendamentoExistente.setTelefone(dadosAtualizados.getTelefone());
        }
        if (dadosAtualizados.getDataHora() != null) {
            agendamentoExistente.setDataHora(LocalDateTime.parse(dadosAtualizados.getDataHora()));
        }
        if (dadosAtualizados.getServicos() != null) {
            agendamentoExistente.setServicos(dadosAtualizados.getServicos());
        }
        return agendaRepository.save(agendamentoExistente);
    }
}
