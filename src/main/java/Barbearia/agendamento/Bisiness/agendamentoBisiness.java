package Barbearia.agendamento.Bisiness;

import Barbearia.agendamento.Repository.agendaRepository;
import Barbearia.agendamento.informacoes.agendamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class agendamentoBisiness {

    @Autowired
    private agendaRepository agendaRepository;

    public agendamento save(agendamento agendar) {
        agendar.setId(UUID.randomUUID().toString());
        LocalDateTime umaHoraAntes = agendar.getHorario().minusHours(1);
        LocalDateTime umaHoraDepois = agendar.getHorario().plusHours(1);

        if(agendaRepository.existsByHorario(agendar.getHorario())){
            throw new RuntimeException("Horario marcado");
        }
        return agendaRepository.save(agendar);
    }

    public Optional<agendamento> findById(String id) {
        return agendaRepository.findById(id);
    }

    public agendamento atualizar(String id, agendamento dadosAtualizados) {
        agendamento agendamentoExistente = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));
        if (dadosAtualizados.getNome() != null) {
            agendamentoExistente.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getTelefone() != null) {
            agendamentoExistente.setTelefone(dadosAtualizados.getTelefone());
        }
        if (dadosAtualizados.getHorario() != null) {
            agendamentoExistente.setHorario(dadosAtualizados.getHorario());
        }
        if (dadosAtualizados.getServicos() != null) {
            agendamentoExistente.setServicos(dadosAtualizados.getServicos());
            {
                throw new RuntimeException("Horario marcado!");
            }
}
        return agendaRepository.save(agendamentoExistente);
}
}