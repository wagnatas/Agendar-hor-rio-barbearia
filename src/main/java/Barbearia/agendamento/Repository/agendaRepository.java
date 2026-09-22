package Barbearia.agendamento.Repository;

import Barbearia.agendamento.informacoes.agendamento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface agendaRepository extends MongoRepository<agendamento, String> {
    boolean existsByHorario(LocalDateTime horario);
}
