package Barbearia.agendamento.Repository;

import Barbearia.agendamento.informacoes.agendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface agendaRepository extends MongoRepository<agendamento, String> {
    boolean existsByDataHora(LocalDateTime dataHora);

    @Query("{ '$or': [ { 'nome': { '$regex': ?0, '$options': 'i' } }, " +
            "{ 'servicos': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<agendamento> buscarPorKeyword(String keyword, Pageable pageable);

}
