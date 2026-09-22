package Barbearia.agendamento.Bisiness;

import Barbearia.agendamento.Repository.agendaRepository;
import Barbearia.agendamento.informacoes.agendamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class agendamentoBisiness {

    @Autowired
    private agendaRepository agendaRepository;

    public agendamento save(agendamento agendar) {
        agendar.setId(UUID.randomUUID().toString());
        if (agendaRepository.existsByDataAndHora(agendar.getData(), agendar.getHora())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Horario ja preenchido.");
        }
        return agendaRepository.save(agendar);
    }

    public Optional<agendamento> findById(String id) {
        return agendaRepository.findById(id);
    }

    public agendamento atualizar(String id, agendamento dadosAtualizados) {
        agendamento agendamentoExistente = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento nao encontrado."));

        if (dadosAtualizados.getNome() != null) {
            agendamentoExistente.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getTelefone() != null) {
            agendamentoExistente.setTelefone(dadosAtualizados.getTelefone());
        }
        if (dadosAtualizados.getData() != null || dadosAtualizados.getHora() != null) {
            LocalDate data = dadosAtualizados.getData() != null ? dadosAtualizados.getData() : agendamentoExistente.getData();
            LocalTime hora = dadosAtualizados.getHora() != null ? dadosAtualizados.getHora() : agendamentoExistente.getHora();

            if (agendaRepository.existsByDataAndHoraAndIdNot(data, hora, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Horario ja preenchido.");
            }
            agendamentoExistente.setData(data);
            agendamentoExistente.setHora(hora);
        }
        if (dadosAtualizados.getServicos() != null) {
            agendamentoExistente.setServicos(dadosAtualizados.getServicos());
        }
        return agendaRepository.save(agendamentoExistente);
    }
}
