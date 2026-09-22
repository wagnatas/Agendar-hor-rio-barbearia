package Barbearia.agendamento.Repository;

import Barbearia.agendamento.informacoes.agendamento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface agendaRepository extends MongoRepository<agendamento, String> {
    boolean existsByDataAndHora(LocalDate data, LocalTime hora);

    boolean existsByDataAndHoraAndIdNot(LocalDate data, LocalTime hora, String id);
}
