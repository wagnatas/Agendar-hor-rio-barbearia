package Barbearia.agendamento.Bisiness;

import Barbearia.agendamento.DTOs.criarAgendaDTO;
import Barbearia.agendamento.Repository.agendaRepository;
import Barbearia.agendamento.informacoes.agendamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

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

    public Page<agendamento> listar(String keyword, Integer page, Integer size) {
        if ((page != null && page < 0) || (size != null && size < 1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "page deve ser maior ou igual a zero e size deve ser maior que zero.");
        }
        Sort sort = Sort.by("dataHora", "id");
        Pageable pageable = page == null && size == null
                ? Pageable.unpaged(sort)
                : PageRequest.of(page == null ? 0 : page, size == null ? 10 : size, sort);

        if (keyword == null || keyword.isBlank()) {
            return agendaRepository.findAll(pageable);
        }
        return agendaRepository.buscarPorKeyword(Pattern.quote(keyword.trim()), pageable);
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

    public ResponseEntity<Void> deletar(String id) {
        if (!agendaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento nao encontrado.");
        }

        agendaRepository.deleteById(id);
        return null;
    }
}
